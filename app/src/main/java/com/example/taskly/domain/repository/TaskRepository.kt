package com.example.taskly.domain.repository

import com.example.taskly.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    suspend fun insertTask(task: Task)

    suspend fun updateTask(task: Task)

    suspend fun deleteTask(task: Task)

    fun getAllTasks(): Flow<List<Task>>

    fun searchTasks(searchQuery: String): Flow<List<Task>>
}
