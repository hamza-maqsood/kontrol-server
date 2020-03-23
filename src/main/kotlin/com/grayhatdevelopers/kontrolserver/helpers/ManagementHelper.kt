package com.grayhatdevelopers.kontrolserver.helpers

import com.grayhatdevelopers.kontrolserver.authentication.generateAdminTokens
import com.grayhatdevelopers.kontrolserver.data.Inventory
import com.grayhatdevelopers.kontrolserver.models.*
import com.grayhatdevelopers.kontrolserver.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.findOne

class ManagementHelper {

    suspend fun createUser(newUser: UserRegistrationRequest) {
        withContext(Dispatchers.IO) {
            if (newUser.userType == UserType.RIDER)
                Repository.ridersCollection.insertOne(
                    Rider(
                        username = newUser.username,
                        displayName = newUser.displayName,
                        sessionToken = newUser.sessionToken,
                        imageURI = newUser.imageURI,
                        userType = UserType.RIDER,
                        cashAmount = 0
                    )
                )
            else Repository.usersCollection.insertOne(newUser)
            Repository.loginCredentials.insertOne(
                LoginCredentials(
                    username = newUser.username,
                    password = newUser.password
                )
            )
        }
    }

    suspend fun doesUserExists(username: String): Boolean = withContext(Dispatchers.IO) {
        val user = Repository.usersCollection.findOne {
            User::username eq username
        }
        return@withContext user != null
    }

    suspend fun getAllRiders(): List<User> = withContext(Dispatchers.IO) {
        return@withContext Repository.ridersCollection.find().toList()
    }

    suspend fun getAllModerators(): List<User> = withContext(Dispatchers.IO) {
        return@withContext Repository.usersCollection.find(
            User::userType eq UserType.MODERATOR
        ).toList()
    }

    suspend fun deleteUser(username: String) {
        withContext(Dispatchers.IO) {
            Repository.usersCollection.deleteOne(User::username eq username)
            Inventory.activeRidersTokens.removeIf {
                it.username == username
            }
        }
    }

    suspend fun authenticateModerator(loginCredentials: LoginCredentials): User? {
        return withContext(Dispatchers.IO) {
            val user = Repository.loginCredentials.findOne {
                and(
                    LoginCredentials::username eq loginCredentials.username,
                    LoginCredentials::password eq loginCredentials.password
                )
            }

            val foundUser = Repository.usersCollection.findOne(
                User::username eq user?.username
            )
            if (foundUser?.userType == UserType.MODERATOR)
                return@withContext foundUser
            else null
        }
    }

    suspend fun authenticateAdmin(loginCredentials: LoginCredentials): User? {
        return withContext(Dispatchers.IO) {
            val user = Repository.loginCredentials.findOne {
                and(
                    LoginCredentials::username eq loginCredentials.username,
                    LoginCredentials::password eq loginCredentials.password
                )
            }

            val foundUser = Repository.usersCollection.findOne(
                User::username eq user?.username
            )
            if (foundUser?.userType == UserType.ADMIN) {
                foundUser.sessionToken = generateAdminTokens()
                Inventory.ADMIN_TOKEN = foundUser.sessionToken!!
                return@withContext foundUser
            } else null
        }
    }

    suspend fun getUserByUsername(username: String): User? = withContext(Dispatchers.IO) {
        return@withContext Repository.usersCollection
            .findOne(User::username eq username)
    }

    suspend fun getUserByToken(token: String?): User? {
        val session = Inventory.activeModeratorsTokens.find {
            it.token == token
        }
        session?.let {
            return getUserByUsername(it.username)
        }
        // no active session found, return null
        return null
    }


}