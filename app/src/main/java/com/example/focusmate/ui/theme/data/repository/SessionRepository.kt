package com.example.focusmate.ui.theme.data.repository

import com.example.focusmate.ui.theme.data.model.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository {

    suspend fun  insertSession(session: Session)

    suspend fun deleteSession(session: Session)

    fun getAllSessions(): Flow<List<Session>>

    fun getRecentSession(): Flow<List<Session>>

    fun getRecentSessionSub(subjectId: Int): Flow<List<Session>>

    fun getTotalSessionDuration(): Flow<Long>

    fun getTotalSessionDurationSubId(subjectId: Int): Flow<Long>
}