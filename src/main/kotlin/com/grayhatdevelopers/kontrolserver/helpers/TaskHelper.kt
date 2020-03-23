package com.grayhatdevelopers.kontrolserver.helpers

import com.grayhatdevelopers.kontrolserver.models.*
import com.grayhatdevelopers.kontrolserver.repository.Repository
import com.grayhatdevelopers.kontrolserver.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.litote.kmongo.*

class TaskHelper {

    suspend fun addTasks(newTasks: Array<Task>) {
        withContext(Dispatchers.IO) {
            val tasks = Repository.tasksCollection.find().toMutableList()
            val clients = Repository.clientsCollection.find().toMutableList()
            for (task in newTasks) {
                val temp: Task? = tasks.find {
                    task.taskID == it.taskID
                }
                if (temp != null) {
                    Logger.log("Task with id: ${task.taskID} already present, incrementing transference to: ${temp.transference + 1}")
                    task.transference += temp.transference + 1

                    // adjust client's balance
                    if (temp.shopName == task.shopName && temp.debit != task.debit) {
                        val client: Client? = clients.find {
                            it.name == temp.shopName
                        }
                        client?.let {
                            val newBalance = (client.balance - temp.debit) + task.debit
                            Repository.clientsCollection.updateOne(
                                Client::name eq it.name, setValue(Client::balance, newBalance)
                            )
                        }
                    }

                    // delete previously placed task with this same ID
                    Repository.tasksCollection.deleteOne(
                        Task::taskID eq temp.taskID
                    )
                }
                // insert this new task
                Repository.tasksCollection.insertOne(task)

                // create a new client if doesn't exist, else update the balance
                val client: Client? = clients.find {
                    it.name == task.shopName
                }
                if (client != null) {
                    // this client already exists, update balance
                    Repository.clientsCollection.updateOne(
                        Client::name eq client.name, setValue(Client::balance, client.balance + task.debit)
                    )

                } else {
                    // we don't have this client already
                    val newClient = Client(
                        name = task.shopName,
                        balance = task.debit,
                        paidAmount = 0.toLong()
                    )
                    Logger.log("Creating a new client: $newClient")
                    ClientsHelper.addClient(newClient)
                    clients.add(newClient)
                }
            }
        }
    }

    suspend fun approveTask(taskID: String) : Boolean {
        return withContext(Dispatchers.IO) {
            val updateResult = Repository.tasksCollection.updateOne(
                Task::taskID eq taskID, set(Task::taskStatus setTo TaskStatus.COMPLETED)
            )
            return@withContext updateResult.wasAcknowledged()
        }
    }

    suspend fun deleteTasks(deleteTasksRequest: DeleteTasksRequest) {
        withContext(Dispatchers.IO) {
            val ids = deleteTasksRequest.taskIds
            if (ids.isEmpty()) {
                val selectedTasks = getTasksByRequest(deleteTasksRequest)
                for (e in selectedTasks)
                    ids.add(e.taskID)
            }
            Repository.tasksCollection.deleteMany(
                Task::taskID `in` ids.toList()
            )
        }
    }

    suspend fun addPaymentToTask(payment: Payment) {
        withContext(Dispatchers.IO) {
            Repository.paymentsCollection.insertOne(payment)
            val taskStatus: TaskStatus = when (payment.verificationStatus) {
                VerificationStatus.ADMIN_PENDING -> {
                    TaskStatus.WAITING
                }
                VerificationStatus.ADMIN_VERIFIED -> {
                    TaskStatus.COMPLETED
                }
                VerificationStatus.CLIENT_VERIFIED -> {
                    TaskStatus.COMPLETED
                }
            }
            Repository.tasksCollection.updateOne(
                Task::taskID eq payment.taskID, set(Task::paymentId setTo  payment._id, Task::taskStatus setTo taskStatus)
            )
        }
    }

    suspend fun transferTask(taskTransferRequest: TaskTransferRequest) {
        withContext(Dispatchers.IO) {
            Repository.taskTransferRequestsCollection.insertOne(taskTransferRequest)
            Repository.tasksCollection.updateOne(
                Task::taskID eq taskTransferRequest.taskID, setValue(Task::rider, taskTransferRequest.newRider)
            )
        }
    }

    suspend fun editTask(editTaskRequest: EditTaskRequest) {
        withContext(Dispatchers.IO) {
            val taskID = editTaskRequest.taskID
            editTaskRequest.rider?.let {
                Repository.tasksCollection.updateOne(
                    Task::taskID eq taskID, setValue(Task::rider, it)
                )
            }
            editTaskRequest.shop?.let {
                Repository.tasksCollection.updateOne(
                    Task::taskID eq taskID, setValue(Task::shopName, it)
                )
            }
            editTaskRequest.company?.let {
                Repository.tasksCollection.updateOne(
                    Task::taskID eq taskID, setValue(Task::company, it)
                )
            }
            editTaskRequest.debit?.let {
                Repository.tasksCollection.updateOne(
                    Task::taskID eq taskID, setValue(Task::debit, it)
                )
            }
            editTaskRequest.date?.let {
                Repository.tasksCollection.updateOne(
                    Task::taskID eq taskID, setValue(Task::date, it)
                )
            }
        }
    }

    suspend fun getTasks(getTasksRequest: GetTasksRequest): MutableList<Task> = getTasksByRequest(getTasksRequest)

    suspend fun getPayments(paymentsRequest: PaymentsRequest): MutableList<Payment> =
        getPaymentsByRequest(paymentsRequest)

    suspend fun deletePayments(paymentsRequest: PaymentsRequest) {
        withContext(Dispatchers.IO) {
            val toDelete: MutableList<Payment> = getPaymentsByRequest(paymentsRequest)
            val ids = mutableListOf<String>().apply {
                for (e in toDelete)
                    add(e.taskID)
            }
            Repository.paymentsCollection.deleteMany(
                Payment::taskID `in` ids
            )
        }
    }

    private suspend fun getPaymentsByRequest(paymentsRequest: PaymentsRequest): MutableList<Payment> =
        withContext(Dispatchers.IO) {
            val ids = paymentsRequest.paymentIds
            val relatedTasks = getTasksByRequest(
                GetTasksRequest(
                    paymentsRequest.riders,
                    paymentsRequest.dates,
                    paymentsRequest.companies,
                    paymentsRequest.shops,
                    paymentsRequest.taskTypes,
                    paymentsRequest.tasksModels,
                    getAllTaskStatuses()
                )
            )
            for (e in relatedTasks)
                ids.add(e.taskID)

            val allPayments = Repository.paymentsCollection.find().toMutableList()

            val filteredPayments = mutableListOf<Payment>()

            for (payment in allPayments)
                if (ids.contains(payment.taskID))
                    filteredPayments.add(payment)

            return@withContext filteredPayments
        }

    private suspend fun getTasksByRequest(getTasksRequest: GetTasksRequest): MutableList<Task> =
        withContext(Dispatchers.IO) {
            with(getTasksRequest) {
                if (riders.isEmpty()) {
                    riders.addAll(getAllRiders())
                }
                if (dates.isEmpty()) {
                    dates.addAll(getAllDates())
                }
                if (companies.isEmpty()) {
                    companies.addAll(getAllCompanies())
                }
                if (shops.isEmpty()) {
                    shops.addAll(getAllClients())
                }
                if (taskTypes.isEmpty()) {
                    taskTypes.addAll(getAllTaskTypes())
                }
                if (tasksModels.isEmpty()) {
                    tasksModels.addAll(getAllTaskModels())
                }
                if (taskStatuses.isEmpty()) {
                    taskStatuses.addAll(getAllTaskStatuses())
                }
            }

            val tasks = Repository.tasksCollection.find().toMutableList()

            val filteredTasks = mutableListOf<Task>()

            with(getTasksRequest) {
                for (task in tasks) {
                    if (
                        riders.contains(task.rider) &&
                        dates.contains(task.date) &&
                        companies.contains(task.company) &&
                        shops.contains(task.shopName) &&
                        taskTypes.contains(task.taskType) &&
                        tasksModels.contains(task.taskModel) &&
                        taskStatuses.contains(task.taskStatus)
                    ) filteredTasks.add(task)
                }
            }

            return@withContext filteredTasks.toMutableList()
        }
}