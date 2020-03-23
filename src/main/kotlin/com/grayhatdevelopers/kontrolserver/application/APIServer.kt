package com.grayhatdevelopers.kontrolserver.application

import com.grayhatdevelopers.kontrolserver.config.setup
import io.ktor.server.engine.EngineAPI
import io.ktor.util.KtorExperimentalAPI

@KtorExperimentalAPI
@EngineAPI
fun main() {
    setup().start(wait = true)
}
