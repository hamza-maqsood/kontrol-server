package com.grayhatdevelopers.kontrolserver.models

data class TaskTransferRequest(
    val transferenceID: String,  /* id to uniquely identify the request */
    val taskID: String,  /* task id that is to be transfer */
    val currentRider: String,  /* rider to whom it is currently assigned */
    val newRider: String,  /* new rider */
    val requestTime: String,  /* com.grayhatdevelopers.kontrolserver.config.server time when the request is placed */
    val requestBearer: String  /* MODERATOR or ADMIN by whom this request is placed */
)