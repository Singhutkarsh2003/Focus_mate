package com.example.focusmate.ui.theme.focusarea

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusmate.ui.theme.data.model.Subjects
import com.example.focusmate.ui.theme.data.model.Task
import com.example.focusmate.ui.theme.data.repository.SessionRepository
import com.example.focusmate.ui.theme.data.repository.SubRepository
import com.example.focusmate.ui.theme.data.repository.TaskRepository
import com.example.focusmate.ui.theme.navArgs
import com.example.focusmate.ui.theme.util.SnackbarEvent
import com.example.focusmate.ui.theme.util.toHours
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SubScreenViewModel @Inject constructor(
    private val subjectRepository: SubRepository,
    private val taskRepository: TaskRepository,
    private val sessionRepository: SessionRepository,
    savedStateHandle: SavedStateHandle

) : ViewModel() {



    private val navArgs: SubScreenNav = savedStateHandle.navArgs()
    private val _state = MutableStateFlow(SubState())
    val state = combine(
        _state,
        taskRepository.getUpcomingTasksForSub(navArgs.subId),
        taskRepository.getCompletedTaskForSub(navArgs.subId),
        sessionRepository.getRecentSessionSub(navArgs.subId),
        sessionRepository.getTotalSessionDuration()
    ) { state, upcomingTasks, completedTask, recentSession, totalSessionDuration ->
        state.copy(
            upComingTask = upcomingTasks,
            completedTask = completedTask,
            recentSession = recentSession,
            studiedHrs = totalSessionDuration.toHours()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = SubState()
    )

    private val _snackbarEventFlow = MutableSharedFlow<SnackbarEvent>()
    val snackbarEventFlow = _snackbarEventFlow.asSharedFlow()

    init {
        fetchSubject()
    }

    fun onEvent(event: SubEvent) {
        when (event) {

            is SubEvent.OnSubCardColorChange -> {
                    _state.update {
                        it.copy(subCardColor = event.color)
                    }
            }

            is SubEvent.OnSubNameChange -> {
                _state.update {
                    it.copy(subName = event.name)
                }
            }

            is SubEvent.OnGoalStudyHrsChange ->  {
                _state.update {
                    it.copy(goalStudyHrs = event.hours)
                }
            }

            SubEvent.UpdateSub -> updateSubject()

            SubEvent.DeleteSub -> deleteSub()

            SubEvent.DeleteSession ->deleteSession()

            is SubEvent.OnDeleteSessionBtn -> {
                _state.update {
                    it.copy(session = event.session)
                }
            }

            is SubEvent.OnTaskIsCompleteChange -> {
                updateTask(event.task)
            }

            SubEvent.UpdateProgress -> {
                val goalStudyHrs = state.value.goalStudyHrs.toFloatOrNull()?:1f
                _state.update {
                    it.copy(
                        progress = (state.value.studiedHrs / goalStudyHrs).coerceIn(0f, 1f)
                    )
                }
            }
        }
    }

    private fun updateSubject(){
        viewModelScope.launch {
            try {
                subjectRepository.upsertSubject(
                    subjects = Subjects(
                        subId = state.value.currentSubId,
                        name = state.value.subName,
                        goalHour = state.value.goalStudyHrs.toFloatOrNull()?:1f,
                        colors = state.value.subCardColor.map {  it.toArgb()}
                    )
                )
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        "Subject Update Successfully"
                    )
                )
            }catch (e: Exception){
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        "Couldn't update Subject. ${e.message}"
                    )
                )
            }

        }
    }

    private  fun fetchSubject(){
        viewModelScope.launch {
            subjectRepository
                .getSubById(navArgs.subId)?.let {  subjects ->
                    _state.update {
                        it.copy(
                            subName = subjects.name,
                            goalStudyHrs =  subjects.goalHour.toString(),
                            subCardColor = subjects.colors.map { Color(it) },
                            currentSubId = subjects.subId
                        )
                    }
                }
        }

    }

    private fun deleteSub(){
        viewModelScope.launch {
            _state.update {  it.copy(isLoading = true)}
            try {
                state.value.currentSubId?.let {
                    subjectRepository.deleteSubject(subjectInt = it)
                }
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        "Subject Delete Successfully"
                    )
                )
            }catch (e: Exception){
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        "Couldn't Delete Subject. ${e.message}"
                    )
                )
            }
            _state.update {  it.copy(isLoading = false)}

        }
    }
    private fun updateTask(task: Task) {
        viewModelScope.launch {
            try {
                taskRepository.upsertTask(
                    task = task.copy(isComplete = !task.isComplete)
                )
                if(task.isComplete){
                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowSnackbar(
                            "Saved in Upcoming Task"
                        )
                    )
                }else{
                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowSnackbar(
                            "Saved in Completed Task"
                        )
                    )
                }

            }catch (e: Exception){
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        "Couldn't Update Task. ${e.message}"

                    )
                )
            }
        }
    }
    private  fun deleteSession(){
        viewModelScope.launch {
            try {
                state.value.session?.let {
                    sessionRepository.deleteSession(it)
                }
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        "Successfully Deleted Session."
                    )
                )
            }catch (e: Exception){
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        "Couldn't delete Session. ${e.message}"
                    )
                )
            }
        }
    }
}