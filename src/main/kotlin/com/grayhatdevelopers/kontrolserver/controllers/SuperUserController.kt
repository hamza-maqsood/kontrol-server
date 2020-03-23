package com.grayhatdevelopers.kontrolserver.controllers

import com.grayhatdevelopers.kontrolserver.authentication.generateSuperUserTokens
import com.grayhatdevelopers.kontrolserver.authentication.validateSuperUserTokens
import com.grayhatdevelopers.kontrolserver.helpers.SuperUserHelper
import com.grayhatdevelopers.kontrolserver.models.LoginCredentials
import com.grayhatdevelopers.kontrolserver.utils.Constants
import com.grayhatdevelopers.kontrolserver.utils.Logger
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.request.ContentTransformationException
import io.ktor.request.header
import io.ktor.request.receive
import io.ktor.response.header
import io.ktor.response.respond
import io.ktor.response.respondFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.html.InputType

class SuperUserController(
    private val mSuperUserHelper: SuperUserHelper
) {

    suspend fun authenticateSuperUser(context: ApplicationCall) {
        Logger.log("SUPER USER login request received")
        try {
            context.receive<LoginCredentials>().also {
                withContext(Dispatchers.IO) {
                    val superUser = mSuperUserHelper.authenticate(it)
                    if (superUser == null) {
                        Logger.log("Super User LoginCredentials are NOT valid $it")
                        context.respond(HttpStatusCode.Unauthorized, "Invalid username or password")
                    } else {
                        /* RIDER is valid, log him in if not already logged in */
                        //return the rider with it's session tokens
                        Logger.log("Super User LoginCredentials are valid $it")
                        val token = generateSuperUserTokens(username = it.username)
                        superUser.sessionToken = token
                        Logger.log("Returning Super User object: $superUser with token $token")
                        context.respond(HttpStatusCode.Accepted, superUser)
                    }
                }
            }
        } catch (e: Exception) {
            if (e is ContentTransformationException) {
                Logger.log("Cannot transform request data to a LoginCredentials object: ${e.message}")
                context.respond(HttpStatusCode.NotAcceptable, "Invalid request data format!")
            } else {
                Logger.log("Super User login request can't be proceeded: ${e.message}")
                context.respond(HttpStatusCode.BadRequest, "Invalid request!")
            }
        }
    }

    suspend fun getLogFile(context: ApplicationCall) {
        val token = context.request.header(Constants.TOKEN_RESPONSE)
        Logger.log("GetLogFile request received with token: $token")
        try {
            context.receive<String>().also {
                if (validateSuperUserTokens(token)) {
                    Logger.log("Valid token for GetLogs request: $token")
                    val file = mSuperUserHelper.getLogFile(it)
                    file?.let {
                        context.response.header(
                            "Content-Disposition",
                            "attachment; filename=\"${InputType.file.name}\""
                        )
                        context.respondFile(file)
                    }
                    file ?: context.respond(HttpStatusCode.NoContent, "No log file found against that date")
                } else {
                    Logger.log("Invalid super user token for GetLogs request: $token")
                    context.respond(HttpStatusCode.Unauthorized, "Not authorized for this request!")
                }
            }
        } catch (e: Exception) {
            if (e is ContentTransformationException) {
                Logger.log("Cannot transform request data to a GetLogFile object: ${e.message}")
                context.respond(HttpStatusCode.NotAcceptable, "Invalid request data format!")
            } else {
                Logger.log("GetLogFileRequest can't be proceeded: ${e.message}")
                context.respond(HttpStatusCode.BadRequest, "Invalid request!")
            }
        }
    }

    suspend fun getAllUsers(context: ApplicationCall) {
        val token = context.request.header(Constants.TOKEN_RESPONSE)
        Logger.log("GetAllUsers request received with token: $token")
        if (validateSuperUserTokens(token)) {
            Logger.log("Valid token for GetAllTasks request: $token")
            context.respond(HttpStatusCode.OK, mSuperUserHelper.getAllUsers())
        } else {
            Logger.log("Invalid super user token for GetAllUsers request: $token")
            context.respond(HttpStatusCode.Unauthorized, "Not authorized for this request!")
        }
    }
}