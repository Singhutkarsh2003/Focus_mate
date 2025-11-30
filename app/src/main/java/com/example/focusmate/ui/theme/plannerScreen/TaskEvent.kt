package com.example.focusmate.ui.theme.plannerScreen

import com.example.focusmate.ui.theme.data.model.Subjects
import com.example.focusmate.ui.theme.util.Priority

sealed class TaskEvent {

     data class OnTitleChange(val title: String): TaskEvent()

     data class OnDescriptionChange(val description : String): TaskEvent()

     data class  OnDateChange(val millis: Long?): TaskEvent()

     data class  OnPriorityChange(val priority: Priority) : TaskEvent()

    data class OnRelatedSubjectSelect(val subject: Subjects): TaskEvent()

    data object OnIsCompleteChange : TaskEvent()

    data object SaveTask: TaskEvent()

    data object DeleteTask: TaskEvent()
}