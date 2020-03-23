package com.grayhatdevelopers.kontrolserver.routings

import com.grayhatdevelopers.kontrolserver.controllers.RiderController
import com.grayhatdevelopers.kontrolserver.utils.Constants
import io.ktor.application.call
import io.ktor.routing.Routing
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.route

fun Routing.riders(riderController: RiderController) {

    /**
     * login route for RIDER
     * @required: [com.grayhatdevelopers.kontrolserver.models.LoginCredentials] in the call
     * @return: if the credentials are valid, returns an object of type [com.grayhatdevelopers.kontrolserver.models.Rider]
     * along with the generated JWToken
     * if the credentials are not valid, an Unauthorized response is returned with a null JWToken
     */
    route(Constants.RIDER_LOGIN_ROUTE) {
        post {
            riderController.riderLogin(this.call)
        }
    }

    /**
     * RIDER logout route
     * @required: a JWToken in the call, and ends the associated RIDER session
     */
    route(Constants.SIGN_OUT_ROUTE) {
        post {
            riderController.signOutRider(this.call)
        }
    }

    /**
     * route to update RIDER profile
     * @required: JWToken in the header to validate request
     * @required: [com.grayhatdevelopers.kontrolserver.models.ProfileUpdateRequest] in the call
     * along with the user acquired JWToken, if the token is valid, updates the user profile
     * with the update request.
     * else an Unauthorized response is returned
     */
    route(Constants.UPDATE_USER_ROUTE) {
        put {
            riderController.updateUserProfile(this.call)
        }
    }

    /**
     * route to get TASKS assigned to the RIDER
     * @required: JWToken in the header to validate request
     * @required: [com.grayhatdevelopers.kontrolserver.models.GetTasksRequest] in the body
     * the GetTaskRequest object will specify what tasks are requested
     * if the token is valid, an array containing objects of type [com.grayhatdevelopers.kontrolserver.models.Task]
     * is returned, else an Unauthorized response is returned
     * Note that the rider can only get his own tasks
     */
//    route(Constants.GET_TASKS_ROUTE) {
//        post {
//            riderController.getRiderTasks(this.call)
//        }
//    }
}
