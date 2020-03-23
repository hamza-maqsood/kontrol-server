package com.grayhatdevelopers.kontrolserver.models

data class ClientBalanceUpdateRequest(
    val requesterUsername: String = "",
    val client: String = "",
    val newBalance: Long = 0.toLong()
)