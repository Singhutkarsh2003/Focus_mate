package com.example.focusmate.dao.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.example.focusmate.ui.theme.data.model.Session
import com.example.focusmate.ui.theme.data.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {


    @Insert
    suspend fun insertSession(session: Session)

    @Delete
    suspend fun deleteSession(session: Session)

    @Query("SELECT  * FROM Session")
    fun getAllSession() : Flow<List<Session>>

    @Query("SELECT * FROM Session WHERE sessionSubId = :subId ")
    fun getRecentSessionForSub(subId: Int ):  Flow<List<Session>>

    @Query("SELECT SUM(duration)FROM Session")
    fun getTotalSessionDuration() : Flow<Long>

    @Query("SELECT SUM(duration)FROM Session WHERE sessionSubId = :subId")
    fun getTotalSessionDurationBySubId(subId: Int) : Flow<Long>


    @Query("DELETE FROM Session WHERE sessionSubId = :subId")
    suspend fun deleteSessionBySub(subId: Int)



}