package com.grayhatdevelopers.kontrolserver.routings

import com.grayhatdevelopers.kontrolserver.controllers.ManagementController
import com.grayhatdevelopers.kontrolserver.utils.Constants
import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.*
import io.ktor.util.KtorExperimentalAPI

@KtorExperimentalAPI
fun Routing.management(managementController: ManagementController) {

    /**
     * MANAGEMENT (ADMIN/MODERATOR) login route
     * @required: [com.grayhatdevelopers.kontrolserver.models.LoginCredentials] in the call
     * @return: if the credentials are valid, returns an HTTP status of ACCEPTED
     * along with the generated JWToken
     * if the credentials are not valid, an Unauthorized response is returned with a null JWToken
     *
     * @note that only one instance of ADMIN login can exists, so if another a valid request of ADMIN login
     * is sent, that'll override the existing ADMIN_TOKENS, but in case of moderator, any number of moderators can login at
     * a given instance of time.
     */
    route(Constants.MANAGEMENT_LOGIN_ROUTE) {
        post {
            managementController.managementLogin(this.call)
        }
    }

    /**
     * MANAGEMENT (ADMIN/MODERATOR) logout route
     * @required: a JWToken in the call
     *
     * Ends the respective ADMIN/MODERATOR session, if active
     */
    route(Constants.MANAGEMENT_SIGN_OUT_ROUTE) {
        post {
            managementController.managementSignOut(this.call)
        }
    }

    /**
     * User validation route
     * @required: Token in the header
     * @return: If the token is valid, associated user is returned with a status code of OK, null otherwise with Status code: UnAccepted
     */
    route(Constants.VALIDATE_USER_TOKEN) {
        post {
            managementController.validateUserToken(this.call)
        }
    }


    /**
     * USER registration route
     * @required: an ADMIN JWToken in the call
     * @required: an object of type [com.grayhatdevelopers.kontrolserver.models.UserRegistrationRequest] in the body
     * Note that only ADMIN can create new users(RIDER or MODERATOR)
     * @return: HttpStatus indicating success/failure response
     */
    route(Constants.CREATE_USER_ROUTE) {
        post {
            managementController.createUser(this.call)
        }
    }

    /**
     * route to delete users
     * @required: an ADMIN JWToken in the call
     * @required: username of the RIDER/MODERATOR
     * if the ADMIN tokens are valid, and if a RIDER/MODERATOR exists with that name,
     * it deletes that user from the database
     * @return: HttpStatus, indicating success/failure
     */
    route(Constants.DELETE_USER_ROUTE) {
        delete {
            managementController.deleteUser(this.call)
        }
    }

    /**
     * route to get a list of all RIDERS
     * @required: a JWToken, associated to a MODERATOR or a RIDER
     * @return: an array containing objects of type [com.grayhatdevelopers.kontrolserver.models.Rider]
     * if the token isn't valid, an HttpStatus indicating failure is returned
     */
    route(Constants.GET_ALL_RIDERS_ROUTE) {
        post {
            managementController.getAllRiders(this.call)
        }
    }

    /**
     * route to get a list of all MODERATORS
     * @required: a JWToken, associated to an ADMIN
     * @return: an array containing objects of type [com.grayhatdevelopers.kontrolserver.models.User]
     * if the token isn't valid, an HttpStatus indicating failure is returned
     */
    route(Constants.GET_ALL_MODERATORS_ROUTE) {
        post {
            managementController.getAllModerators(this.call)
        }
    }

    /**
     * root route, just for testing purposes
     * simply returns a plain text
     */
    route(Constants.ROOT_ROUTE) {
        get {
            call.respondText("Hello there, welcome to the Kontrol Server, Wish you happy coding :)")
        }
    }
}