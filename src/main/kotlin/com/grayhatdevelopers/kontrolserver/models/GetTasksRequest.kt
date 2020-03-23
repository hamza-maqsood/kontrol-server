package com.grayhatdevelopers.kontrolserver.models


open class GetTasksRequest(
    val riders: ArrayList<String>, /* list of riders to include in the list, empty list indicates all riders */
    val dates: ArrayList<String>, /* list of dates to include in the list, empty list indicates all riders */
    val companies: ArrayList<String>, /* list of companies to include in the list, empty list indicates all riders */
    val shops: ArrayList<String>, /* list of shops to include in the list, empty list indicates all riders */
    val taskTypes: ArrayList<TaskType>, /* list of task types to include in the list, empty list indicates all riders */
    val tasksModels: ArrayList<TaskModel>, /* list of task models to include in the list, empty list indicates all riders */
    val taskStatuses: ArrayList<TaskStatus> /* list of task statues to include in the list, empty list indicates all riders */
)