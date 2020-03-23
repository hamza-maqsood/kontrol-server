package com.grayhatdevelopers.kontrolserver.models

import java.io.Serializable

data class Payment(
    val _id: String, /* payment unique id */
    val taskID: String, /* same as the associated task's 'id */
    val paidAmount: String, /* amount that was paid to the rider */
    val timeOfPayment: String?, /* (com.grayhatdevelopers.kontrolserver.config.server) time of payment */
    val remarks: String, /* remarks by the rider */
    val imageURL: String?, /* imageURI, if any added */
    val clientPhoneNumber: String = "",
    val location: LatLng, /* location point of the rider when the payment was added */
    var verificationStatus: VerificationStatus /* payment verification status */
)

enum class VerificationStatus : Serializable {
    ADMIN_PENDING, ADMIN_VERIFIED, CLIENT_VERIFIED
}