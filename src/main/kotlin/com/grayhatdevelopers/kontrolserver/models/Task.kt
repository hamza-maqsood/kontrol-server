package com.grayhatdevelopers.kontrolserver.models

import com.fasterxml.jackson.annotation.JsonFormat

data class Task(
    val taskID: String, /* com.grayhatdevelopers.kontrolserver.config.server generated unique identifier */
    val rider: String, /* rider to whom this very task is assigned */
    val debit: Long, /* amount that the rider is supposed to take from the client */
    val taskModel: TaskModel,/* task model */
    val shopName: String, /* shop, for whom this very task is */
    val date: String, /* assignment date */
    val createdAt: String, /* time when the task was created */
    val lastUpdatedAt: String, /* time of last update */
    val taskStatus: TaskStatus, /* current status of task */
    var transference: Int, /* transference number */
    val taskType: TaskType, /* task type */
    val assignedTo: String, /* rider to whom this task was actually assigned */
    val company: String, /* products company */
    val paymentId: String? /* payment for this very task */
)

@JsonFormat(shape = JsonFormat.Shape.STRING)
enum class TaskStatus {
    ACTIVE, COMPLETED, WAITING, Active, Completed, Waiting
}

@Suppress("EnumEntryName")
@JsonFormat(shape = JsonFormat.Shape.STRING)
enum class TaskType {
    OFF_SCHEDULE, REGULAR, EMERGENCY, Off_Schedule, Regular, Emergency
}

@JsonFormat(shape = JsonFormat.Shape.STRING)
enum class TaskModel {
    Invoice, Payment, Return, INVOICE, PAYMENT, RETURN,
}
