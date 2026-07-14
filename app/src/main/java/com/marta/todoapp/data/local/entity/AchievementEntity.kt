package com.marta.todoapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val targetValue: Int,
    val currentValue: Int = 0,
    val isUnlocked: Boolean = false
)
