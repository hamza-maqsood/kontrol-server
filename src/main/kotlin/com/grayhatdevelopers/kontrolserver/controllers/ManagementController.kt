package com.grayhatdevelopers.kontrolserver.controllers

import com.grayhatdevelopers.kontrolserver.authentication.generateAdminTokens
import com.grayhatdevelopers.kontrolserver.authentication.generateModeratorTokens
import com.grayhatdevelopers.kontrolserver.authentication.validateAdminTokens
import com.grayhatdevelopers.kontrolserver.authentication.validateModeratorTokens
import com.grayhatdevelopers.kontrolserver.data.Inventory
import com.grayhatdevelopers.kontrolserver.helpers.ManagementHelper
import com.grayhatdevelopers.kontrolserver.models.*
import com.grayhatdevelopers.kontrolserver.utils.Constants
import com.grayhatdevelopers.kontrolserver.utils.Logger
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.request.ContentTransformationException
import io.ktor.request.header
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ManagementController(
    private val mManagementHelper: ManagementHelper
) {
    suspend fun createUser(context: ApplicationCall) {
        val token = context.request.header(Constants.TOKEN_RESPONSE)
        Logger.log("User creation request with token: $token")
        try {
            context.receive<UserRegistrationRequest>().let {
                Logger.log("User Info: $it")
                val isUserAuthorized = validateAdminTokens(token)
                if (isUserAuthorized) {
                    Logger.log("Token is valid for user creation")
                    val user = getUserByUsername(it.username)
                    if (user == null) {
                        if (it.userType == UserType.SUPERUSER) {
                            Logger.log("ADMIN tried to create a Super User!")
                            context.respond(HttpStatusCode.Forbidden, "Sorry, you can't create users of type SUPERUSER")
                        } else {
                            Logger.log("Creating user: $it")
                            mManagementHelper.createUser(newUser = it).also {
                                context.respond(HttpStatusCode.OK, "User Created Successfully!")
                            }
                        }
                    } else {
                        Logger.log("User with username ${it.username} already exists!")
                        context.respond(HttpStatusCode.Forbidden, "User with username ${it.username} already exists!")
                    }
                } else {
                    Logger.log("Token for user creation: $token is not valid")
                    context.respond(HttpStatusCode.Unauthorized, "Only admin can create new users!")
                }
            }
        } catch (e: Exception) {
            if (e is ContentTransformationException) {
                Logger.log("Cannot transform request data to a UserRegistrationRequest object: ${e.message}")
                context.respond(HttpStatusCode.NotAcceptable, "Invalid request data format!")
            } else {
                Logger.log("User creation request can't be proceeded: ${e.message}")
                context.respond(HttpStatusCode.BadRequest, "Invalid request!")
            }
        }
    }

    suspend fun managementLogin(context: ApplicationCall) {
        try {
            context.receive<LoginCredentials>().also {
                Logger.log("Management login request received: $it")
                withContext(Dispatchers.IO) {
                    var user: User? = mManagementHelper.authenticateAdmin(it)
                    if (user == null)
                        user = mManagementHelper.authenticateModerator(it)
                    if (user == null) {
                        Logger.log("Invalid credentials for management login: $it")
                        context.respond(HttpStatusCode.Unauthorized, "Invalid username or password")
                    } else {
                        /* ADMIN/MODERATOR credentials are valid, log him in if not already logged in */
                        Logger.log("Moderator credentials are valid: $it")
                        val token = if (user.userType == UserType.ADMIN) {
                            Logger.log("logged in user is ADMIN")
                            generateAdminTokens()
                        } else {
                            Logger.log("logged in user is MODERATOR")
                            generateModeratorTokens(username = user.username)
                        }
                        Logger.log("Generated ${user.userType} token: $token")
                        user.sessionToken = token

                        @Suppress("NON_EXHAUSTIVE_WHEN")
                        when (user.userType) {
                            UserType.MODERATOR -> {
                                Inventory.activeModeratorsTokens.add(JWToken(user.username, token))
                            }
                            UserType.ADMIN -> {
                                Inventory.ADMIN_TOKEN = token
                            }
                        }

                        context.respond(HttpStatusCode.Accepted, user)
                    }
                }
            }
        } catch (e: Exception) {
            Logger.log("Error while logging in as admin: ${e.message}")
            context.respond(HttpStatusCode.BadRequest, "Error while logging in as admin: ${e.message}")
        }
    }

    suspend fun managementSignOut(context: ApplicationCall) {
        try {
            context.receive<String>().let { token ->
                Logger.log("Management sign out request received with token: $token")
                val isFound = Inventory.activeModeratorsTokens.removeIf {
                    it.token == token
                }
                if (!isFound) {
                    Logger.log("Received token belongs to ADMIN, logging out admin")
                    Inventory.ADMIN_TOKEN = ""
                } else {
                    Logger.log("Received token belongs to a MODERATOR, logged out")
                }
            }
        } catch (e: Exception) {
            if (e is ContentTransformationException) {
                Logger.log("Cannot transform request data to a token : ${e.message}")
                context.respond(HttpStatusCode.NotAcceptable, "Invalid request data format!")
            } else {
                Logger.log("Management sign out request can't be proceeded: ${e.message}")
                context.respond(HttpStatusCode.BadRequest, "Invalid request!")
            }
        }
    }

    suspend fun validateUserToken(context: ApplicationCall) {
        val token = context.request.header(Constants.TOKEN_RESPONSE)
        Logger.log("Validate User request with token: $token")
        if (!token.isNullOrBlank()) {
            val user: User? = mManagementHelper.getUserByToken(token)
            if (user != null) {
                Logger.log("User found with the specified token! : $user")
                context.respond(HttpStatusCode.OK, user)
            } else {
                Logger.log("Couldn't find any with specified token!")
                context.respond(HttpStatusCode.NotAcceptable, "Cannot find any user with specified token")
            }
        } else {
            Logger.log("User token was empty in Validate User Request")
            context.respond(HttpStatusCode.BadRequest, "Empty User Token")
        }
    }


    @KtorExperimentalAPI
    suspend fun deleteUser(context: ApplicationCall) {
        val token = context.request.header(Constants.TOKEN_RESPONSE)
        Logger.log("Delete user request received with token: $token")
        val isUserAuthorized = validateAdminTokens(token)
        if (isUserAuthorized) {
            Logger.log("Delete user request received with token is valid: $token")
            try {
                val username = context.receive<String>()
                val doesUserExists = doesUserExists(username)
                if (doesUserExists) {
                    Logger.log("Deleting user with username: $username")
                    deleteUser(username)
                    context.respond(HttpStatusCode.Accepted, "User with username: $username deleted!")
                } else {
                    Logger.log("Sorry, No user exists with this username: $username")
                    context.respond(HttpStatusCode.NotAcceptable, "Sorry, No user exists with this username: $username")
                }
            } catch (e: ContentTransformationException) {
                Logger.log("Delete user request can't be proceeded: ${e.message}")
                context.respond(HttpStatusCode.BadRequest, "Invalid request!")
            }
        } else {
            Logger.log("Delete user request received with token is not valid: $token")
            context.respond(HttpStatusCode.Unauthorized, "Sorry, only admin has access to delete a user")
        }
    }

    suspend fun getAllRiders(context: ApplicationCall) {
        val token = context.request.header(Constants.TOKEN_RESPONSE)
        Logger.log("Get all riders request received with token: $token")
        val isUserAuthorized = validateModeratorTokens(token)
        if (isUserAuthorized) {
            Logger.log("Token is valid for get all riders request, sending all riders data")
            context.respond(HttpStatusCode.OK, mManagementHelper.getAllRiders())
        } else {
            Logger.log("Token for get all riders is invalid")
            context.respond(HttpStatusCode.NotAcceptable, "Sorry, only MODERATORS or ADMIN has access to it!")
        }
    }

    suspend fun getAllModerators(context: ApplicationCall) {
        val token = context.request.header(Constants.TOKEN_RESPONSE)
        Logger.log("Get all moderators request received with token: $token")
        val isAdminAuthorized = validateAdminTokens(token)
        if (isAdminAuthorized) {
            Logger.log("Token is valid for get all moderators request, sending all moderators data")
            context.respond(HttpStatusCode.OK, mManagementHelper.getAllModerators())
        } else {
            Logger.log("Get all moderators request token is not valid")
            context.respond(HttpStatusCode.NotAcceptable, "Sorry, only ADMIN has access to it!")
        }
    }

    private suspend fun getUserByUsername(username: String) = username.let {
        mManagementHelper.getUserByUsername(it)
    }

    private suspend fun deleteUser(username: String) = mManagementHelper.deleteUser(username)

    private suspend fun doesUserExists(username: String) = mManagementHelper.doesUserExists(username)
}