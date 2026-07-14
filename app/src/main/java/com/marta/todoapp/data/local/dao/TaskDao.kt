package com.marta.todoapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.marta.todoapp.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY sortOrder ASC, id ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE completedDate = :date ORDER BY sortOrder ASC")
    fun getTasksForDate(date: String): Flow<List<TaskEntity>>

    @Query("SELECT COUNT(*) FROM tasks WHERE completedDate = :date AND isCompleted = 1")
    fun getCompletedCountForDate(date: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 1")
    fun getTotalCompletedCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity): Long

    @Update
    suspend fun update(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id AND isDefault = 0")
    suspend fun deleteById(id: Long)

    @Query("UPDATE tasks SET isCompleted = 0, completedDate = NULL WHERE completedDate = :date")
    suspend fun resetTasksForDate(date: String)
}
