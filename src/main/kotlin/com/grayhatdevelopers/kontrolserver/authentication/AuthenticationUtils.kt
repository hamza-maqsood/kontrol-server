package com.grayhatdevelopers.kontrolserver.authentication

suspend fun validateModeratorTokens(token: String?) =
    AuthenticationHandler.getInstance().authenticationDAO.authenticateModerator(token)

suspend fun validateAdminTokens(token: String?) =
    AuthenticationHandler.getInstance().authenticationDAO.authenticateAdmin(token)

suspend fun validateUserTokens(token: String?) =
    AuthenticationHandler.getInstance().authenticationDAO.authenticateUser(token)

suspend fun validateSuperUserTokens(token: String?) =
    AuthenticationHandler.getInstance().authenticationDAO.authenticateSuperUser(token)

fun generateUserTokens(username: String) =
    AuthenticationHandler.getInstance().authenticationDAO.generateUserTokens(username)

fun generateAdminTokens() =
    AuthenticationHandler.getInstance().authenticationDAO.generateAdminTokens()

fun generateModeratorTokens(username: String) =
    AuthenticationHandler.getInstance().authenticationDAO.generateModeratorTokens(username)

fun generateSuperUserTokens(username: String) =
    AuthenticationHandler.getInstance().authenticationDAO.generateSuperUserTokens(username)