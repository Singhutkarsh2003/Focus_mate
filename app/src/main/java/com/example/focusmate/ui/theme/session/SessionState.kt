package com.example.focusmate.ui.theme.session

import com.example.focusmate.ui.theme.data.model.Session
import com.example.focusmate.ui.theme.data.model.Subjects

data class SessionState(
    val subjects: List<Subjects> = emptyList(),
    val sessions: List<Session> = emptyList(),
    val relatedToSubj: String? = null,
    val subId: Int? = null,
    val session: Session? = null
)
