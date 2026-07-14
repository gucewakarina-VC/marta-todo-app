package com.marta.todoapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.marta.todoapp.data.local.dao.AchievementDao
import com.marta.todoapp.data.local.dao.RewardDao
import com.marta.todoapp.data.local.dao.TaskDao
import com.marta.todoapp.data.local.entity.AchievementEntity
import com.marta.todoapp.data.local.entity.RewardEntity
import com.marta.todoapp.data.local.entity.TaskEntity

@Database(
    entities = [TaskEntity::class, RewardEntity::class, AchievementEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun rewardDao(): RewardDao
    abstract fun achievementDao(): AchievementDao
}
