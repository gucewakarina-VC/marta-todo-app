package com.marta.todoapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.marta.todoapp.data.local.entity.AchievementEntity
import com.marta.todoapp.data.local.entity.RewardEntity
import com.marta.todoapp.data.local.entity.TaskEntity
import com.marta.todoapp.data.preferences.ThemeMode
import com.marta.todoapp.data.preferences.UserSettings
import com.marta.todoapp.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TodayUiState(
    val tasks: List<TaskEntity> = emptyList(),
    val totalStars: Int = 0,
    val childName: String = "Марта",
    val completedToday: Int = 0,
    val totalToday: Int = 0
)

class TodayViewModel(private val repository: AppRepository) : ViewModel() {

    val uiState: StateFlow<TodayUiState> = combine(
        repository.allTasks,
        repository.settings
    ) { tasks, settings ->
        val today = repository.todayDate()
        val todayTasks = tasks
        val completedToday = todayTasks.count { it.isCompleted && it.completedDate == today }
        TodayUiState(
            tasks = todayTasks,
            totalStars = settings.totalStars,
            childName = settings.childName,
            completedToday = completedToday,
            totalToday = todayTasks.size
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TodayUiState())

    fun toggleTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(task)
        }
    }

    fun completeTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.completeTask(task)
        }
    }
}

data class RewardsUiState(
    val availableRewards: List<RewardEntity> = emptyList(),
    val redeemedRewards: List<RewardEntity> = emptyList(),
    val totalStars: Int = 0
)

class RewardsViewModel(private val repository: AppRepository) : ViewModel() {

    val uiState: StateFlow<RewardsUiState> = combine(
        repository.availableRewards,
        repository.redeemedRewards,
        repository.settings
    ) { available, redeemed, settings ->
        RewardsUiState(
            availableRewards = available,
            redeemedRewards = redeemed,
            totalStars = settings.totalStars
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RewardsUiState())

    private val _redeemMessage = MutableStateFlow<String?>(null)
    val redeemMessage: StateFlow<String?> = _redeemMessage.asStateFlow()

    fun redeemReward(reward: RewardEntity) {
        viewModelScope.launch {
            val success = repository.redeemReward(reward)
            _redeemMessage.value = if (success) {
                "Ура! Ты получил(а) награду: ${reward.emoji} ${reward.title}!"
            } else {
                "Недостаточно звёздочек. Нужно ещё ${reward.starCost - uiState.value.totalStars} ⭐"
            }
        }
    }

    fun addReward(title: String, emoji: String, starCost: Int) {
        viewModelScope.launch {
            repository.addReward(title, emoji, starCost)
        }
    }

    fun deleteReward(reward: RewardEntity) {
        viewModelScope.launch {
            repository.deleteReward(reward)
        }
    }

    fun clearMessage() {
        _redeemMessage.value = null
    }
}

data class AchievementsUiState(
    val achievements: List<AchievementEntity> = emptyList(),
    val unlockedCount: Int = 0,
    val totalCount: Int = 0,
    val totalCompletedTasks: Int = 0,
    val totalStars: Int = 0,
    val streakDays: Int = 0
)

class AchievementsViewModel(private val repository: AppRepository) : ViewModel() {

    val uiState: StateFlow<AchievementsUiState> = combine(
        repository.achievements,
        repository.totalCompletedTasks,
        repository.settings
    ) { achievements, completedTasks, settings ->
        AchievementsUiState(
            achievements = achievements,
            unlockedCount = achievements.count { it.isUnlocked },
            totalCount = achievements.size,
            totalCompletedTasks = completedTasks,
            totalStars = settings.totalStars,
            streakDays = settings.streakDays
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AchievementsUiState())
}


class SettingsViewModel(private val repository: AppRepository) : ViewModel() {

    val settings: StateFlow<UserSettings> = repository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings())

    private val _pinVerified = MutableStateFlow(false)
    val pinVerified: StateFlow<Boolean> = _pinVerified.asStateFlow()

    private val _pinError = MutableStateFlow(false)
    val pinError: StateFlow<Boolean> = _pinError.asStateFlow()

    fun verifyPin(pin: String) {
        viewModelScope.launch {
            val valid = repository.verifyPin(pin)
            _pinVerified.value = valid
            _pinError.value = !valid
        }
    }

    fun resetPinVerification() {
        _pinVerified.value = false
        _pinError.value = false
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { repository.setThemeMode(mode) }
    }

    fun setChildName(name: String) {
        viewModelScope.launch { repository.setChildName(name) }
    }

    fun setParentPin(pin: String) {
        viewModelScope.launch { repository.setParentPin(pin) }
    }

    fun addTask(title: String, emoji: String, stars: Int) {
        viewModelScope.launch { repository.addTask(title, emoji, stars) }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch { repository.deleteTask(task) }
    }
}

class AppViewModel(private val repository: AppRepository) : ViewModel() {

    val settings: StateFlow<UserSettings> = repository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings())

    fun acceptAgreement() {
        viewModelScope.launch { repository.setAgreementAccepted(true) }
    }
}

class ViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AppViewModel::class.java) -> AppViewModel(repository) as T
            modelClass.isAssignableFrom(TodayViewModel::class.java) -> TodayViewModel(repository) as T
            modelClass.isAssignableFrom(RewardsViewModel::class.java) -> RewardsViewModel(repository) as T
            modelClass.isAssignableFrom(AchievementsViewModel::class.java) -> AchievementsViewModel(repository) as T
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> SettingsViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}
