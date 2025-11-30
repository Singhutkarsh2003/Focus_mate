package com.example.focusmate.ui.theme.dashboard

import androidx.compose.ui.graphics.Color
import com.example.focusmate.ui.theme.data.model.Session
import com.example.focusmate.ui.theme.data.model.Subjects

data class DashboardState(
    val totalSubCount: Int =0,
    val totalStudiesHrs: Float = 0f,
    val totalGoalHrs : Float = 0f,
    val subject: List<Subjects> = emptyList(),
    val subName: String = "",
    val goalStudyHrs : String ="",
    val subjectCardColor: List<Color> = Subjects.subCardColor.random(),
    val session: Session? = null
)
