package com.grayhatdevelopers.kontrolserver.models

data class Cheque(
    val _id: String, /* unique identifier for the task */
    val shopName: String, /* shop */
    val salesPerson: String, /* rider who took it */
    val time: String, /* time when it was taken */
    val amount: Int, /* amount mentioned on the cheque */
    val taskID: String, /* task id to which this payment is associated */
    val validationStatus: Boolean /* approved or not */
)