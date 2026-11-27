package com.example.tasks.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.time.LocalDate

class OfflineTasksRepository(
    private val taskDao: TaskDao,
    private val dataStore: DataStore<Preferences>
) : TasksRepository {

    private val preferences: Flow<Preferences> = dataStore.data
        .catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                throw it
            }
        }

    override val selectedDate: Flow<LocalDate> = preferences
        .map { prefs -> prefs[SELECTED_DATE]?.let(LocalDate::ofEpochDay) ?: LocalDate.now() }
        .distinctUntilChanged()

    override val sortOrder: Flow<SortOrder> = preferences
        .map { prefs ->
            prefs[SORT_ORDER]?.let { name -> SortOrder.entries.firstOrNull { it.name == name } }
                ?: SortOrder.DATE_ASC
        }
        .distinctUntilChanged()

    override suspend fun setSelectedDate(date: LocalDate) {
        dataStore.edit { it[SELECTED_DATE] = date.toEpochDay() }
    }

    override suspend fun setSortOrder(sortOrder: SortOrder) {
        dataStore.edit { it[SORT_ORDER] = sortOrder.name }
    }

    override fun getTasksStream(date: LocalDate): Flow<List<Task>> =
        taskDao.getTasksByDate(date.toEpochDay())

    override fun getTaskStream(id: Int): Flow<Task?> = taskDao.getTask(id)

    override suspend fun insertTask(task: Task) = taskDao.insert(task)

    override suspend fun updateTask(task: Task) = taskDao.update(task)

    override suspend fun deleteTask(task: Task) = taskDao.delete(task)

    private companion object {
        val SELECTED_DATE = longPreferencesKey("selected_date")
        val SORT_ORDER = stringPreferencesKey("sort_order")
    }
}
