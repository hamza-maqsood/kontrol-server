package com.grayhatdevelopers.kontrolserver.authentication

import com.grayhatdevelopers.kontrolserver.data.Inventory
import com.grayhatdevelopers.kontrolserver.models.JWToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthenticationDAO {

    suspend fun authenticateUser(token: String?): Boolean = withContext(Dispatchers.IO) {
        token?.let {
            for (e in Inventory.activeRidersTokens)
                if (token == e.token)
                    return@withContext true
        }
        return@withContext false
    }

    suspend fun authenticateModerator(token: String?): Boolean = withContext(Dispatchers.IO) {
        return@withContext ((authenticateAdmin(token)) || with(token) {
            for (e in Inventory.activeModeratorsTokens)
                if (e.token == this) return@with true
            return@with false
        })
    }

    suspend fun authenticateAdmin(token: String?): Boolean = withContext(Dispatchers.IO) {
        return@withContext Inventory.ADMIN_TOKEN.isNotBlank() && token == Inventory.ADMIN_TOKEN
    }

    suspend fun authenticateSuperUser(token: String?): Boolean = withContext(Dispatchers.IO) {
        return@withContext Inventory.ACTIVE_SUPER_USER_TOKEN.isNotBlank() && token == Inventory.ACTIVE_SUPER_USER_TOKEN
    }

    fun generateAdminTokens(): String {
        val token = JwtProvider.createJWT("admin")
        // update admin token in Inventory
        Inventory.ADMIN_TOKEN = token
        Inventory.activeModeratorsTokens.add(JWToken("admin", token))
        return token
    }

    @Suppress("DuplicatedCode")
    fun generateModeratorTokens(username: String): String {
        val token = JwtProvider.createJWT(username)
        //add these tokens if not already added
        val isUserPresent = Inventory.activeModeratorsTokens.find {
            it.username == username
        }
        if (isUserPresent != null)
            isUserPresent.token = token
        else Inventory.activeModeratorsTokens.add(JWToken(username, token))
        return token
    }

    fun generateSuperUserTokens(username: String): String {
        JwtProvider.createJWT(username).also {
            // update admin token in Inventory
            Inventory.ACTIVE_SUPER_USER_TOKEN = it
            return it
        }

    }


    @Suppress("DuplicatedCode")
    fun generateUserTokens(username: String): String {
        val token = JwtProvider.createJWT(username)
        //add these tokens if not already added
        val isUserPresent = Inventory.activeRidersTokens.find {
            it.username == username
        }
        if (isUserPresent != null)
            isUserPresent.token = token
        else Inventory.activeRidersTokens.add(JWToken(username, token))
        return token
    }
}