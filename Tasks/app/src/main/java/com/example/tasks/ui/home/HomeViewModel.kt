package com.example.tasks.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasks.data.SortOrder
import com.example.tasks.data.Task
import com.example.tasks.data.TasksRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeViewModel(private val tasksRepository: TasksRepository) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val homeUiState: StateFlow<HomeUiState> =
        combine(tasksRepository.selectedDate, tasksRepository.sortOrder) { date, sortOrder ->
            date to sortOrder
        }.flatMapLatest { (date, sortOrder) ->
            tasksRepository.getTasksStream(date).map { tasks ->
                HomeUiState(
                    selectedDate = date,
                    sortOrder = sortOrder,
                    tasks = sortOrder.sort(tasks)
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = HomeUiState()
        )

    fun selectDate(date: LocalDate) {
        viewModelScope.launch {
            tasksRepository.setSelectedDate(date)
        }
    }

    fun selectSortOrder(sortOrder: SortOrder) {
        viewModelScope.launch {
            tasksRepository.setSortOrder(sortOrder)
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class HomeUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val sortOrder: SortOrder = SortOrder.DATE_ASC,
    val tasks: List<Task> = listOf()
)
