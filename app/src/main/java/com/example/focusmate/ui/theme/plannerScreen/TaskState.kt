package com.example.focusmate.ui.theme.plannerScreen

import com.example.focusmate.ui.theme.data.model.Subjects
import com.example.focusmate.ui.theme.util.Priority

data class TaskState(
    val title: String ="",
    val description: String = "",
    val dueDate: Long? = null,
    val isTaskComplete: Boolean = false,
    val priority: Priority = Priority.LOW,
    val relatedToSub: String? = null,
    val subjects: List<Subjects> = emptyList(),
    val subjectId: Int? = null,
    val currentTaskId : Int? = null
)
