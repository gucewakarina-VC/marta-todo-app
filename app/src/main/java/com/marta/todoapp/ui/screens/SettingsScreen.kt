package com.marta.todoapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.marta.todoapp.data.preferences.ThemeMode
import com.marta.todoapp.ui.viewmodel.TodayViewModel
import com.marta.todoapp.ui.components.AddItemDialog
import com.marta.todoapp.ui.components.ChangePinDialog
import com.marta.todoapp.ui.components.PinDialog
import com.marta.todoapp.ui.components.UserAgreementDialog
import com.marta.todoapp.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    todayViewModel: TodayViewModel
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val todayState by todayViewModel.uiState.collectAsStateWithLifecycle()
    val pinVerified by viewModel.pinVerified.collectAsStateWithLifecycle()
    val pinError by viewModel.pinError.collectAsStateWithLifecycle()
    var showPinDialog by remember { mutableStateOf(true) }
    var showChangePinDialog by remember { mutableStateOf(false) }
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var showAgreement by remember { mutableStateOf(false) }
    var childName by remember(settings.childName) { mutableStateOf(settings.childName) }

    if (showPinDialog && !pinVerified) {
        PinDialog(
            subtitle = "Для изменения настроек нужен PIN родителя",
            onDismiss = { },
            onConfirm = { pin -> viewModel.verifyPin(pin) },
            showError = pinError
        )
    }

    LaunchedEffect(pinVerified) {
        if (pinVerified) showPinDialog = false
    }

    if (showChangePinDialog) {
        ChangePinDialog(
            onDismiss = { showChangePinDialog = false },
            onConfirm = { pin ->
                viewModel.setParentPin(pin)
                showChangePinDialog = false
            }
        )
    }

    if (showAddTaskDialog) {
        AddItemDialog(
            title = "Новое дело",
            nameLabel = "Название дела",
            extraLabel = "Звёзды за выполнение",
            onDismiss = { showAddTaskDialog = false },
            onConfirm = { name, emoji, stars ->
                viewModel.addTask(name, emoji, stars)
                showAddTaskDialog = false
            }
        )
    }

    if (showAgreement) {
        UserAgreementDialog(
            onAccept = { showAgreement = false },
            onDecline = { showAgreement = false }
        )
    }

    if (!pinVerified && showPinDialog) return

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Настройки ⚙️",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            SettingsSection(title = "Профиль ребёнка") {
                OutlinedTextField(
                    value = childName,
                    onValueChange = {
                        childName = it
                        viewModel.setChildName(it)
                    },
                    label = { Text("Имя") },
                    leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        item {
            SettingsSection(title = "Тема оформления") {
                ThemeMode.entries.forEach { mode ->
                    val (label, icon) = when (mode) {
                        ThemeMode.LIGHT -> "Светлая" to Icons.Filled.LightMode
                        ThemeMode.DARK -> "Тёмная" to Icons.Filled.DarkMode
                        ThemeMode.SYSTEM -> "Как в системе" to Icons.Filled.PhoneAndroid
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.setThemeMode(mode) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(icon, contentDescription = null, modifier = Modifier.padding(end = 12.dp))
                        Text(label, modifier = Modifier.weight(1f))
                        RadioButton(
                            selected = settings.themeMode == mode,
                            onClick = { viewModel.setThemeMode(mode) }
                        )
                    }
                }
            }
        }

        item {
            SettingsSection(title = "Управление делами") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Добавить новое дело")
                    IconButton(onClick = { showAddTaskDialog = true }) {
                        Text("+", style = MaterialTheme.typography.headlineSmall)
                    }
                }
                todayState.tasks.filter { !it.isDefault }.forEach { task ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${task.emoji} ${task.title}", modifier = Modifier.weight(1f))
                        IconButton(onClick = { viewModel.deleteTask(task) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Удалить")
                        }
                    }
                }
                if (todayState.tasks.none { !it.isDefault }) {
                    Text(
                        "Дополнительных дел пока нет",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            SettingsSection(title = "Родительский режим") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showChangePinDialog = true }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Lock, contentDescription = null)
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Text("Изменить PIN-код", modifier = Modifier.weight(1f))
                }
            }
        }

        item {
            SettingsSection(title = "О приложении") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showAgreement = true }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📋", modifier = Modifier.padding(end = 12.dp))
                    Text("Пользовательское соглашение", modifier = Modifier.weight(1f))
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    text = "Мои дела и награды v1.0",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Приложение для мотивации детей выполнять ежедневные дела",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}
