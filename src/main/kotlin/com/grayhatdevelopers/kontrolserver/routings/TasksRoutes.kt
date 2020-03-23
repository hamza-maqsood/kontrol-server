package com.grayhatdevelopers.kontrolserver.routings

import com.grayhatdevelopers.kontrolserver.controllers.TaskController
import com.grayhatdevelopers.kontrolserver.utils.Constants
import io.ktor.application.call
import io.ktor.routing.Routing
import io.ktor.routing.post
import io.ktor.routing.route

fun Routing.tasks(taskController: TaskController) {

    /**
     * route to get TASKS
     * @required: a JWToken assigned to a MODERATOR or ADMIN
     * @required: [com.grayhatdevelopers.kontrolserver.models.GetTasksRequest] in the body
     * the GetTaskRequest object will specify what TASKS are requested
     * if the token is valid, an array containing objects of type [com.grayhatdevelopers.kontrolserver.models.Task]is returned,
     * else an Unauthorized response is returned
     */
    route(Constants.GET_TASKS_ROUTE) {
        post {
            taskController.getTasks(this.call)
        }
    }

    /**
     * route to approve a payment by ADMIN/MODERATOR
     * @required: a JWToken assigned to a MODERATOR or ADMIN
     * @required: a taskID(String) in the body
     * @return: HTTP response of OK (200), if the request was successful, BAD REQUEST (400) response otherwise
     */
    route(Constants.APPROVE_PAYMENT) {
        post {
            taskController.approveTask(this.call)
        }
    }

    /**
     * route to delete TASKS
     * @required: a JWToken assigned to a MODERATOR or ADMIN
     * @required: [com.grayhatdevelopers.kontrolserver.models.DeleteTasksRequest] in the body
     * the DeleteTaskRequest object will specify which tasks to delete
     * Note that the associated payments are not deleted
     */
    route(Constants.DELETE_TASKS_ROUTE) {
        post {
            taskController.deleteTasks(this.call)
        }
    }

    /**
     * route to add TASKS
     * @required: a JWToken assigned to a MODERATOR or ADMIN
     * @required: an array of [com.grayhatdevelopers.kontrolserver.models.Task] in the body
     * these task are added to TASKS_COLLECTION in the DEFAULT_DATABASE
     *
     * If the client specified in the incoming task doesn't already exist in the database, a new
     * client is created with initial balance as per incoming task's debit amount, otherwise, the existing
     * client's balance is incremented with the incoming task's debit amount
     *
     * @note: If a taskID already exists, the transference of the incoming task will be incremented by 1
     * and the previous task will be replaced by the incoming one
     */
    route(Constants.ADD_TASKS_ROUTE) {
        post {
            taskController.addTasks(this.call)
        }
    }

    /**
     * route to add PAYMENTS
     * @required: a JWToken assigned to a MODERATOR or ADMIN
     * @required: an object of type [com.grayhatdevelopers.kontrolserver.models.Payment] in the body
     * passed in payment is added to the PAYMENTS_COLLECTION in the DEFAULT_DATABASE
     */
    route(Constants.ADD_PAYMENT_TO_TASK_ROUTE) {
        post {
            taskController.addPaymentToTask(this.call)
        }
    }

    /**
     * route to transfer a TASK to another RIDER
     * @required: a JWToken assigned to a MODERATOR or ADMIN
     * @required: an object of type [com.grayhatdevelopers.kontrolserver.models.TaskTransferRequest] in the body
     * transfers the specified TASK to the specified RIDER
     */
    route(Constants.TRANSFER_TASK_ROUTE) {
        post {
            taskController.transferTasks(this.call)
        }
    }

    /**
     * route to edit a TASK
     * @required: a JWToken assigned to a MODERATOR or ADMIN
     * @required: an object of type [com.grayhatdevelopers.kontrolserver.models.EditTaskRequest] in the body
     * updates the TASK as specified in the request object
     */
    route(Constants.EDIT_TASK_ROUTE) {
        post {
            taskController.editTask(this.call)
        }
    }

    /**
     * route to get PAYMENTS
     * @required: a JWToken assigned to a MODERATOR or ADMIN
     * @required: [com.grayhatdevelopers.kontrolserver.models.GetTasksRequest] in the body
     * the GetPaymentRequest object will specify what PAYMENTS are requested
     * Note that the GetTasksRequest object is used here.
     * if the token is valid, an array containing objects of type [com.grayhatdevelopers.kontrolserver.models.Payment]is returned,
     * else an Unauthorized response is returned
     */
    route(Constants.GET_PAYMENTS_ROUTE) {
        post {
            taskController.getPayments(this.call)
        }
    }

    /**
     * route to delete PAYMENTS
     * @required: a JWToken assigned to a MODERATOR or ADMIN
     * @required: [com.grayhatdevelopers.kontrolserver.models.PaymentsRequest] in the body
     * the DeletePaymentsRequest object will specify which PAYMENTS to delete
     * Note that the associated TASKS are not deleted
     */
    route(Constants.DELETE_PAYMENTS_ROUTE) {
        post {
            taskController.deletePayments(this.call)
        }
    }
}
