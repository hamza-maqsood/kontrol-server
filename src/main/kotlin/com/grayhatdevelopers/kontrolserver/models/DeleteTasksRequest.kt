package com.grayhatdevelopers.kontrolserver.models

class DeleteTasksRequest(
    val taskIds: ArrayList<String>, /* List of task IDs to delete*/
    riders: ArrayList<String>,
    dates: ArrayList<String>,
    companies: ArrayList<String>,
    shops: ArrayList<String>,
    taskTypes: ArrayList<TaskType>,
    tasksModels: ArrayList<TaskModel>,
    taskStatuses: ArrayList<TaskStatus>
) : GetTasksRequest(
    riders, dates, companies, shops, taskTypes, tasksModels, taskStatuses
)