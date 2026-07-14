package com.marta.todoapp.data.repository

import com.marta.todoapp.data.local.AppDatabase
import com.marta.todoapp.data.local.DefaultData
import com.marta.todoapp.data.local.entity.AchievementEntity
import com.marta.todoapp.data.local.entity.RewardEntity
import com.marta.todoapp.data.local.entity.TaskEntity
import com.marta.todoapp.data.preferences.SettingsDataStore
import com.marta.todoapp.data.preferences.ThemeMode
import com.marta.todoapp.data.preferences.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AppRepository(
    private val database: AppDatabase,
    private val settingsDataStore: SettingsDataStore
) {
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun todayDate(): String = LocalDate.now().format(dateFormatter)

    val settings: Flow<UserSettings> = settingsDataStore.settings
    val allTasks: Flow<List<TaskEntity>> = database.taskDao().getAllTasks()
    val availableRewards: Flow<List<RewardEntity>> = database.rewardDao().getAvailableRewards()
    val redeemedRewards: Flow<List<RewardEntity>> = database.rewardDao().getRedeemedRewards()
    val achievements: Flow<List<AchievementEntity>> = database.achievementDao().getAllAchievements()
    val unlockedAchievementsCount: Flow<Int> = database.achievementDao().getUnlockedCount()
    val totalCompletedTasks: Flow<Int> = database.taskDao().getTotalCompletedCount()
    val redeemedRewardsCount: Flow<Int> = database.rewardDao().getRedeemedCount()

    suspend fun initializeIfNeeded() {
        val settings = settingsDataStore.settings.first()
        if (settings.isFirstLaunch) {
            DefaultData.defaultTasks.forEach { database.taskDao().insert(it) }
            database.achievementDao().insertAll(DefaultData.defaultAchievements)
            settingsDataStore.setFirstLaunchDone()
        }
    }

    suspend fun toggleTaskCompletion(task: TaskEntity) {
        val today = todayDate()
        val newCompleted = !task.isCompleted || task.completedDate != today

        if (newCompleted && !task.isCompleted) {
            val updated = task.copy(isCompleted = true, completedDate = today)
            database.taskDao().update(updated)
            settingsDataStore.addStars(task.starsReward)
            updateAchievementsAfterTaskCompletion()
        } else if (!newCompleted || task.completedDate == today) {
            if (task.isCompleted && task.completedDate == today) {
                val updated = task.copy(isCompleted = false, completedDate = null)
                database.taskDao().update(updated)
                settingsDataStore.spendStars(task.starsReward)
            }
        }
    }

    suspend fun completeTask(task: TaskEntity) {
        if (task.isCompleted && task.completedDate == todayDate()) return
        val updated = task.copy(isCompleted = true, completedDate = todayDate())
        database.taskDao().update(updated)
        settingsDataStore.addStars(task.starsReward)
        updateAchievementsAfterTaskCompletion()
    }

    suspend fun addTask(title: String, emoji: String, starsReward: Int) {
        val tasks = database.taskDao().getAllTasks().first()
        val maxOrder = tasks.maxOfOrNull { it.sortOrder } ?: 0
        database.taskDao().insert(
            TaskEntity(
                title = title,
                emoji = emoji,
                starsReward = starsReward,
                sortOrder = maxOrder + 1
            )
        )
    }

    suspend fun deleteTask(task: TaskEntity) {
        database.taskDao().deleteById(task.id)
    }

    suspend fun addReward(title: String, emoji: String, starCost: Int) {
        database.rewardDao().insert(RewardEntity(title = title, emoji = emoji, starCost = starCost))
    }

    suspend fun deleteReward(reward: RewardEntity) {
        database.rewardDao().deleteById(reward.id)
    }

    suspend fun redeemReward(reward: RewardEntity): Boolean {
        val success = settingsDataStore.spendStars(reward.starCost)
        if (success) {
            database.rewardDao().update(
                reward.copy(isRedeemed = true, redeemedAt = System.currentTimeMillis())
            )
            updateAchievement("first_reward", 1)
            val redeemedCount = database.rewardDao().getRedeemedCount().first()
            updateAchievement("first_reward", redeemedCount)
        }
        return success
    }

    suspend fun setParentPin(pin: String) = settingsDataStore.setParentPin(pin)
    suspend fun setThemeMode(mode: ThemeMode) = settingsDataStore.setThemeMode(mode)
    suspend fun setAgreementAccepted(accepted: Boolean) = settingsDataStore.setAgreementAccepted(accepted)
    suspend fun setChildName(name: String) = settingsDataStore.setChildName(name)
    suspend fun verifyPin(pin: String): Boolean {
        return settingsDataStore.settings.first().parentPin == pin
    }

    private suspend fun updateAchievementsAfterTaskCompletion() {
        val totalCompleted = database.taskDao().getTotalCompletedCount().first()
        val today = todayDate()
        val todayTasks = database.taskDao().getAllTasks().first()
        val completedToday = todayTasks.count { it.isCompleted && it.completedDate == today }
        val allTodayDone = todayTasks.isNotEmpty() && todayTasks.all {
            it.isCompleted && it.completedDate == today
        }
        val totalStars = settingsDataStore.settings.first().totalStars

        updateAchievement("first_task", totalCompleted.coerceAtMost(1))
        updateAchievement("five_tasks", totalCompleted)
        updateAchievement("ten_tasks", totalCompleted)
        updateAchievement("twenty_tasks", totalCompleted)
        updateAchievement("star_collector", totalStars)
        if (allTodayDone) {
            updateAchievement("perfect_day", 1)
            settingsDataStore.updateStreak(today, true)
            val streak = settingsDataStore.settings.first().streakDays
            updateAchievement("week_streak", streak)
        }
    }

    private suspend fun updateAchievement(id: String, value: Int) {
        val achievements = database.achievementDao().getAllAchievements().first()
        val achievement = achievements.find { it.id == id } ?: return
        val newValue = value.coerceAtLeast(achievement.currentValue)
        val unlocked = newValue >= achievement.targetValue
        database.achievementDao().update(
            achievement.copy(
                currentValue = newValue,
                isUnlocked = unlocked || achievement.isUnlocked
            )
        )
    }
}
