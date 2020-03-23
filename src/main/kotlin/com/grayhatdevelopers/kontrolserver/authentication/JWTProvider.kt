package com.grayhatdevelopers.kontrolserver.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.grayhatdevelopers.kontrolserver.models.User
import com.grayhatdevelopers.kontrolserver.utils.Constants
import java.util.*

object JwtProvider {
    private const val issuer = "ktor-realworld"
    private const val audience = "ktor-audience"

    private val algorithm = Algorithm.HMAC256(Constants.ENCRYPTION_KEY)

    fun createJWT(username: String): String =
        JWT.create()
            .withIssuedAt(Date())
            .withSubject("Authentication")
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim(User.FIELD_USERNAME, username).sign(algorithm)
}