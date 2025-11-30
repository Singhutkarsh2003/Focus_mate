package com.example.focusmate.dao.repository

import com.example.focusmate.dao.local.SessionDao
import com.example.focusmate.dao.local.SubDao
import com.example.focusmate.dao.local.TaskDao
import com.example.focusmate.ui.theme.data.model.Subjects
import com.example.focusmate.ui.theme.data.repository.SubRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SubRepositoryImpl @Inject constructor(
    private  val subjectDao: SubDao,
    private val taskDao: TaskDao,
    private val sessionDao: SessionDao
): SubRepository {

    override suspend fun upsertSubject(subjects: Subjects) {
        subjectDao.upsertSub(subjects)
    }

    override fun setTotalSubjectCount(): Flow<Int> {
        return subjectDao.getTotalSubjectCount()
    }

    override fun getTotalGoalHrs(): Flow<Float> {
       return subjectDao.getTotalGoalHours()
    }

    override suspend fun deleteSubject(subjectInt: Int) {
        taskDao.deleteTaskBySubId(subjectInt)
        sessionDao.deleteSessionBySub(subjectInt)
        subjectDao.deleteSubject(subjectInt)
    }

    override suspend fun getSubById(subjectInt: Int): Subjects? {
       return  subjectDao.getSubjectById(subjectInt)
    }

    override fun getAllSub(): Flow<List<Subjects>> {
       return subjectDao.getAllSubject()
    }
}