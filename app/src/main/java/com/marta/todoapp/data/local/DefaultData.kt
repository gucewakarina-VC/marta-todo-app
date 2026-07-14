package com.marta.todoapp.data.local

import com.marta.todoapp.data.local.entity.AchievementEntity
import com.marta.todoapp.data.local.entity.TaskEntity

object DefaultData {
    val defaultTasks = listOf(
        TaskEntity(title = "Почистить зубы", emoji = "🪥", starsReward = 1, sortOrder = 0, isDefault = true),
        TaskEntity(title = "Сделать зарядку", emoji = "🏃", starsReward = 2, sortOrder = 1, isDefault = true),
        TaskEntity(title = "Убрать игрушки", emoji = "🧸", starsReward = 2, sortOrder = 2, isDefault = true),
        TaskEntity(title = "Сделать уроки", emoji = "📚", starsReward = 3, sortOrder = 3, isDefault = true),
        TaskEntity(title = "Помочь по дому", emoji = "🏠", starsReward = 2, sortOrder = 4, isDefault = true),
        TaskEntity(title = "Почитать книгу", emoji = "📖", starsReward = 2, sortOrder = 5, isDefault = true)
    )

    val defaultAchievements = listOf(
        AchievementEntity(
            id = "first_task",
            title = "Первое дело",
            description = "Выполни своё первое дело",
            emoji = "🌟",
            targetValue = 1
        ),
        AchievementEntity(
            id = "five_tasks",
            title = "Молодец!",
            description = "Выполни 5 дел",
            emoji = "⭐",
            targetValue = 5
        ),
        AchievementEntity(
            id = "ten_tasks",
            title = "Супергерой",
            description = "Выполни 10 дел",
            emoji = "🦸",
            targetValue = 10
        ),
        AchievementEntity(
            id = "twenty_tasks",
            title = "Чемпион",
            description = "Выполни 20 дел",
            emoji = "🏆",
            targetValue = 20
        ),
        AchievementEntity(
            id = "first_reward",
            title = "Первая награда",
            description = "Получи свою первую награду",
            emoji = "🎁",
            targetValue = 1
        ),
        AchievementEntity(
            id = "star_collector",
            title = "Собиратель звёзд",
            description = "Накопи 20 звёздочек",
            emoji = "✨",
            targetValue = 20
        ),
        AchievementEntity(
            id = "perfect_day",
            title = "Идеальный день",
            description = "Выполни все дела за один день",
            emoji = "🌈",
            targetValue = 1
        ),
        AchievementEntity(
            id = "week_streak",
            title = "Неделя успеха",
            description = "Выполняй дела 7 дней подряд",
            emoji = "🔥",
            targetValue = 7
        )
    )
}
