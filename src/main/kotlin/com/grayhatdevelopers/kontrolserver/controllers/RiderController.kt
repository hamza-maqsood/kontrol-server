package com.grayhatdevelopers.kontrolserver.controllers

import com.grayhatdevelopers.kontrolserver.authentication.generateUserTokens
import com.grayhatdevelopers.kontrolserver.authentication.validateUserTokens
import com.grayhatdevelopers.kontrolserver.data.Inventory
import com.grayhatdevelopers.kontrolserver.helpers.RiderHelper
import com.grayhatdevelopers.kontrolserver.models.LoginCredentials
import com.grayhatdevelopers.kontrolserver.models.ProfileUpdateRequest
import com.grayhatdevelopers.kontrolserver.utils.Constants
import com.grayhatdevelopers.kontrolserver.utils.Logger
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.request.ContentTransformationException
import io.ktor.request.header
import io.ktor.request.receive
import io.ktor.response.respond
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RiderController(
    private val mRiderHelper: RiderHelper
) {
    suspend fun riderLogin(context: ApplicationCall) {
        Logger.log("Rider login request received")
        try {
            context.receive<LoginCredentials>().also {
                withContext(Dispatchers.IO) {
                    val rider = mRiderHelper.authenticate(it)
                    if (rider == null) {
                        Logger.log("Rider LoginCredentials are NOT valid $it")
                        context.respond(HttpStatusCode.Unauthorized, "Invalid username or password")
                    } else {
                        /* RIDER is valid, log him in if not already logged in */
                        //return the rider with it's session tokens
                        Logger.log("Rider LoginCredentials are valid $it")
                        val token = generateUserTokens(username = it.username)
                        rider.sessionToken = token
                        Logger.log("Returning Rider object: $rider with token $token")
                        context.respond(HttpStatusCode.Accepted, rider)
                    }
                }
            }
        } catch (e: Exception) {
            if (e is ContentTransformationException) {
                Logger.log("Cannot transform request data to a LoginCredentials object: ${e.message}")
                context.respond(HttpStatusCode.NotAcceptable, "Invalid request data format!")
            } else {
                Logger.log("Rider login request can't be proceeded: ${e.message}")
                context.respond(HttpStatusCode.BadRequest, "Invalid request!")
            }
        }
    }

    suspend fun signOutRider(context: ApplicationCall) {
        context.receive<String>().let { token ->
            Logger.log("Rider sign out request received with token: $token")
            Inventory.activeRidersTokens.removeIf {
                it.token == token
            }
        }
    }


    suspend fun updateUserProfile(context: ApplicationCall) {
        val token = context.request.header(Constants.TOKEN_RESPONSE)
        Logger.log("UpdateUserProfile request received with token: $token")
        if (validateUserTokens(token)) {
            Logger.log("Valid token for UpdateUserProfile request: $token")
            try {
                updateUserProfile(context.receive<ProfileUpdateRequest>())
            } catch (e: Exception) {
                if (e is ContentTransformationException) {
                    Logger.log("Cannot transform request data to a UpdateUserProfileRequest object: ${e.message}")
                    context.respond(HttpStatusCode.NotAcceptable, "Invalid request data format!")
                } else {
                    Logger.log("UpdateUserProfile request can't be proceeded: ${e.message}")
                    context.respond(HttpStatusCode.BadRequest, "Invalid request!")
                }
            }
            context.respond(HttpStatusCode.Accepted, "Profile Updated!")
        } else {
            Logger.log("Invalid user token for UpdateUserProfile request: $token")
            context.respond(HttpStatusCode.Unauthorized, "Not authorized for this request!")
        }
    }

    private suspend fun updateUserProfile(profileUpdateRequest: ProfileUpdateRequest) {
        mRiderHelper.updateUserProfile(profileUpdateRequest)
    }

}