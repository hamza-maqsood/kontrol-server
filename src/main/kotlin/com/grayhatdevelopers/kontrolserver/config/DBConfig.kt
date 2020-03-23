package com.grayhatdevelopers.kontrolserver.config

import com.grayhatdevelopers.kontrolserver.utils.Constants
import org.litote.kmongo.KMongo

object DBConfig {
    fun setupKMongoInstance() = KMongo.createClient(
        Constants.HOST,
        Constants.MONGO_PORT
    )
}