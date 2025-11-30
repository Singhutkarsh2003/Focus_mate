package com.example.focusmate.ui.theme.plannerScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusmate.ui.theme.data.model.Task
import com.example.focusmate.ui.theme.data.repository.SubRepository
import com.example.focusmate.ui.theme.data.repository.TaskRepository
import com.example.focusmate.ui.theme.navArgs
import com.example.focusmate.ui.theme.util.Priority
import com.example.focusmate.ui.theme.util.SnackbarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(

    private  val taskRepository: TaskRepository,
    private  val subRepository: SubRepository,
    savedStateHandle: SavedStateHandle,

) : ViewModel(){

    private  val navArgs : TaskScreenNav = savedStateHandle.navArgs()

    private  val _state = MutableStateFlow(TaskState())
    val state = combine(
        _state,
        subRepository.getAllSub()
    ){state , subjects ->
        state.copy(subjects = subjects)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = TaskState()
    )

    private val _snackbarEventFlow = MutableSharedFlow<SnackbarEvent>()
    val snackbarEventFlow = _snackbarEventFlow.asSharedFlow()

    init {
        fetchTask()
        fetchSubject()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onEvent(event: TaskEvent){
        when(event){

            is TaskEvent.OnTitleChange ->{
                _state.update {
                    it.copy(title = event.title)
                }
            }

            is TaskEvent.OnDescriptionChange ->{
                _state.update {
                    it.copy(description = event.description)
                }
            }

            is TaskEvent.OnDateChange -> {
                _state.update {
                    it.copy(dueDate = event.millis)
                }
            }

            is TaskEvent.OnPriorityChange -> {
                _state.update {
                    it.copy(priority = event.priority )
                }
            }

            TaskEvent.OnIsCompleteChange -> {
                _state.update {
                    it.copy(isTaskComplete = _state.value.isTaskComplete)
                }
            }

            is TaskEvent.OnRelatedSubjectSelect -> {
                _state.update {
                    it.copy(
                        relatedToSub = event.subject.name,
                        subjectId = event.subject.subId
                    )
                }
            }

            TaskEvent.SaveTask -> saveTask()

            TaskEvent.DeleteTask -> deleteTask()


        }
    }

    private fun deleteTask() {
        viewModelScope.launch {
            try {
                val currentTaskId = state.value.currentTaskId
                if (currentTaskId != null){
                    withContext(Dispatchers.IO){
                        taskRepository.deleteTask(taskId = currentTaskId)
                    }
                }
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        "Task Delete Successfully"
                    )
                )
            }catch (e: Exception){
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        "Couldn't Delete Task. ${e.message}"
                    )
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveTask() {
        viewModelScope.launch {
            val state = _state.value
            if(state.subjectId == null || state.relatedToSub == null){
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        "Select Subject related to the Task"
                    )
                )
                return@launch
            }
            try {
                taskRepository.upsertTask(
                    task = Task(
                        title = state.title,
                        description = state.description,
                        dueDate = state.dueDate ?: Instant.now().toEpochMilli(),
                        relatedToSub = state.relatedToSub,
                        priority = state.priority.value,
                        isComplete = state.isTaskComplete,
                        taskSubId = state.subjectId,
                        taskId = state.currentTaskId
                    )
                )

                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        "Task Save Successfully"
                    )
                )

            }catch (e: Exception){
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        "Couldn't Save Task. ${e.message}")
                )
            }
        }
    }

    private  fun fetchTask(){
        viewModelScope.launch {
            navArgs.taskId?.let{id ->
                taskRepository.getTaskById(id)?.let { task ->
                    _state.update {
                        it.copy(
                            title = task.title,
                            description = task.description,
                            dueDate = task.dueDate,
                            isTaskComplete = task.isComplete,
                            relatedToSub =  task.relatedToSub,
                            priority = Priority.fromInt(task.priority),
                            subjectId = task.taskSubId,
                            currentTaskId = task.taskId
                        )
                    }

                }
            }
        }
    }

    private  fun fetchSubject(){
        viewModelScope.launch {
            navArgs.subId?.let {id ->
                subRepository.getSubById(id)?.let { subjects ->
                    _state.update {
                        it.copy(
                            subjectId = subjects.subId,
                            relatedToSub = subjects.name
                        )
                    }
                }
            }
        }
    }
}