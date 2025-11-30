package com.example.focusmate.ui.theme.session

import com.example.focusmate.ui.theme.data.model.Session
import com.example.focusmate.ui.theme.data.model.Subjects

sealed class SessionEvent {

    data class OnRelatedSubChange(val subjects: Subjects): SessionEvent()

    data class SaveSession(val duration: Long): SessionEvent()

    data class onDeleteSessionButtonClick(val session: Session): SessionEvent()

    data object DeleteSession : SessionEvent()

    data object  CheckSubId: SessionEvent()

    data class UpdateSubIdAndRelateSub(
        val subjectId: Int?,
        val relatedToSub: String?
    ): SessionEvent()

}