package com.example.tasks.data

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface TasksRepository {
    val selectedDate: Flow<LocalDate>

    val sortOrder: Flow<SortOrder>

    suspend fun setSelectedDate(date: LocalDate)

    suspend fun setSortOrder(sortOrder: SortOrder)

    fun getTasksStream(date: LocalDate): Flow<List<Task>>

    fun getTaskStream(id: Int): Flow<Task?>

    suspend fun insertTask(task: Task)

    suspend fun updateTask(task: Task)

    suspend fun deleteTask(task: Task)
}
