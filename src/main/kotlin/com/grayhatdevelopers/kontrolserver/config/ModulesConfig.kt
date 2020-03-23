package com.grayhatdevelopers.kontrolserver.config

import com.grayhatdevelopers.kontrolserver.controllers.*
import com.grayhatdevelopers.kontrolserver.helpers.*
import com.grayhatdevelopers.kontrolserver.repository.Repository
import com.grayhatdevelopers.kontrolserver.utils.Constants
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton


object ModulesConfig {
    private val ridersModule = Kodein.Module(Constants.RIDERS_MODULE) {
        bind() from singleton { RiderController(instance()) }
        bind() from singleton { RiderHelper() }
        bind() from singleton { Repository }
    }

    private val tasksModule = Kodein.Module(Constants.TASKS_MODULE) {
        bind() from singleton { TaskController(instance()) }
        bind() from singleton { TaskHelper() }
    }

    private val managementModule = Kodein.Module(Constants.MANAGEMENT_MODULE) {
        bind() from singleton { ManagementController(instance()) }
        bind() from singleton { ManagementHelper() }
    }

    private val clientsModule = Kodein.Module(Constants.CLIENTS_MODULE) {
        bind() from singleton { ClientsController(instance()) }
        bind() from singleton { ClientsHelper() }
    }

    private val superUserModule = Kodein.Module(Constants.SUPER_USER_MODULE) {
        bind() from singleton { SuperUserController(instance()) }
        bind() from singleton { SuperUserHelper() }
    }

    internal val kodein = Kodein {
        import(ridersModule)
        import(tasksModule)
        import(managementModule)
        import(clientsModule)
        import(superUserModule)
    }
}