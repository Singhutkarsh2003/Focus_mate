package com.example.focusmate.ui.theme.focusarea

import androidx.compose.ui.graphics.Color
import com.example.focusmate.ui.theme.data.model.Session
import com.example.focusmate.ui.theme.data.model.Task

sealed class SubEvent {

     data object UpdateSub : SubEvent()

     data object DeleteSub: SubEvent()

     data object DeleteSession : SubEvent()

    data object UpdateProgress : SubEvent()

     data class OnTaskIsCompleteChange(val task : Task): SubEvent()

    data class OnSubCardColorChange( val color: List<Color>): SubEvent()

    data class  OnSubNameChange( val name: String): SubEvent()

    data class  OnGoalStudyHrsChange(val hours : String): SubEvent()

    data class OnDeleteSessionBtn( val session: Session): SubEvent()
}