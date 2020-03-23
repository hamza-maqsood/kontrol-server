package com.grayhatdevelopers.kontrolserver.controllers

import com.grayhatdevelopers.kontrolserver.authentication.validateAdminTokens
import com.grayhatdevelopers.kontrolserver.authentication.validateModeratorTokens
import com.grayhatdevelopers.kontrolserver.authentication.validateUserTokens
import com.grayhatdevelopers.kontrolserver.data.Inventory
import com.grayhatdevelopers.kontrolserver.helpers.TaskHelper
import com.grayhatdevelopers.kontrolserver.models.*
import com.grayhatdevelopers.kontrolserver.utils.Constants
import com.grayhatdevelopers.kontrolserver.utils.Logger
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.request.ContentTransformationException
import io.ktor.request.header
import io.ktor.request.receive
import io.ktor.response.respond
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TaskController(
    private val mTaskHelper: TaskHelper
) {
    suspend fun addTasks(context: ApplicationCall) {
        val token = context.request.header(Constants.TOKEN_RESPONSE)
        Logger.log("AddTasks request received with token: $token")
        try {
            val isUserAuthorized = validateModeratorTokens(token)
            Logger.log("admin actual token: ${Inventory.ADMIN_TOKEN}")
            if (isUserAuthorized) {
                Logger.log("AddTasks request token is valid: $token")
                context.receive<Array<Task>>().also {
                    Logger.log("Adding tasks to database: $it")
                    mTaskHelper.addTasks(it)
                }
                context.respond(HttpStatusCode.Accepted, "Tasks Uploaded!")
            } else {
                Logger.log("AddTasks request with invalid token: $token")
                context.respond(HttpStatusCode.Unauthorized, "Only ADMIN or MODERATORS can add a TASK!")
            }
        } catch (e: Exception) {
            if (e is ContentTransformationException) {
                Logger.log("Cannot transform request data to a ListOf[Task] object: ${e.message}")
                context.respond(HttpStatusCode.NotAcceptable, "Invalid request data format!")
            } else {
                Logger.log("AddTasks request can't be proceeded: ${e.message}")
                context.respond(HttpStatusCode.BadRequest, "Invalid request!")
            }
        }
    }

    suspend fun deleteTasks(context: ApplicationCall) {
        val token = context.request.header(Constants.TOKEN_RESPONSE)
        Logger.log("DeleteTasks request received with token: $token")
        val isUserAuthorized = validateModeratorTokens(token)
        if (isUserAuthorized) {
            Logger.log("DeleteTasks request token is valid: $token")
            try {
                context.receive<DeleteTasksRequest>().also {
                    Logger.log("Deleting tasks: $it")
                    mTaskHelper.deleteTasks(it)
                }
            } catch (e: Exception) {
                if (e is ContentTransformationException) {
                    Logger.log("Cannot transform request data to a DeleteTasksRequest object: ${e.message}")
                    context.respond(HttpStatusCode.NotAcceptable, "Invalid request data format!")
                } else {
                    Logger.log("DeleteTasks request can't be proceeded: ${e.message}")
                    context.respond(HttpStatusCode.BadRequest, "Invalid request!")
                }
            }
            context.respond(HttpStatusCode.Accepted, "Specified Tasks are deleted!")
        } else {
            Logger.log("DeleteTasks request token is NOT valid: $token")
            context.respond(HttpStatusCode.Unauthorized, "Only ADMIN or MODERATORS can delete TASKS!")
        }
    }

    suspend fun approveTask(context: ApplicationCall) {
        val token = context.request.header(Constants.TOKEN_RESPONSE)
        Logger.log("ApproveTask request received with token: $token")
        val isUserAuthorized = validateModeratorTokens(token)
        if (isUserAuthorized) {
            Logger.log("ApproveTask request token is valid: $token")
            try {
                context.receive<String>().also {
                    Logger.log("TaskID received: $it")
                    val isTaskApproved: Boolean = mTaskHelper.approveTask(taskID = it)
                    if (isTaskApproved) context.respond(HttpStatusCode.OK, "Task Status Updated")
                    else context.respond(HttpStatusCode.NotAcceptable, "Task NOT found against the ID: $it")
                }
            } catch (e: Exception) {
                if (e is ContentTransformationException) {
                    Logger.log("Cannot transform request data to a String object: ${e.message}")
                    context.respond(HttpStatusCode.NotAcceptable, "Invalid request data format!")
                } else {
                    Logger.log("ApproveTask request can't be proceeded: ${e.message}")
                    context.respond(HttpStatusCode.BadRequest, "Invalid request!")
                }
            }
        } else {
            Logger.log("ApproveTask request token is NOT valid: $token")
            context.respond(HttpStatusCode.Unauthorized, "Only ADMIN or MODERATORS can approve TASKS!")
        }
    }

    suspend fun addPaymentToTask(context: ApplicationCall) {
        val token = context.request.header(Constants.TOKEN_RESPONSE)
        Logger.log("AddPaymentToTask request received with token: $token")
        val isUserAuthorized = validateUserTokens(token)
        if (isUserAuthorized) {
            Logger.log("AddPaymentToTasks request token is valid: $token")
            try {
                context.receive<Payment>().also {
                    Logger.log("Executing AddPaymentToTasks request: $it")
                    mTaskHelper.addPaymentToTask(it)
                }
            } catch (e: Exception) {
                if (e is ContentTransformationException) {
                    Logger.log("Cannot transform request data to a Payment object: ${e.message}")
                    context.respond(HttpStatusCode.NotAcceptable, "Invalid request data format!")
                } else {
                    Logger.log("AddPaymentToTasks request can't be proceeded: ${e.message}")
                    context.respond(HttpStatusCode.BadRequest, "Invalid request!")
                }
            }
            context.respond(HttpStatusCode.Accepted, "Payment Updated!")
        } else {
            Logger.log("AddPaymentToTasks request token is NOT valid: $token")
            context.respond(HttpStatusCode.Unauthorized, "Only ADMIN or MODERATORS can delete TASKS!")
        }
    }

    suspend fun transferTasks(context: ApplicationCall) {
        val token = context.request.header(Constants.TOKEN_RESPONSE)
        Logger.log("TransferTask request received with token: $token")
        val isUserAuthorized = validateModeratorTokens(token)
        if (isUserAuthorized) {
            Logger.log("TaskTransfer request token is valid: $token")
            try {
                context.receive<TaskTransferRequest>().also {
                    Logger.log("Executing TaskTransfer request: $it")
                    mTaskHelper.transferTask(it)
                }
            } catch (e: Exception) {
                if (e is ContentTransformationException) {
                    Logger.log("Cannot transform request data to a TaskTransferRequest object: ${e.message}")
                    context.respond(HttpStatusCode.NotAcceptable, "Invalid request data format!")
                } else {
                    Logger.log("TaskTransfer request can't be proceeded: ${e.message}")
                    context.respond(HttpStatusCode.BadRequest, "Invalid request!")
                }
            }
            context.respond(HttpStatusCode.Accepted, "Task Transferred!")
        } else {
            Logger.log("TaskTransfer request token is NOT valid: $token")
            context.respond(HttpStatusCode.Unauthorized, "Only ADMIN or MODERATORS can transfer TASKS!")
        }
    }

    suspend fun editTask(context: ApplicationCall) {
        val token = context.request.header(Constants.TOKEN_RESPONSE)
        Logger.log("EditTask request received with token: $token")
        val isUserAuthorized = validateModeratorTokens(token)
        if (isUserAuthorized) {
            Logger.log("EditTask request token is valid: $token")
            try {
                context.receive<EditTaskRequest>().also {
                    Logger.log("Executing EditTask request: $it")
                    mTaskHelper.editTask(it)
                }
            } catch (e: Exception) {
                if (e is ContentTransformationException) {
                    Logger.log("Cannot transform request data to a EditTask object: ${e.message}")
                    context.respond(HttpStatusCode.NotAcceptable, "Invalid request data format!")
                } else {
                    Logger.log("EditTask request can't be proceeded: ${e.message}")
                    context.respond(HttpStatusCode.BadRequest, "Invalid request!")
                }
            }
            context.respond(HttpStatusCode.Accepted, "Task Modified!")
        } else {
            Logger.log("EditTask request token is NOT valid: $token")
            context.respond(HttpStatusCode.Unauthorized, "Only ADMIN or MODERATORS can edit TASKS!")
        }
    }

    suspend fun getTasks(context: ApplicationCall) {
        val token = context.request.header(Constants.TOKEN_RESPONSE)
        Logger.log("GetTasks request received with token: $token")
        val isUserAuthorized = validateModeratorTokens(token) || validateUserTokens(token)
        if (isUserAuthorized) {
            Logger.log("GetTasks request token is valid: $token")
            try {
                context.receive<GetTasksRequest>().also {
                    Logger.log("Executing GetTasks request: $it")
                    withContext(Dispatchers.IO) {
                        val tasks = mTaskHelper.getTasks(it)
                        Logger.log("Returning Tasks:: $tasks")
                        context.respond(HttpStatusCode.OK, tasks)
                    }
                }
            } catch (e: Exception) {
                if (e is ContentTransformationException) {
                    Logger.log("Cannot transform request data to a GetTasksRequest object: ${e.message}")
                    context.respond(HttpStatusCode.NotAcceptable, "Invalid request data format!")
                } else {
                    Logger.log("type: ${e.javaClass.simpleName}")
                    Logger.log("GetTasks request can't be proceeded: ${e.message}")
                    context.respond(HttpStatusCode.BadRequest, "Invalid request!")
                }
            }
        } else {
            Logger.log("GetTasks request token is NOT valid: $token")
            context.respond(HttpStatusCode.Unauthorized, "Invalid user token for request!")
        }
    }

    suspend fun getPayments(context: ApplicationCall) {
        val token = context.request.header(Constants.TOKEN_RESPONSE)
        Logger.log("GetPayments request received with token: $token")
        val isUserAuthorized = validateModeratorTokens(token)
        if (isUserAuthorized) {
            Logger.log("GetPayments request token is valid: $token")
            try {
                context.receive<PaymentsRequest>().also {
                    Logger.log("Executing GetPayments request: $it")
                    val payments = mTaskHelper.getPayments(it)
                    context.respond(HttpStatusCode.OK, payments)
                }
            } catch (e: Exception) {
                if (e is ContentTransformationException) {
                    Logger.log("Cannot transform request data to a GetPaymentsRequest object: ${e.message}")
                    context.respond(HttpStatusCode.NotAcceptable, "Invalid request data format!")
                } else {
                    Logger.log("GetPayments request can't be proceeded: ${e.message}")
                    context.respond(HttpStatusCode.BadRequest, "Invalid request!")
                }
            }
        } else {
            Logger.log("GetPayments request token is NOT valid: $token")
            context.respond(HttpStatusCode.Unauthorized, "Only ADMIN or MODERATORS can request PAYMENTS!")
        }
    }

    suspend fun deletePayments(context: ApplicationCall) {
        val token = context.request.header(Constants.TOKEN_RESPONSE)
        Logger.log("DeletePayments request received with token: $token")
        val isUserAuthorized = validateAdminTokens(token)
        if (isUserAuthorized) {
            Logger.log("DeletePayments request token is valid: $token")
            try {
                context.receive<PaymentsRequest>().also {
                    Logger.log("Executing DeletePayments request: $it")
                    mTaskHelper.deletePayments(it)
                }
            } catch (e: Exception) {
                if (e is ContentTransformationException) {
                    Logger.log("Cannot transform request data to a DeletePaymentsRequest object: ${e.message}")
                    context.respond(HttpStatusCode.NotAcceptable, "Invalid request data format!")
                } else {
                    Logger.log("DeletePayments request can't be proceeded: ${e.message}")
                    context.respond(HttpStatusCode.BadRequest, "Invalid request!")
                }
            }
            context.respond(HttpStatusCode.Accepted, "Payments deleted!")
        } else {
            Logger.log("DeletePayments request token is NOT valid: $token")
            context.respond(HttpStatusCode.Unauthorized, "Only ADMIN can delete PAYMENTS record!")
        }
    }
}