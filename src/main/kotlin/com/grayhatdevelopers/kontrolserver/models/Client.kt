package com.grayhatdevelopers.kontrolserver.models

data class Client(
    val name: String = "",
    val balance: Long = 0.toLong(),
    val paidAmount: Long = 0.toLong()
)