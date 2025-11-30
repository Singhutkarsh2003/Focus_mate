package com.example.focusmate.ui.theme.dashboard


import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusmate.ui.theme.data.model.Session
import com.example.focusmate.ui.theme.data.model.Subjects
import com.example.focusmate.ui.theme.data.model.Task
import com.example.focusmate.ui.theme.data.repository.SessionRepository
import com.example.focusmate.ui.theme.data.repository.SubRepository
import com.example.focusmate.ui.theme.data.repository.TaskRepository
import com.example.focusmate.ui.theme.util.SnackbarEvent
import com.example.focusmate.ui.theme.util.toHours
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val subjectRepository: SubRepository,
    private  val sessionRepository: SessionRepository,
    private  val taskRepository: TaskRepository
): ViewModel() {

    private  val _state = MutableStateFlow(DashboardState())

    val state = combine(
        _state,
        subjectRepository.getAllSub(),
        subjectRepository.getTotalGoalHrs(),
        subjectRepository.setTotalSubjectCount(),
        sessionRepository.getTotalSessionDuration()
    ){state , subjects  , goalHrs , subCount , totalSessionDuration ->
        state.copy(
            totalSubCount = subCount,
            totalGoalHrs = goalHrs,
            subject = subjects,
            totalStudiesHrs = totalSessionDuration.toHours()
        )

    }.stateIn(
        scope =  viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardState()
    )

    val tasks: StateFlow<List<Task>> = taskRepository.getAllUpcomingTask()
        .stateIn(
            scope =  viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue =emptyList()
        )
    val recentSession: StateFlow<List<Session>> = sessionRepository.getRecentSession()
        .stateIn(
            scope =  viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue =emptyList()
        )
    private val _snackbarEventFlow = MutableSharedFlow<SnackbarEvent>()
    val snackbarEventFlow = _snackbarEventFlow.asSharedFlow()


    fun onEvent(event: DashBoardEvent){
        when(event){

            is DashBoardEvent.OnSubNameChange -> {
                _state.update {
                    it.copy(subName = event.name)
                }
            }

            is DashBoardEvent.OnGoalStudyHrsChange -> {
                _state.update {
                    it.copy(goalStudyHrs = event.hours)
                }
            }

            is DashBoardEvent.OnSubCardColorChange -> {
                _state.update {
                    it.copy(subjectCardColor = event.colors)
                }
            }

            is DashBoardEvent.OnDeleteSessionBtn -> {
                _state.update {
                    it.copy(session = event.session)
                }
            }

            DashBoardEvent.SaveSub -> saveSub()


            DashBoardEvent.DeleteSession ->deleteSession()


            is DashBoardEvent.OnTaskIsCompleteChange -> {
                updateTask(event.task)
            }

        }
    }

    private fun updateTask(task: Task) {
        viewModelScope.launch {
            try {
              taskRepository.upsertTask(
                  task = task.copy(isComplete = !task.isComplete)
              )
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        "Saved in Completed Task"
                    )
                )
            }catch (e: Exception){
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        "Couldn't Update Task. ${e.message}"

                    )
                )
            }
        }
    }

    private fun saveSub() {
        viewModelScope.launch {
            try {
                subjectRepository.upsertSubject(
                    subjects = Subjects(
                        name = state.value.subName,
                        goalHour= state.value.goalStudyHrs.toFloatOrNull()?:1f,
                        colors = state.value.subjectCardColor.map {  it.toArgb()}
                    )
                )
                _state.update {
                    it.copy(
                        subName = "",
                        goalStudyHrs = "",
                        subjectCardColor = Subjects.subCardColor.random()
                    )
                }
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        "Subject Save Successfully"
                    )
                )

            }catch (e: Exception){
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        "Couldn't Save Subject. ${e.message}"

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