package com.marta.todoapp.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

data class UserSettings(
    val totalStars: Int = 0,
    val parentPin: String = "1234",
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val isAgreementAccepted: Boolean = false,
    val childName: String = "Марта",
    val streakDays: Int = 0,
    val lastActiveDate: String = "",
    val isFirstLaunch: Boolean = true
)

class SettingsDataStore(private val context: Context) {

    private object Keys {
        val TOTAL_STARS = intPreferencesKey("total_stars")
        val PARENT_PIN = stringPreferencesKey("parent_pin")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val AGREEMENT_ACCEPTED = booleanPreferencesKey("agreement_accepted")
        val CHILD_NAME = stringPreferencesKey("child_name")
        val STREAK_DAYS = intPreferencesKey("streak_days")
        val LAST_ACTIVE_DATE = stringPreferencesKey("last_active_date")
        val FIRST_LAUNCH = booleanPreferencesKey("first_launch")
    }

    val settings: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        UserSettings(
            totalStars = prefs[Keys.TOTAL_STARS] ?: 0,
            parentPin = prefs[Keys.PARENT_PIN] ?: "1234",
            themeMode = ThemeMode.valueOf(prefs[Keys.THEME_MODE] ?: ThemeMode.SYSTEM.name),
            isAgreementAccepted = prefs[Keys.AGREEMENT_ACCEPTED] ?: false,
            childName = prefs[Keys.CHILD_NAME] ?: "Марта",
            streakDays = prefs[Keys.STREAK_DAYS] ?: 0,
            lastActiveDate = prefs[Keys.LAST_ACTIVE_DATE] ?: "",
            isFirstLaunch = prefs[Keys.FIRST_LAUNCH] ?: true
        )
    }

    suspend fun setTotalStars(stars: Int) {
        context.dataStore.edit { it[Keys.TOTAL_STARS] = stars }
    }

    suspend fun addStars(amount: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.TOTAL_STARS] ?: 0
            prefs[Keys.TOTAL_STARS] = current + amount
        }
    }

    suspend fun spendStars(amount: Int): Boolean {
        var success = false
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.TOTAL_STARS] ?: 0
            if (current >= amount) {
                prefs[Keys.TOTAL_STARS] = current - amount
                success = true
            }
        }
        return success
    }

    suspend fun setParentPin(pin: String) {
        context.dataStore.edit { it[Keys.PARENT_PIN] = pin }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    suspend fun setAgreementAccepted(accepted: Boolean) {
        context.dataStore.edit { it[Keys.AGREEMENT_ACCEPTED] = accepted }
    }

    suspend fun setChildName(name: String) {
        context.dataStore.edit { it[Keys.CHILD_NAME] = name }
    }

    suspend fun updateStreak(date: String, completedToday: Boolean) {
        context.dataStore.edit { prefs ->
            val lastDate = prefs[Keys.LAST_ACTIVE_DATE] ?: ""
            val currentStreak = prefs[Keys.STREAK_DAYS] ?: 0

            if (completedToday) {
                val newStreak = when {
                    lastDate.isEmpty() -> 1
                    lastDate == date -> currentStreak
                    isConsecutiveDay(lastDate, date) -> currentStreak + 1
                    else -> 1
                }
                prefs[Keys.STREAK_DAYS] = newStreak
                prefs[Keys.LAST_ACTIVE_DATE] = date
            }
        }
    }

    suspend fun setFirstLaunchDone() {
        context.dataStore.edit { it[Keys.FIRST_LAUNCH] = false }
    }

    private fun isConsecutiveDay(previous: String, current: String): Boolean {
        return try {
            val prev = java.time.LocalDate.parse(previous)
            val curr = java.time.LocalDate.parse(current)
            prev.plusDays(1) == curr
        } catch (_: Exception) {
            false
        }
    }
}
