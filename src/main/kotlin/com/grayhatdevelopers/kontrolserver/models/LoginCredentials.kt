package com.grayhatdevelopers.kontrolserver.models

open class LoginCredentials(
    val username: String, /* assigned username */
    val password: String /* password */
) {
    override fun toString() = "$username -- $password"
}