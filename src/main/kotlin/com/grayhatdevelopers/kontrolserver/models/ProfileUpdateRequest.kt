package com.grayhatdevelopers.kontrolserver.models

class ProfileUpdateRequest(
    username: String,  /* username */
    password: String,  /* password for verification */
    val newPassword: String?,  /* if password need to be updated */
    val newImageURI: String?,  /* if image is to be updated */
    val newDisplayName: String? /* if display name is to be updated */
) : LoginCredentials(username, password)