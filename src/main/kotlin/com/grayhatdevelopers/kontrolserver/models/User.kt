package com.grayhatdevelopers.kontrolserver.models

open class User(
    val username: String,  /* uniquely identified username */
    val displayName: String,  /* Actual name */
    var sessionToken: String?,  /* assigned session JWToken */
    val imageURI: String,  /* image URI */
    val userType: UserType?  /* User Type, this will determine permissions */
) {
    companion object {
        val FIELD_USERNAME = User::username.name
    }
}

enum class UserType {
    RIDER, MODERATOR, ADMIN, SUPERUSER
}
