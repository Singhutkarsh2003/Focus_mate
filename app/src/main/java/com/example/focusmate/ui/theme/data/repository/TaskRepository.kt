package com.example.focusmate.ui.theme.data.repository

import com.example.focusmate.ui.theme.data.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    suspend fun upsertTask(task: Task)

    suspend fun deleteTask(taskId: Int)

    suspend fun getTaskById(taskId: Int):Task?

    fun getUpcomingTasksForSub(subjectInt: Int): Flow<List<Task>>

    fun getCompletedTaskForSub(subjectInt: Int): Flow<List<Task>>

    fun getAllUpcomingTask(): Flow<List<Task>>
}