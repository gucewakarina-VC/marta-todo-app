package com.marta.todoapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Today : Screen("today", "Сегодня", Icons.Filled.Today)
    data object Rewards : Screen("rewards", "Награды", Icons.Outlined.CardGiftcard)
    data object Achievements : Screen("achievements", "Достижения", Icons.Filled.EmojiEvents)
    data object Settings : Screen("settings", "Настройки", Icons.Filled.Settings)

    companion object {
        val bottomNavItems = listOf(Today, Rewards, Achievements, Settings)
    }
}
