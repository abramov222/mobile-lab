package com.example.tasks.ui.task

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tasks.R
import com.example.tasks.ui.AppViewModelProvider
import com.example.tasks.ui.TasksTopAppBar
import com.example.tasks.ui.components.TaskDatePickerDialog
import com.example.tasks.ui.components.TaskTimePickerDialog
import com.example.tasks.ui.components.toShortText
import com.example.tasks.ui.components.toText
import com.example.tasks.ui.navigation.NavigationDestination
import com.example.tasks.ui.theme.TasksTheme
import kotlinx.coroutines.launch

object TaskEntryDestination : NavigationDestination {
    override val route = "task_entry"
    override val titleRes = R.string.add_task
}

@Composable
fun TaskEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: TaskEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TasksTopAppBar(
                title = stringResource(TaskEntryDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        TaskEntryBody(
            taskUiState = viewModel.taskUiState,
            onTaskValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveTask()
                    navigateBack()
                }
            },
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )
    }
}

@Composable
fun TaskEntryBody(
    taskUiState: TaskUiState,
    onTaskValueChange: (TaskDetails) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = modifier.padding(16.dp)
    ) {
        TaskInputForm(
            taskDetails = taskUiState.taskDetails,
            onValueChange = onTaskValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            enabled = taskUiState.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.save))
        }
    }
}

@Composable
fun TaskInputForm(
    taskDetails: TaskDetails,
    modifier: Modifier = Modifier,
    onValueChange: (TaskDetails) -> Unit = {}
) {
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showTimePicker by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = taskDetails.title,
            onValueChange = { onValueChange(taskDetails.copy(title = it)) },
            label = { Text(stringResource(R.string.task_title_req)) },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = taskDetails.description,
            onValueChange = { onValueChange(taskDetails.copy(description = it)) },
            label = { Text(stringResource(R.string.task_description)) },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.weight(3f)
            ) {
                Icon(imageVector = Icons.Filled.DateRange, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(taskDetails.date.toShortText())
            }
            Spacer(Modifier.width(8.dp))
            OutlinedButton(
                onClick = { showTimePicker = true },
                modifier = Modifier.weight(2f)
            ) {
                Text("${stringResource(R.string.task_time)} ${taskDetails.time.toText()}")
            }
        }
        Text(
            text = stringResource(R.string.required_fields),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 16.dp)
        )
    }

    if (showDatePicker) {
        TaskDatePickerDialog(
            initialDate = taskDetails.date,
            onDismiss = { showDatePicker = false },
            onConfirm = {
                showDatePicker = false
                onValueChange(taskDetails.copy(date = it))
            }
        )
    }
    if (showTimePicker) {
        TaskTimePickerDialog(
            initialTime = taskDetails.time,
            onDismiss = { showTimePicker = false },
            onConfirm = {
                showTimePicker = false
                onValueChange(taskDetails.copy(time = it))
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskEntryBodyPreview() {
    TasksTheme {
        TaskEntryBody(
            taskUiState = TaskUiState(TaskDetails(title = "Сдать лабораторную", description = "Room")),
            onTaskValueChange = {},
            onSaveClick = {}
        )
    }
}
