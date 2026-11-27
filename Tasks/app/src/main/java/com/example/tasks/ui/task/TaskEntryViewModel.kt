package com.example.tasks.ui.task

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasks.data.Task
import com.example.tasks.data.TasksRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class TaskEntryViewModel(private val tasksRepository: TasksRepository) : ViewModel() {
    var taskUiState by mutableStateOf(TaskUiState())
        private set

    init {
        viewModelScope.launch {
            val date = tasksRepository.selectedDate.first()
            val today = LocalDate.now()
            val details = taskUiState.taskDetails.copy(date = if (date < today) today else date)
            taskUiState = taskUiState.copy(taskDetails = details)
        }
    }

    fun updateUiState(taskDetails: TaskDetails) {
        taskUiState = TaskUiState(taskDetails = taskDetails, isEntryValid = validateInput(taskDetails))
    }

    suspend fun saveTask() {
        if (validateInput()) {
            tasksRepository.insertTask(taskUiState.taskDetails.toTask())
        }
    }

    private fun validateInput(uiState: TaskDetails = taskUiState.taskDetails): Boolean =
        uiState.title.isNotBlank()
}

data class TaskUiState(
    val taskDetails: TaskDetails = TaskDetails(),
    val isEntryValid: Boolean = false
)

data class TaskDetails(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val date: LocalDate = LocalDate.now(),
    val time: LocalTime = LocalTime.now().plusHours(1).withMinute(0).withSecond(0).withNano(0)
)

fun TaskDetails.toTask(): Task = Task(
    id = id,
    title = title.trim(),
    description = description.trim(),
    date = date.toEpochDay(),
    time = time.hour * 60 + time.minute
)

fun Task.toTaskDetails(): TaskDetails = TaskDetails(
    id = id,
    title = title,
    description = description,
    date = LocalDate.ofEpochDay(date),
    time = LocalTime.of(time / 60, time % 60)
)

fun Task.toTaskUiState(isEntryValid: Boolean = false): TaskUiState = TaskUiState(
    taskDetails = toTaskDetails(),
    isEntryValid = isEntryValid
)
