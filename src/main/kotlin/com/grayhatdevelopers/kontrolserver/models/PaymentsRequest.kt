package com.grayhatdevelopers.kontrolserver.models

class PaymentsRequest(
    val paymentIds: ArrayList<String>, /* List of payment IDs to delete */
    val riders: ArrayList<String>,
    val dates: ArrayList<String>,
    val companies: ArrayList<String>,
    val shops: ArrayList<String>,
    val verificationStatues: ArrayList<VerificationStatus>,
    val taskTypes: ArrayList<TaskType>,
    val tasksModels: ArrayList<TaskModel>
)