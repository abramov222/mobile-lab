package com.example.tasks.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.tasks.R
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val MILLIS_IN_DAY = 86_400_000L

val appLocale: Locale = Locale.forLanguageTag("ru")

private val fullDateFormatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", appLocale)
private val shortDateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", appLocale)
private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", appLocale)

fun LocalDate.toFullText(): String =
    format(fullDateFormatter).replaceFirstChar { it.titlecase(appLocale) }

fun LocalDate.toShortText(): String = format(shortDateFormatter)

fun LocalTime.toText(): String = format(timeFormatter)

private fun LocalDate.toUtcMillis(): Long = toEpochDay() * MILLIS_IN_DAY

private fun Long.toLocalDate(): LocalDate = LocalDate.ofEpochDay(Math.floorDiv(this, MILLIS_IN_DAY))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDatePickerDialog(
    initialDate: LocalDate,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate) -> Unit,
    minDate: LocalDate? = null
) {
    val state = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.toUtcMillis(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                minDate == null || utcTimeMillis >= minDate.toUtcMillis()

            override fun isSelectableYear(year: Int): Boolean =
                minDate == null || year >= minDate.year
        }
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { state.selectedDateMillis?.let { onConfirm(it.toLocalDate()) } },
                enabled = state.selectedDateMillis != null
            ) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    ) {
        DatePicker(state = state)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskTimePickerDialog(
    initialTime: LocalTime,
    onDismiss: () -> Unit,
    onConfirm: (LocalTime) -> Unit
) {
    val state = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = true
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(LocalTime.of(state.hour, state.minute)) }) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
        text = { TimePicker(state = state) }
    )
}
