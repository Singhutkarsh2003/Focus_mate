package com.example.focusmate.dao.repository

import com.example.focusmate.dao.local.SessionDao
import com.example.focusmate.ui.theme.data.model.Session
import com.example.focusmate.ui.theme.data.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val sessionDao : SessionDao
): SessionRepository {

    override suspend fun insertSession(session: Session) {
       sessionDao.insertSession(session)
    }

    override suspend fun deleteSession(session: Session) {
        sessionDao.deleteSession(session)
    }

    override fun getAllSessions(): Flow<List<Session>> {
        return sessionDao.getAllSession()
            .map {sessions -> sessions.sortedByDescending {  it.date } }
    }

    override fun getRecentSession(): Flow<List<Session>> {
        return sessionDao.getAllSession()
            .map {sessions -> sessions.sortedByDescending {  it.date } }
            .take(5)
    }

    override fun getRecentSessionSub(subjectId: Int): Flow<List<Session>> {
        return sessionDao.getRecentSessionForSub(subjectId)
            .map {sessions -> sessions.sortedByDescending {  it.date } }
            .take(10)
    }

    override fun getTotalSessionDuration(): Flow<Long> {
        return  sessionDao.getTotalSessionDuration()
    }

    override fun getTotalSessionDurationSubId(subjectId: Int): Flow<Long> {
        return sessionDao.getTotalSessionDurationBySubId(subjectId)
    }
}