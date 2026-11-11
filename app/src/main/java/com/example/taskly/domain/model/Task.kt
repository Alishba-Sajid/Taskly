package com.example.taskly.domain.model

data class Task(
    val id: Int = 0,
    val title: String,
    val description: String,
    val status: TaskStatus = TaskStatus.PENDING
)
