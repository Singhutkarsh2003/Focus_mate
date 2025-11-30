package com.example.focusmate.dao.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.focusmate.ui.theme.data.model.Subjects
import kotlinx.coroutines.flow.Flow

@Dao
interface SubDao {

    @Upsert
    suspend fun upsertSub(subjects: Subjects)

    @Query("SELECT COUNT(*) FROM SUBJECTS")
    fun getTotalSubjectCount(): Flow<Int>

    @Query("SELECT SUM(goalHour)FROM SUBJECTS")
    fun getTotalGoalHours() : Flow<Float>

    @Query("SELECT * FROM Subjects WHERE subId  = :subId ")
    suspend fun getSubjectById(subId: Int ): Subjects?

    @Query("DELETE FROM Subjects WHERE subId =:subId")
    suspend fun deleteSubject(subId : Int)

    @Query("SELECT  * FROM subjects")
    fun getAllSubject() : Flow<List<Subjects>>
}