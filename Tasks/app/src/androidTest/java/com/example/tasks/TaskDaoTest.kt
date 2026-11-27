package com.example.tasks

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.tasks.data.Task
import com.example.tasks.data.TaskDao
import com.example.tasks.data.TasksDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class TaskDaoTest {
    private lateinit var taskDao: TaskDao
    private lateinit var tasksDatabase: TasksDatabase

    private val task1 = Task(1, "Лабораторная", "Room", 20_000, 600)
    private val task2 = Task(2, "Спортзал", "", 20_000, 1080)
    private val task3 = Task(3, "Встреча", "", 20_001, 540)

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        tasksDatabase = Room.inMemoryDatabaseBuilder(context, TasksDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        taskDao = tasksDatabase.taskDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        tasksDatabase.close()
    }

    private suspend fun addAllTasksToDb() {
        taskDao.insert(task1)
        taskDao.insert(task2)
        taskDao.insert(task3)
    }

    @Test
    @Throws(Exception::class)
    fun daoInsert_insertsTaskIntoDB() = runBlocking {
        taskDao.insert(task1)
        assertEquals(task1, taskDao.getTask(1).first())
    }

    @Test
    @Throws(Exception::class)
    fun daoGetTasksByDate_returnsOnlyTasksOfThatDate() = runBlocking {
        addAllTasksToDb()
        val tasks = taskDao.getTasksByDate(20_000).first()
        assertEquals(listOf(task1, task2), tasks)
    }

    @Test
    @Throws(Exception::class)
    fun daoUpdate_updatesTaskInDB() = runBlocking {
        addAllTasksToDb()
        val updated = task1.copy(title = "Отчёт", time = 720)
        taskDao.update(updated)
        assertEquals(updated, taskDao.getTask(1).first())
    }

    @Test
    @Throws(Exception::class)
    fun daoDelete_deletesTaskFromDB() = runBlocking {
        addAllTasksToDb()
        taskDao.delete(task3)
        assertNull(taskDao.getTask(3).first())
        assertTrue(taskDao.getTasksByDate(20_001).first().isEmpty())
    }
}
