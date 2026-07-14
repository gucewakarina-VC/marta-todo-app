package com.marta.todoapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val emoji: String = "✨",
    val starsReward: Int = 1,
    val isCompleted: Boolean = false,
    val completedDate: String? = null,
    val sortOrder: Int = 0,
    val isDefault: Boolean = false
)
