package com.grayhatdevelopers.kontrolserver.controllers

import com.grayhatdevelopers.kontrolserver.authentication.validateAdminTokens
import com.grayhatdevelopers.kontrolserver.authentication.validateModeratorTokens
import com.grayhatdevelopers.kontrolserver.helpers.ClientsHelper
import com.grayhatdevelopers.kontrolserver.models.ClientBalanceUpdateRequest
import com.grayhatdevelopers.kontrolserver.utils.Constants
import com.grayhatdevelopers.kontrolserver.utils.Logger
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.request.ContentTransformationException
import io.ktor.request.header
import io.ktor.request.receive
import io.ktor.response.respond

class ClientsController(
    private val mClientsHelper: ClientsHelper
) {

    suspend fun getAllClients(context: ApplicationCall) {
        val token = context.request.header(Constants.TOKEN_RESPONSE)
        Logger.log("GetAllClientsRequest with token: $token")
        val isUserAuthorized = validateModeratorTokens(token)
        if (isUserAuthorized) {
            Logger.log("Token is valid for GetAllClientsRequest, sending all clients data")
            context.respond(HttpStatusCode.OK, mClientsHelper.getAllClients())
        } else {
            Logger.log("Token for GetAllClientsRequest is invalid")
            context.respond(HttpStatusCode.Unauthorized, "Sorry, only MODERATORS or ADMIN has access to it!")
        }
    }

    suspend fun getClient(context: ApplicationCall) {
        val token = context.request.header(Constants.TOKEN_RESPONSE)
        Logger.log("GetClientRequest with token: $token")
        val isUserAuthorized = validateModeratorTokens(token)
        try {
            context.receive<String>().also {
                if (isUserAuthorized) {
                    Logger.log("Token is valid for GetClientRequest, sending respective client's data")
                    context.respond(HttpStatusCode.OK, mClientsHelper.getClient(it))
                } else {
                    Logger.log("Token for GetClientRequest is invalid")
                    context.respond(HttpStatusCode.Unauthorized, "Sorry, only MODERATORS or ADMIN has access to it!")
                }
            }
        } catch (e: Exception) {
            if (e is ContentTransformationException) {
                Logger.log("Cannot transform request data to a GetClientRequest object: ${e.message}")
                context.respond(HttpStatusCode.NotAcceptable, "Invalid request data format!")
            } else {
                Logger.log("GetClientRequest can't be proceeded: ${e.message}")
                context.respond(HttpStatusCode.BadRequest, "Invalid request!")
            }
        }
    }

    suspend fun updateClientBalance(context: ApplicationCall) {
        val token = context.request.header(Constants.TOKEN_RESPONSE)
        Logger.log("UpdateClientBalanceRequest with token: $token")
        try {
            context.receive<ClientBalanceUpdateRequest>().also {
                val isUserAuthorized = validateAdminTokens(token)
                if (isUserAuthorized) {
                    Logger.log("Token is valid for UpdateClientBalanceRequest")
                    Logger.log("Updating Client Balance with request: $it")
                    val status = mClientsHelper.updateClientBalance(it)
                    if (status) {
                        Logger.log("Client balance updated with request: $it")
                        context.respond(HttpStatusCode.Accepted, "Client balance updated")
                    } else {
                        Logger.log("Client balance NOT updated with request: $it")
                        context.respond(HttpStatusCode.NotAcceptable, "Client doesn't exist!")
                    }
                } else {
                    Logger.log("Token for UpdateClientBalanceRequest: $token is not valid")
                    context.respond(HttpStatusCode.Unauthorized, "Only admin can update client balance!")
                }
            }
        } catch (e: Exception) {
            if (e is ContentTransformationException) {
                Logger.log("Cannot transform request data to a UpdateClientBalanceRequest object: ${e.message}")
                context.respond(HttpStatusCode.NotAcceptable, "Invalid request data format!")
            } else {
                Logger.log("UpdateClientBalanceRequest can't be proceeded: ${e.message}")
                context.respond(HttpStatusCode.BadRequest, "Invalid request!")
            }
        }
    }
}