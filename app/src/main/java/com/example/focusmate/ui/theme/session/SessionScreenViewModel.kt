package com.example.focusmate.ui.theme.session

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusmate.ui.theme.data.model.Session
import com.example.focusmate.ui.theme.data.repository.SessionRepository
import com.example.focusmate.ui.theme.data.repository.SubRepository
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
class SessionScreenViewModel @Inject constructor(
    subRepository: SubRepository,
    private  val sessionRepository: SessionRepository
) : ViewModel(){

    private  val _state = MutableStateFlow(SessionState())

    val state = combine(
        _state,
        subRepository.getAllSub(),
        sessionRepository.getAllSessions()
    ){state , subjects , session ->
        state.copy(
            subjects = subjects,
            sessions = session
        )
    }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = SessionState()
        )

    private val _snackbarEventFlow = MutableSharedFlow<SnackbarEvent>()
    val snackbarEventFlow = _snackbarEventFlow.asSharedFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    fun onEvent(event: SessionEvent){

        when(event){
            SessionEvent.CheckSubId -> notifyToUpdateSubj()
            SessionEvent.DeleteSession -> deleteSession()
            is SessionEvent.onDeleteSessionButtonClick -> {
                _state.update {
                    it.copy(session = event.session)
                }
            }
            is SessionEvent.OnRelatedSubChange -> {
                _state.update {
                    it.copy(
                        relatedToSubj =  event.subjects.name,
                        subId = event.subjects.subId
                    )
                }
            }
            is SessionEvent.SaveSession -> insertSession(event.duration)
            is SessionEvent.UpdateSubIdAndRelateSub -> {
                _state.update {
                    it.copy(
                        relatedToSubj =  event.relatedToSub,
                        subId = event.subjectId
                    )
                }
            }
        }
    }

    private fun notifyToUpdateSubj() {
        viewModelScope.launch {
            if (state.value.subId == null || state.value.relatedToSubj == null){
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        "Successfully Deleted Session."
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


    @RequiresApi(Build.VERSION_CODES.O)
    private fun insertSession(duration: Long) {
        viewModelScope.launch {
            if (duration <36){
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        "Session Can't less than 36 second"
                    )
                )
                return@launch
            }
            try {
                sessionRepository.insertSession(
                    session = Session(
                        sessionSubId = state.value.subId ?: -1,
                        relatedSub = state.value.relatedToSubj ?: "",
                        date = Instant.now().toEpochMilli(),
                        duration = duration,
                        sessionId = 0,
                    )
                )
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        "Session Saved  Successfully"
                    )
                )
            }catch (e: Exception){
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        "Couldn't Save Session. ${e.message}"
                    )
                )
            }

        }
    }
}