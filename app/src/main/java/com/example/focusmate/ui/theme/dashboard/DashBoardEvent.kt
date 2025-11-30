package com.example.focusmate.ui.theme.dashboard

import androidx.compose.ui.graphics.Color
import com.example.focusmate.ui.theme.data.model.Session
import com.example.focusmate.ui.theme.data.model.Task

sealed class DashBoardEvent {

     data object SaveSub: DashBoardEvent()

     data object DeleteSession: DashBoardEvent()

     data class OnDeleteSessionBtn(val session: Session): DashBoardEvent()

    data class  OnTaskIsCompleteChange(val task: Task): DashBoardEvent()

    data class  OnSubCardColorChange(val colors: List<Color>): DashBoardEvent()

    data class  OnSubNameChange(val name: String): DashBoardEvent()

    data class  OnGoalStudyHrsChange(val hours: String): DashBoardEvent()
}