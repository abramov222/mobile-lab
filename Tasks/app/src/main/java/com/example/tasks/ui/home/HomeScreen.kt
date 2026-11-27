package com.example.tasks.ui.home

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tasks.R
import com.example.tasks.data.SortOrder
import com.example.tasks.data.Task
import com.example.tasks.ui.AppViewModelProvider
import com.example.tasks.ui.TasksTopAppBar
import com.example.tasks.ui.components.TaskDatePickerDialog
import com.example.tasks.ui.components.appLocale
import com.example.tasks.ui.components.toFullText
import com.example.tasks.ui.components.toText
import com.example.tasks.ui.navigation.NavigationDestination
import com.example.tasks.ui.task.toTaskDetails
import com.example.tasks.ui.theme.TasksTheme
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

private const val DAYS_AHEAD = 365

@Composable
fun HomeScreen(
    navigateToTaskEntry: () -> Unit,
    navigateToTaskDetails: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.homeUiState.collectAsState()
    var showCalendar by rememberSaveable { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    val today = remember { LocalDate.now() }

    Scaffold(
        modifier = modifier,
        topBar = {
            TasksTopAppBar(
                title = stringResource(HomeDestination.titleRes),
                canNavigateBack = false,
                actions = {
                    IconButton(onClick = { showCalendar = true }) {
                        Icon(
                            imageVector = Icons.Filled.DateRange,
                            contentDescription = stringResource(R.string.calendar)
                        )
                    }
                    Box {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.List,
                                contentDescription = stringResource(R.string.sort)
                            )
                        }
                        SortMenu(
                            expanded = showSortMenu,
                            current = uiState.sortOrder,
                            onDismiss = { showSortMenu = false },
                            onSelect = {
                                showSortMenu = false
                                viewModel.selectSortOrder(it)
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToTaskEntry,
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_task)
                )
            }
        }
    ) { innerPadding ->
        HomeBody(
            uiState = uiState,
            today = today,
            onDateSelected = viewModel::selectDate,
            onTaskClick = navigateToTaskDetails,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
    }

    if (showCalendar) {
        TaskDatePickerDialog(
            initialDate = if (uiState.selectedDate < today) today else uiState.selectedDate,
            minDate = today,
            onDismiss = { showCalendar = false },
            onConfirm = {
                showCalendar = false
                viewModel.selectDate(it)
            }
        )
    }
}

@Composable
private fun SortMenu(
    expanded: Boolean,
    current: SortOrder,
    onDismiss: () -> Unit,
    onSelect: (SortOrder) -> Unit
) {
    DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) {
        SortOrder.entries.forEach { sortOrder ->
            DropdownMenuItem(
                text = { Text(stringResource(sortOrder.labelRes())) },
                onClick = { onSelect(sortOrder) },
                leadingIcon = {
                    if (sortOrder == current) {
                        Icon(imageVector = Icons.Filled.Check, contentDescription = null)
                    } else {
                        Spacer(Modifier.size(24.dp))
                    }
                }
            )
        }
    }
}

@StringRes
private fun SortOrder.labelRes(): Int = when (this) {
    SortOrder.DATE_ASC -> R.string.sort_date_asc
    SortOrder.DATE_DESC -> R.string.sort_date_desc
    SortOrder.TITLE -> R.string.sort_title
}

@Composable
private fun HomeBody(
    uiState: HomeUiState,
    today: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onTaskClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        DateHeader(
            selectedDate = uiState.selectedDate,
            today = today,
            onDateSelected = onDateSelected
        )
        DateStrip(
            selectedDate = uiState.selectedDate,
            today = today,
            onDateSelected = onDateSelected
        )
        if (uiState.tasks.isEmpty()) {
            Text(
                text = stringResource(R.string.no_tasks),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, start = 16.dp, end = 16.dp)
            )
        } else {
            TaskList(
                tasks = uiState.tasks,
                onTaskClick = { onTaskClick(it.id) }
            )
        }
    }
}

@Composable
private fun DateHeader(
    selectedDate: LocalDate,
    today: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        IconButton(
            onClick = { onDateSelected(selectedDate.minusDays(1)) },
            enabled = selectedDate > today
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = stringResource(R.string.previous_day)
            )
        }
        Text(
            text = selectedDate.toFullText(),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = { onDateSelected(selectedDate.plusDays(1)) }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = stringResource(R.string.next_day)
            )
        }
    }
}

@Composable
private fun DateStrip(
    selectedDate: LocalDate,
    today: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    LaunchedEffect(selectedDate) {
        val index = ChronoUnit.DAYS.between(today, selectedDate).toInt()
        if (index in 0 until DAYS_AHEAD) {
            listState.animateScrollToItem((index - 2).coerceAtLeast(0))
        }
    }
    LazyRow(
        state = listState,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        items(count = DAYS_AHEAD, key = { it }) { index ->
            val date = today.plusDays(index.toLong())
            DayCard(
                date = date,
                selected = date == selectedDate,
                onClick = { onDateSelected(date) }
            )
        }
    }
}

@Composable
private fun DayCard(
    date: LocalDate,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        colors = if (selected) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        modifier = modifier.width(56.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, appLocale),
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = date.month.getDisplayName(TextStyle.SHORT, appLocale),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun TaskList(
    tasks: List<Task>,
    onTaskClick: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        items(items = tasks, key = { it.id }) { task ->
            TaskCard(
                task = task,
                modifier = Modifier.clickable { onTaskClick(task) }
            )
        }
    }
}

@Composable
private fun TaskCard(
    task: Task,
    modifier: Modifier = Modifier
) {
    val details = task.toTaskDetails()
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = details.time.toText(),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = details.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (details.description.isNotBlank()) {
                    Text(
                        text = details.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeBodyPreview() {
    val today = LocalDate.now()
    TasksTheme {
        HomeBody(
            uiState = HomeUiState(
                selectedDate = today,
                tasks = listOf(
                    Task(1, "Лабораторная 9", "Room и DataStore", today.toEpochDay(), 600),
                    Task(2, "Тренировка", "", today.toEpochDay(), 1080)
                )
            ),
            today = today,
            onDateSelected = {},
            onTaskClick = {}
        )
    }
}
