package com.grayhatdevelopers.kontrolserver.config

import com.fasterxml.jackson.databind.SerializationFeature
import com.grayhatdevelopers.kontrolserver.controllers.*
import com.grayhatdevelopers.kontrolserver.exceptions.InvalidCredentialsException
import com.grayhatdevelopers.kontrolserver.repository.Repository
import com.grayhatdevelopers.kontrolserver.routings.*
import com.grayhatdevelopers.kontrolserver.utils.Constants
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.*
import io.ktor.gson.gson
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.server.engine.*
import io.ktor.server.netty.Netty
import io.ktor.util.KtorExperimentalAPI
import org.kodein.di.generic.instance
import org.slf4j.event.Level
import java.text.DateFormat


@KtorExperimentalAPI
@EngineAPI
fun server(
    engine: ApplicationEngineFactory<BaseApplicationEngine,
            out ApplicationEngine.Configuration>
): BaseApplicationEngine {
    setupDB()
    return embeddedServer(
        engine,
        port = Constants.SERVER_PORT,
        watchPaths = listOf("com.grayhatdevelopers.kontrolserver.config.mainModule"),
        module = Application::mainModule
    )
}

@EngineAPI
@KtorExperimentalAPI
fun setup() = server(Netty)

fun setupDB() {
    Repository.setupDB()
}

@KtorExperimentalAPI
@EngineAPI
fun Application.mainModule() {

    val ridersController by ModulesConfig.kodein.instance<RiderController>()
    val taskController by ModulesConfig.kodein.instance<TaskController>()
    val managementController by ModulesConfig.kodein.instance<ManagementController>()
    val clientsController by ModulesConfig.kodein.instance<ClientsController>()
    val superUserController by ModulesConfig.kodein.instance<SuperUserController>()

    install(CallLogging) {
        level = Level.INFO
    }

    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT) // Pretty Prints the JSON
        }
    }

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Get)
        method(HttpMethod.Post)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        allowCredentials = true
        anyHost()
    }

    install(StatusPages) {
        exception<InvalidCredentialsException> { exception ->
            call.respond(HttpStatusCode.Unauthorized, mapOf("OK" to false, "error" to (exception.message ?: "")))
        }
    }

    install(DefaultHeaders)

    install(Routing) {
        riders(riderController = ridersController)
        tasks(taskController = taskController)
        management(managementController = managementController)
        clients(clientsController = clientsController)
        superUser(superUserController = superUserController)
    }
}