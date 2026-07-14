# Мои дела и награды

Android-приложение для детей, которое мотивирует выполнять ежедневные дела в игровой форме.

## Технологии

- Kotlin
- Jetpack Compose + Material 3
- Room (локальная база данных)
- DataStore (настройки)
- Navigation Compose

## Функциональность

- **Сегодня** — список ежедневных дел с отметкой выполнения и начислением звёздочек
- **Награды** — создание наград и обмен звёздочек (родительский PIN)
- **Достижения** — прогресс и разблокируемые достижения
- **Настройки** — имя ребёнка, тема, управление делами, PIN-код
- Светлая и тёмная тема
- Пользовательское соглашение при первом запуске
- Родительский режим с PIN-кодом (по умолчанию: `1234`)

## Сборка APK

### Требования

- Android Studio Hedgehog (2023.1.1) или новее
- JDK 17
- Android SDK 34

### Через Android Studio

1. Откройте папку проекта в Android Studio
2. Дождитесь синхронизации Gradle
3. **Build → Build Bundle(s) / APK(s) → Build APK(s)**

### Через командную строку

```bash
./gradlew assembleDebug
```

APK будет в `app/build/outputs/apk/debug/app-debug.apk`

## Структура проекта

```
app/src/main/java/com/marta/todoapp/
├── data/
│   ├── local/          # Room: entities, DAOs, database
│   ├── preferences/    # DataStore настройки
│   └── repository/     # Единый репозиторий
├── ui/
│   ├── components/     # Общие UI-компоненты и диалоги
│   ├── navigation/     # Маршруты навигации
│   ├── screens/        # Экраны приложения
│   ├── theme/          # Material 3 тема
│   └── viewmodel/      # ViewModels
├── util/               # Утилиты
├── MainActivity.kt
└── MartaTodoApplication.kt
```

## PIN-код по умолчанию

При первом запуске PIN-код родителя: **1234**. Его можно изменить в настройках.
