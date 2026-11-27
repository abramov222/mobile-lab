package com.example.tasks.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private const val TASK_PREFERENCES_NAME = "task_preferences"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = TASK_PREFERENCES_NAME
)

interface AppContainer {
    val tasksRepository: TasksRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val tasksRepository: TasksRepository by lazy {
        OfflineTasksRepository(
            taskDao = TasksDatabase.getDatabase(context).taskDao(),
            dataStore = context.dataStore
        )
    }
}
