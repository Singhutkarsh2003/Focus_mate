package com.example.focusmate.ui.theme.focusarea

import androidx.compose.ui.graphics.Color
import com.example.focusmate.ui.theme.data.model.Session
import com.example.focusmate.ui.theme.data.model.Subjects
import com.example.focusmate.ui.theme.data.model.Task

data class SubState (

    val currentSubId: Int? = null,
    val subName : String ="",
    val goalStudyHrs : String = "",
    val subCardColor : List<Color> = Subjects.subCardColor.random(),
    val studiedHrs : Float = 0f,
    val progress: Float = 0f,
    val recentSession: List<Session> = emptyList(),
    val upComingTask: List<Task> = emptyList(),
    val completedTask: List<Task> = emptyList(),
    val session: Session? = null,
    val isLoading : Boolean = false

)