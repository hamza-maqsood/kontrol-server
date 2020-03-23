package com.grayhatdevelopers.kontrolserver.utils

object Constants {
    const val ENCRYPTION_KEY = "M A R D A N A M A R A D S"
    const val HOST = "localhost"
    const val MONGO_PORT = 27017
    const val SERVER_PORT = 8081
    const val DEFAULT_DATABASE = "defaultDatabase"

    const val AUTH_SCHEMAS = "Token"
    const val JWT_REALM = "kontrolServer"
    const val TOKEN_RESPONSE = "Token"
    const val AUTH_RIDER = "riderAuth"
    const val AUTH_MODERATOR = "moderatorAuth"
    const val AUTH_ADMIN = "adminAuth"
    const val DEFAULT_DATE_FORMAT = "d-MMM-YYYY"
    const val DEFAULT_TIME_FORMAT = "$DEFAULT_DATE_FORMAT hh:mm:ss"

    /**
     * collections
     */

    const val CLIENTS_COLLECTION = "clientsCollection"
    const val USERS_COLLECTION = "usersCollection"
    const val TASKS_COLLECTION = "tasksCollection"
    const val RIDERS_COLLECTION = "ridersCollection"
    const val PAYMENTS_COLLECTION = "paymentsCollection"
    const val LOGIN_CREDENTIALS_COLLECTION = "loginCredentialsCollection"
    const val TASK_TRANSFER_REQUESTS_COLLECTION = "taskTransferRequestsCollection"
    const val DELETE_TASKS_REQUESTS_COLLECTION = "deleteTasksRequestsCollection"
    const val DELETE_PAYMENTS_REQUESTS_COLLECTION = "deletePaymentsRequestsCollection"
    const val UPDATE_CLIENT_BALANCE_REQUEST_COLLECTION = "updateClientBalanceRequestCollection"

    /**
     * modules
     */

    const val RIDERS_MODULE = "RIDERS"
    const val TASKS_MODULE = "TASKS"
    const val CLIENTS_MODULE = "CLIENTS"
    const val MANAGEMENT_MODULE = "MANAGEMENT"
    const val SUPER_USER_MODULE = "SUPERUSER"

    /**
     * routes
     */
    const val RIDER_LOGIN_ROUTE = "/rider_login"
    const val CREATE_USER_ROUTE = "/register"
    const val SIGN_OUT_ROUTE = "/sign_out"
    const val DELETE_USER_ROUTE = "/delete_user"
    const val GET_ALL_RIDERS_ROUTE = "/get_all_riders"
    const val GET_ALL_MODERATORS_ROUTE = "/get_all_moderators"
    const val UPDATE_USER_ROUTE = "/update_user_route"
    const val GET_TASKS_ROUTE = "/tasks"
    const val DELETE_TASKS_ROUTE = "/delete_tasks"
    const val ADD_TASKS_ROUTE = "/add_tasks"
    const val ADD_PAYMENT_TO_TASK_ROUTE = "/add_payment_to_task"
    const val TRANSFER_TASK_ROUTE = "/transfer_task"
    const val EDIT_TASK_ROUTE = "/edit_task"
    const val GET_PAYMENTS_ROUTE = "/get_payments"
    const val DELETE_PAYMENTS_ROUTE = "delete_payments"
    const val ROOT_ROUTE = "/"
    const val VALIDATE_USER_TOKEN = "/validate_user_token"
    const val GET_CLIENT_ROUTE = "/get_client"
    const val GET_ALL_CLIENTS_ROUTE = "/get_all_clients"
    const val UPDATE_CLIENT_BALANCE_ROUTE = "/update_client_balance"
    const val MANAGEMENT_LOGIN_ROUTE = "/management_login"
    const val MANAGEMENT_SIGN_OUT_ROUTE = "/management_sign_out"
    const val APPROVE_PAYMENT = "/approve_payment"

    // Super User routes
    const val AUTHENTICATE_SUPER_USER_ROUTE = "/su_login"
    const val GET_ALL_USERS_ROUTE = "/get_all_users"
    const val GET_LOGS_ROUTE = "get_logs"
}
