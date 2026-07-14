package com.marta.todoapp

import android.app.Application
import androidx.room.Room
import com.marta.todoapp.data.local.AppDatabase
import com.marta.todoapp.data.preferences.SettingsDataStore
import com.marta.todoapp.data.repository.AppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MartaTodoApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "marta_todo_db"
        ).build()
    }

    val settingsDataStore: SettingsDataStore by lazy {
        SettingsDataStore(applicationContext)
    }

    val repository: AppRepository by lazy {
        AppRepository(database, settingsDataStore)
    }

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            repository.initializeIfNeeded()
        }
    }
}
