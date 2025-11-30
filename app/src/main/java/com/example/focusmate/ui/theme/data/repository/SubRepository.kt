package com.example.focusmate.ui.theme.data.repository

import com.example.focusmate.ui.theme.data.model.Subjects
import kotlinx.coroutines.flow.Flow

interface SubRepository {

    suspend fun upsertSubject(subjects: Subjects)

    fun setTotalSubjectCount(): Flow<Int>

    fun getTotalGoalHrs(): Flow<Float>

    suspend fun  deleteSubject(subjectInt: Int)

    suspend fun  getSubById(subjectInt: Int): Subjects?

    fun getAllSub(): Flow<List<Subjects>>
}