package com.marta.todoapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rewards")
data class RewardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val emoji: String = "🎁",
    val starCost: Int,
    val isRedeemed: Boolean = false,
    val redeemedAt: Long? = null
)
