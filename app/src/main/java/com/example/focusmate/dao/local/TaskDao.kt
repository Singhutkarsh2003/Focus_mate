package com.example.focusmate.dao.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.focusmate.ui.theme.data.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {



    @Upsert
    suspend fun upsertTask(task: Task)

    @Query("DELETE FROM task WHERE taskId =:taskId")
    suspend fun deleteTask(taskId : Int)

    @Query("DELETE FROM Task WHERE taskSubId =:subId")
    suspend fun deleteTaskBySubId(subId : Int)

    @Query("SELECT * FROM Task WHERE taskId = :taskId ")
    suspend fun getTaskById(taskId: Int ): Task?

    @Query("SELECT * FROM Task WHERE taskSubId = :subId ")
     fun getTaskForSubject(subId: Int ):  Flow<List<Task>>

    @Query("SELECT  * FROM Task")
    fun getAllTask() : Flow<List<Task>>


}