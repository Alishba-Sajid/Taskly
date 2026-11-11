package com.example.taskly.data.mapper

import com.example.taskly.data.local.TaskEntity
import com.example.taskly.domain.model.Task
import com.example.taskly.domain.model.TaskStatus

fun TaskEntity.toDomain(): Task {
    return Task(
        id = id,
        title = title,
        description = description,
        status = TaskStatus.valueOf(status)
    )
}

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        description = description,
        status = status.name
    )
}
