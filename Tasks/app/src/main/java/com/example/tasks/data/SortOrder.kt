package com.example.tasks.data

enum class SortOrder {
    DATE_ASC,
    DATE_DESC,
    TITLE;

    fun sort(tasks: List<Task>): List<Task> = when (this) {
        DATE_ASC -> tasks.sortedWith(compareBy<Task> { it.date }.thenBy { it.time })
        DATE_DESC -> tasks.sortedWith(compareByDescending<Task> { it.date }.thenByDescending { it.time })
        TITLE -> tasks.sortedWith(compareBy<Task> { it.title.lowercase() }.thenBy { it.time })
    }
}
