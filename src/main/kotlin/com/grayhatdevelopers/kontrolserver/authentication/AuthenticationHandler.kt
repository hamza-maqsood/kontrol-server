package com.grayhatdevelopers.kontrolserver.authentication

class AuthenticationHandler {
    // All the DAOs go here!
    var authenticationDAO = AuthenticationDAO()
        private set

    companion object {
        // @Volatile - Writes to this property are immediately visible to other threads
        @Volatile
        private var instance: AuthenticationHandler? = null

        // The only way to get hold of the AuthenticationHandler object
        fun getInstance() =
        // Already instantiated? - return the instance
            // Otherwise instantiate in a thread-safe manner
            instance ?: synchronized(this) {
                // If it's still not instantiated, finally create an object
                // also set the "instance" property to be the currently created one
                instance ?: AuthenticationHandler().also { instance = it }
            }
    }
}