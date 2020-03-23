package com.grayhatdevelopers.kontrolserver.models

data class EditTaskRequest(
    val taskID: String, /* Task ID to edit */
    val moderatorUsername: String, /* Username of the moderator who placed the request,
                                    will be used to keep track of requests */
    val requestTime: String, /* time when the update request was placed*/
    val rider: String?, /* Rider to update */
    val shop: String?, /* Shop name to update */
    val company: String?, /*company name to update */
    val debit: Long?, /* debit to update*/
    val date: String? /*if the current task is postponed */
)