package com.example.taskly.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("SELECT * FROM tasks ORDER BY id DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query(
        "SELECT * FROM tasks WHERE title LIKE '%' || :searchQuery || '%' " +
                "OR description LIKE '%' || :searchQuery || '%' ORDER BY id DESC"
    )
    fun searchTasks(searchQuery: String): Flow<List<TaskEntity>>
}
