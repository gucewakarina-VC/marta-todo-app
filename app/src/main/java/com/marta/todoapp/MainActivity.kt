package com.marta.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.marta.todoapp.ui.components.UserAgreementDialog
import com.marta.todoapp.ui.navigation.Screen
import com.marta.todoapp.ui.screens.AchievementsScreen
import com.marta.todoapp.ui.screens.RewardsScreen
import com.marta.todoapp.ui.screens.SettingsScreen
import com.marta.todoapp.ui.screens.TodayScreen
import com.marta.todoapp.ui.theme.MartaTodoTheme
import com.marta.todoapp.ui.viewmodel.AchievementsViewModel
import com.marta.todoapp.ui.viewmodel.AppViewModel
import com.marta.todoapp.ui.viewmodel.RewardsViewModel
import com.marta.todoapp.ui.viewmodel.SettingsViewModel
import com.marta.todoapp.ui.viewmodel.TodayViewModel
import com.marta.todoapp.ui.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = (application as MartaTodoApplication).repository
        val factory = ViewModelFactory(repository)

        setContent {
            val appViewModel: AppViewModel = viewModel(factory = factory)
            val settings by appViewModel.settings.collectAsStateWithLifecycle()
            var showAgreement by remember { mutableStateOf(false) }

            LaunchedEffect(settings.isAgreementAccepted) {
                showAgreement = !settings.isAgreementAccepted
            }

            MartaTodoTheme(themeMode = settings.themeMode) {
                if (showAgreement) {
                    UserAgreementDialog(
                        onAccept = {
                            appViewModel.acceptAgreement()
                            showAgreement = false
                        },
                        onDecline = {
                            finish()
                        }
                    )
                } else {
                    MartaTodoApp(factory = factory)
                }
            }
        }
    }
}

@Composable
fun MartaTodoApp(factory: ViewModelFactory) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val todayViewModel: TodayViewModel = viewModel(factory = factory)
    val rewardsViewModel: RewardsViewModel = viewModel(factory = factory)
    val achievementsViewModel: AchievementsViewModel = viewModel(factory = factory)
    val settingsViewModel: SettingsViewModel = viewModel(factory = factory)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                Screen.bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Today.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Today.route) {
                TodayScreen(viewModel = todayViewModel)
            }
            composable(Screen.Rewards.route) {
                RewardsScreen(
                    rewardsViewModel = rewardsViewModel,
                    settingsViewModel = settingsViewModel
                )
            }
            composable(Screen.Achievements.route) {
                AchievementsScreen(viewModel = achievementsViewModel)
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    viewModel = settingsViewModel,
                    todayViewModel = todayViewModel
                )
            }
        }
    }
}
