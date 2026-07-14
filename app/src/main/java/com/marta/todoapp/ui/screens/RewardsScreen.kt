package com.marta.todoapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.marta.todoapp.data.local.entity.RewardEntity
import com.marta.todoapp.ui.components.AddItemDialog
import com.marta.todoapp.ui.components.EmojiCircle
import com.marta.todoapp.ui.components.PinDialog
import com.marta.todoapp.ui.components.StarsBadge
import com.marta.todoapp.ui.viewmodel.RewardsViewModel
import com.marta.todoapp.ui.viewmodel.SettingsViewModel

@Composable
fun RewardsScreen(
    rewardsViewModel: RewardsViewModel,
    settingsViewModel: SettingsViewModel
) {
    val uiState by rewardsViewModel.uiState.collectAsStateWithLifecycle()
    val pinVerified by settingsViewModel.pinVerified.collectAsStateWithLifecycle()
    val pinError by settingsViewModel.pinError.collectAsStateWithLifecycle()
    var showPinDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }
    val redeemMessage by rewardsViewModel.redeemMessage.collectAsStateWithLifecycle()

    LaunchedEffect(redeemMessage) {
        redeemMessage?.let {
            snackbarHostState.showSnackbar(it)
            rewardsViewModel.clearMessage()
        }
    }

    if (showPinDialog) {
        PinDialog(
            subtitle = "Для создания наград нужен PIN родителя",
            onDismiss = {
                showPinDialog = false
                settingsViewModel.resetPinVerification()
            },
            onConfirm = { pin ->
                settingsViewModel.verifyPin(pin)
            },
            showError = pinError
        )
    }

    LaunchedEffect(pinVerified) {
        if (pinVerified) {
            showPinDialog = false
            showAddDialog = true
            settingsViewModel.resetPinVerification()
        }
    }

    if (showAddDialog) {
        AddItemDialog(
            title = "Новая награда",
            nameLabel = "Название награды",
            extraLabel = "Стоимость (звёзды)",
            extraDefault = "5",
            onDismiss = { showAddDialog = false },
            onConfirm = { name, emoji, cost ->
                rewardsViewModel.addReward(name, emoji, cost)
                showAddDialog = false
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showPinDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Добавить награду")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Мои награды 🎁",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                StarsBadge(stars = uiState.totalStars)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Доступные") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Полученные") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            val rewards = if (selectedTab == 0) uiState.availableRewards else uiState.redeemedRewards

            if (rewards.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🎁", style = MaterialTheme.typography.displayMedium)
                        Text(
                            if (selectedTab == 0) "Пока нет наград.\nСоздай свою первую!"
                            else "Ты ещё не получил(а) наград.\nВыполняй дела и копи звёзды!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(rewards, key = { it.id }) { reward ->
                        RewardCard(
                            reward = reward,
                            totalStars = uiState.totalStars,
                            isAvailable = selectedTab == 0,
                            onRedeem = { rewardsViewModel.redeemReward(reward) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RewardCard(
    reward: RewardEntity,
    totalStars: Int,
    isAvailable: Boolean,
    onRedeem: () -> Unit
) {
    var showConfirm by remember { mutableStateOf(false) }
    val canAfford = totalStars >= reward.starCost

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("Получить награду?") },
            text = {
                Text("Потратить ${reward.starCost} ⭐ на «${reward.title}»?")
            },
            confirmButton = {
                Button(onClick = { showConfirm = false; onRedeem() }) {
                    Text("Да, получить!")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) {
                    Text("Пока нет")
                }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isAvailable) MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            EmojiCircle(emoji = reward.emoji, size = 56)

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reward.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${reward.starCost} звёзд",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (isAvailable) {
                Button(
                    onClick = { showConfirm = true },
                    enabled = canAfford,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Получить")
                }
            } else {
                Text("✅", style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}
