package com.example.tasks

import com.example.tasks.data.SortOrder
import com.example.tasks.data.Task
import org.junit.Assert.assertEquals
import org.junit.Test

class SortOrderTest {
    private val morning = Task(1, "Пробежка", "", 20_000, 7 * 60)
    private val noon = Task(2, "Обед", "", 20_000, 13 * 60)
    private val evening = Task(3, "Английский", "", 20_000, 19 * 60)
    private val tomorrow = Task(4, "Встреча", "", 20_001, 9 * 60)
    private val tasks = listOf(noon, tomorrow, evening, morning)

    @Test
    fun dateAsc_sortsByDateThenTime() {
        assertEquals(listOf(morning, noon, evening, tomorrow), SortOrder.DATE_ASC.sort(tasks))
    }

    @Test
    fun dateDesc_sortsByDateThenTimeDescending() {
        assertEquals(listOf(tomorrow, evening, noon, morning), SortOrder.DATE_DESC.sort(tasks))
    }

    @Test
    fun title_sortsAlphabeticallyIgnoringCase() {
        val lower = Task(5, "аптека", "", 20_000, 10 * 60)
        assertEquals(
            listOf(evening, lower, tomorrow, noon, morning),
            SortOrder.TITLE.sort(tasks + lower)
        )
    }
}
