package com.grayhatdevelopers.kontrolserver.models

data class JWToken(
    val username: String, /* username of the token bearer */
    var token: String /* assigned JW token */
)