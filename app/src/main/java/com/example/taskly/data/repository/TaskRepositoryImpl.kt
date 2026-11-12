package com.example.taskly.data.repository

import com.example.taskly.data.local.TaskDao
import com.example.taskly.data.mapper.toDomain
import com.example.taskly.data.mapper.toEntity
import com.example.taskly.domain.model.Task
import com.example.taskly.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {

    override suspend fun insertTask(task: Task) {
        taskDao.insertTask(task.toEntity())
    }

    override suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.toEntity())
    }

    override suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task.toEntity())
    }

    override suspend fun getTaskById(taskId: Int): Task? {
        return taskDao.getTaskById(taskId)?.toDomain()
    }


    override fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { tasks ->
            tasks.map { it.toDomain() }
        }
    }

    override fun searchTasks(searchQuery: String): Flow<List<Task>> {
        return taskDao.searchTasks(searchQuery).map { tasks ->
            tasks.map { it.toDomain() }
        }
    }
}
