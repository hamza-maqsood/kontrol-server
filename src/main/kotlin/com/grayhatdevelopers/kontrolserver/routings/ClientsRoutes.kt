package com.grayhatdevelopers.kontrolserver.routings

import com.grayhatdevelopers.kontrolserver.controllers.ClientsController
import com.grayhatdevelopers.kontrolserver.utils.Constants
import io.ktor.application.call
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route

fun Routing.clients(clientsController: ClientsController) {


    /**
     * route to get all clients.
     * @required: ADMIN/MODERATOR token in the request header.
     * @return: list of all clients present in the ClientsCollection in the DefaultDatabase.
     */
    route(Constants.GET_ALL_CLIENTS_ROUTE) {
        get {
            clientsController.getAllClients(this.call)
        }
    }

    /**
     * route to get data of client, provided client's name.
     * @required: ADMIN/MODERATOR token in the request header.
     * @required: client name in the request body.
     * @return: If the client with the given name exists, it's returned, empty Client object otherwise.
     */
    route(Constants.GET_CLIENT_ROUTE) {
        post {
            clientsController.getClient(this.call)
        }
    }

    /**
     * route to update a client's balance
     * @required: ADMIN token in the request header
     * @required: [com.grayhatdevelopers.kontrolserver.models.ClientBalanceUpdateRequest] in the call.
     * If the client exists, it's balance is updated as specified in the request object and the call is returned.
     * with a status code of ACCEPTED (202), NOT ACCEPTABLE (406) status code otherwise.
     */
    route(Constants.UPDATE_CLIENT_BALANCE_ROUTE) {
        post {
            clientsController.updateClientBalance(this.call)
        }
    }
}