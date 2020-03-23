package com.grayhatdevelopers.kontrolserver.models

class UserRegistrationRequest(
    username: String,
    val password: String,  /* User password */
    displayName: String,
    sessionToken: String?,
    imageURI: String,
    userType: UserType
) : User(
    username, displayName, sessionToken, imageURI, userType
) {
    override fun toString() = StringBuilder().apply {
        append("Username: $username")
        append("displayName: $displayName")
        append("password: $password")
        append("imageURI: $imageURI")
        append("userType: $userType")
    }.toString()
}