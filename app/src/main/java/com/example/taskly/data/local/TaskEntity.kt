package com.example.taskly.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.taskly.domain.model.Task
import com.example.taskly.domain.model.TaskStatus

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val dueDate: String,
    val status: String = "IN_PROGRESS"
)

fun TaskEntity.toDomain(): Task {
    return Task(
        id = id,
        title = title,
        description = description,
        dueDate = dueDate,
        status = TaskStatus.valueOf(status)
    )
}

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        description = description,
        dueDate = dueDate,
        status = status.name
    )
}
