package com.grayhatdevelopers.kontrolserver.data

import com.grayhatdevelopers.kontrolserver.models.JWToken

object Inventory {
    var ADMIN_TOKEN: String = ""
    var ACTIVE_SUPER_USER_TOKEN = ""
    val activeRidersTokens = mutableListOf<JWToken>()
    val activeModeratorsTokens = mutableListOf<JWToken>()
}