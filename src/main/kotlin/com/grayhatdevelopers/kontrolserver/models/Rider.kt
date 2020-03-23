package com.grayhatdevelopers.kontrolserver.models

class Rider(
    username: String,
    displayName: String,
    sessionToken: String?,
    imageURI: String,
    userType: UserType?,
    val cashAmount: Int  /* Cash Amount currently the rider due */
) : User(
    username, displayName, sessionToken, imageURI, userType
)