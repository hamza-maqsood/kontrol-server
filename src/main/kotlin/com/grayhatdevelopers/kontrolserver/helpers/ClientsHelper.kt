package com.grayhatdevelopers.kontrolserver.helpers

import com.grayhatdevelopers.kontrolserver.models.Client
import com.grayhatdevelopers.kontrolserver.models.ClientBalanceUpdateRequest
import com.grayhatdevelopers.kontrolserver.repository.Repository
import com.grayhatdevelopers.kontrolserver.utils.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.setValue

class ClientsHelper {

    suspend fun updateClientBalance(clientBalanceUpdateRequest: ClientBalanceUpdateRequest): Boolean {
        return withContext(Dispatchers.IO) {
            with(clientBalanceUpdateRequest) {
                if (doesClientExist(this.client)) {
                    Repository.clientsCollection.updateOne(
                        Client::name eq client, setValue(Client::balance, newBalance)
                    )
                    Repository.updateClientBalanceUpdateRequestCollection.insertOne(this)
                    return@withContext true
                } else {
                    Logger.log("Client with name: ${this.client} does not exists!")
                    return@withContext false
                }
            }
        }
    }

    suspend fun getClient(name: String): Client = withContext(Dispatchers.IO) {
        return@withContext Repository.clientsCollection.findOne {
            Client::name eq name
        } ?: Client()
    }

    suspend fun getAllClients(): List<Client> = withContext(Dispatchers.IO) {
        return@withContext Repository.clientsCollection.find().toList()
    }

    private suspend fun doesClientExist(name: String): Boolean {
        return withContext(Dispatchers.IO) {
            return@withContext Repository.clientsCollection.findOne {
                Client::name eq name
            } != null
        }
    }

    companion object {
        suspend fun addClient(client: Client) {
            withContext(Dispatchers.IO) {
                Repository.clientsCollection.insertOne(client)
            }
        }
    }
}