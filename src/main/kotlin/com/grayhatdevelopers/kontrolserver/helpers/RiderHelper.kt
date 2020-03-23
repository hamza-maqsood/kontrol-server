package com.grayhatdevelopers.kontrolserver.helpers

import com.grayhatdevelopers.kontrolserver.models.LoginCredentials
import com.grayhatdevelopers.kontrolserver.models.ProfileUpdateRequest
import com.grayhatdevelopers.kontrolserver.models.User
import com.grayhatdevelopers.kontrolserver.models.UserRegistrationRequest
import com.grayhatdevelopers.kontrolserver.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.setValue

class RiderHelper {

    suspend fun authenticate(loginCredentials: LoginCredentials): User? {
        return withContext(Dispatchers.IO) {
            val user = Repository.loginCredentials.findOne {
                and(
                    LoginCredentials::username eq loginCredentials.username,
                    LoginCredentials::password eq loginCredentials.password
                )
            }

            return@withContext Repository.ridersCollection.findOne(
                User::username eq user?.username
            )
        }
    }

    private suspend fun validateUserPassword(loginCredentials: LoginCredentials): Boolean =
        authenticate(loginCredentials) != null

    suspend fun updateUserProfile(profileUpdateRequest: ProfileUpdateRequest) {
        if (validateUserPassword(profileUpdateRequest)) {
            val username = profileUpdateRequest.username
            withContext(Dispatchers.IO) {
                //update password
                profileUpdateRequest.newPassword?.let {
                    Repository.usersCollection.updateOne(
                        User::username eq username,
                        setValue(UserRegistrationRequest::password, it)
                    )
                }
                //update display name
                profileUpdateRequest.newDisplayName?.let {
                    Repository.usersCollection.updateOne(
                        User::username eq username,
                        setValue(User::displayName, it)
                    )
                }
                //update image URI
                profileUpdateRequest.newImageURI?.let {
                    Repository.usersCollection.updateOne(
                        User::username eq username,
                        setValue(User::imageURI, it)
                    )
                }
            }
        }
    }
}

