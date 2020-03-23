package com.grayhatdevelopers.kontrolserver.utils

import com.grayhatdevelopers.kontrolserver.models.TaskModel
import com.grayhatdevelopers.kontrolserver.models.TaskStatus
import com.grayhatdevelopers.kontrolserver.models.TaskType
import com.grayhatdevelopers.kontrolserver.repository.Repository


fun getAllRiders(): ArrayList<String> =
    ArrayList(
        Repository.ridersCollection.find().toList().map {
            return@map it.username
        }.distinct()
    )

fun getAllClients(): ArrayList<String> =
    ArrayList(
        Repository.tasksCollection.find(
        ).toList().map {
            return@map it.shopName
        }.distinct()
    )

fun getAllDates(): ArrayList<String> =
    ArrayList(
        Repository.tasksCollection.find(
        ).toList().map {
            return@map it.date
        }.distinct()
    )

fun getAllCompanies(): ArrayList<String> =
    ArrayList(
        Repository.tasksCollection.find(
        ).toList().map {
            return@map it.company
        }.distinct()
    )

fun getAllTaskTypes(): ArrayList<TaskType> =
    ArrayList(
        Repository.tasksCollection.find(
        ).toList().map {
            return@map TaskType.valueOf(it.taskType.toString())
        }.distinct()
    )

fun getAllTaskModels(): ArrayList<TaskModel> =
    ArrayList(
        Repository.tasksCollection.find(
        ).toList().map {
            return@map TaskModel.valueOf(it.taskModel.toString())
        }.distinct()
    )

fun getAllTaskStatuses(): ArrayList<TaskStatus> =
    ArrayList(
        Repository.tasksCollection.find(
        ).toList().map {
            return@map TaskStatus.valueOf(it.taskStatus.toString())
        }.distinct()
    )