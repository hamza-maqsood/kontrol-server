package com.grayhatdevelopers.kontrolserver.repository

import com.grayhatdevelopers.kontrolserver.config.DBConfig
import com.grayhatdevelopers.kontrolserver.models.*
import com.grayhatdevelopers.kontrolserver.utils.Constants
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase

object Repository {

    private lateinit var mongoDBInstance: MongoDatabase
    lateinit var tasksCollection: MongoCollection<Task>
    lateinit var usersCollection: MongoCollection<User>
    lateinit var ridersCollection: MongoCollection<Rider>
    lateinit var clientsCollection: MongoCollection<Client>
    lateinit var paymentsCollection: MongoCollection<Payment>
    lateinit var loginCredentials: MongoCollection<LoginCredentials>
    lateinit var taskTransferRequestsCollection: MongoCollection<TaskTransferRequest>
    lateinit var deletePaymentsRequestsCollection: MongoCollection<PaymentsRequest>
    lateinit var deleteTasksRequestsCollection: MongoCollection<DeleteTasksRequest>
    lateinit var updateClientBalanceUpdateRequestCollection: MongoCollection<ClientBalanceUpdateRequest>

    fun setupDB() {
        mongoDBInstance = DBConfig.setupKMongoInstance().getDatabase(Constants.DEFAULT_DATABASE)
        tasksCollection = mongoDBInstance.getCollection(Constants.TASKS_COLLECTION, Task::class.java)
        usersCollection = mongoDBInstance.getCollection(Constants.USERS_COLLECTION, User::class.java)
        ridersCollection = mongoDBInstance.getCollection(Constants.RIDERS_COLLECTION, Rider::class.java)
        clientsCollection = mongoDBInstance.getCollection(Constants.CLIENTS_COLLECTION, Client::class.java)
        paymentsCollection = mongoDBInstance.getCollection(Constants.PAYMENTS_COLLECTION, Payment::class.java)
        loginCredentials =
            mongoDBInstance.getCollection(Constants.LOGIN_CREDENTIALS_COLLECTION, LoginCredentials::class.java)
        taskTransferRequestsCollection =
            mongoDBInstance.getCollection(Constants.TASK_TRANSFER_REQUESTS_COLLECTION, TaskTransferRequest::class.java)
        deletePaymentsRequestsCollection = mongoDBInstance.getCollection(
            Constants.DELETE_PAYMENTS_REQUESTS_COLLECTION,
            PaymentsRequest::class.java
        )
        deleteTasksRequestsCollection =
            mongoDBInstance.getCollection(Constants.DELETE_TASKS_REQUESTS_COLLECTION, DeleteTasksRequest::class.java)
        updateClientBalanceUpdateRequestCollection =
            mongoDBInstance.getCollection(
                Constants.UPDATE_CLIENT_BALANCE_REQUEST_COLLECTION,
                ClientBalanceUpdateRequest::class.java
            )
    }

}