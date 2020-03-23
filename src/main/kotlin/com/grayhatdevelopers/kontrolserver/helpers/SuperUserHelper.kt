package com.grayhatdevelopers.kontrolserver.helpers

import com.grayhatdevelopers.kontrolserver.models.LoginCredentials
import com.grayhatdevelopers.kontrolserver.models.User
import com.grayhatdevelopers.kontrolserver.models.UserType
import com.grayhatdevelopers.kontrolserver.repository.Repository
import com.grayhatdevelopers.kontrolserver.utils.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import java.io.File
import java.io.FileNotFoundException

class SuperUserHelper {

    suspend fun authenticate(loginCredentials: LoginCredentials): User? {
        return withContext(Dispatchers.IO) {
            val user = Repository.loginCredentials.findOne {
                and(
                    LoginCredentials::username eq loginCredentials.username,
                    LoginCredentials::password eq loginCredentials.password
                )
            }
            return@withContext Repository.usersCollection.findOne(
                and(
                    User::username eq user?.username,
                    User::userType eq UserType.SUPERUSER
                )
            )
        }
    }

    suspend fun getAllUsers(): List<User> {
        return withContext(Dispatchers.IO) {
            return@withContext mutableListOf<User>().apply {
                this.addAll(
                    Repository.usersCollection.find().toList()
                )
                this.addAll(
                    Repository.ridersCollection.find().toList()
                )
            }
        }
    }

    suspend fun getLogFile(date: String): File? {
        val filename = "./Logs/Logs_${date}.txt"
        try {
            return withContext(Dispatchers.IO) {
                File(filename).also {
                    if (it.exists())
                        return@withContext it
                    else return@withContext null
                }
            }
        } catch (e: Exception) {
            if (e is FileNotFoundException) {
                Logger.log("File with filename $filename not found!")
            } else {
                Logger.log("Exception while returning file: ${e.message}")
            }
            return null
        }
    }
}