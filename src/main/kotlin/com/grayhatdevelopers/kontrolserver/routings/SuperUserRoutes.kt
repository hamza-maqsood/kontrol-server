package com.grayhatdevelopers.kontrolserver.routings

import com.grayhatdevelopers.kontrolserver.controllers.SuperUserController
import com.grayhatdevelopers.kontrolserver.utils.Constants
import io.ktor.application.call
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route

fun Routing.superUser(superUserController: SuperUserController) {

    /**
     * route to authenticate SUPERUSER
     * @required: [com.grayhatdevelopers.kontrolserver.models.LoginCredentials] in the call
     * @return: if the credentials are valid, returns an object of type [com.grayhatdevelopers.kontrolserver.models.User]
     * along with the generated JWToken
     * if the credentials are not valid, an Unauthorized response is returned with a null JWToken
     */
    route(Constants.AUTHENTICATE_SUPER_USER_ROUTE) {
        post {
            superUserController.authenticateSuperUser(this.call)
        }
    }

    /**
     * route to get all the users currently registered on the server
     * @required: a JWToken in the call header, with a key: 'Token'
     * @return: if the JWT is valid, a list containing all users is returned,
     * otherwise an Unauthorized response
     */
    route(Constants.GET_ALL_USERS_ROUTE) {
        get {
            superUserController.getAllUsers(this.call)
        }
    }

    /**
     * route to get all the log files on the server
     * @required: a JWToken in the call header, with a key: 'Token'
     * @required: a String containing date of which the logs are required
     * @return: if the JWT is valid, all files are returned,
     * otherwise an Unauthorized response
     * if the file doesn't exists, a response code of HttpStatus.NoContent (204) is returned
     * @note: date must be of the format: d-MMM-YYYY
     * @note: a header of type Content-disposition with the filename is added in the response which will
     * instruct the browser to download the incoming file.
     * @note: this route is primarily for debugging purposes or for data recovery
     */
    route(Constants.GET_LOGS_ROUTE) {
        post {
            superUserController.getLogFile(this.call)
        }
    }
}