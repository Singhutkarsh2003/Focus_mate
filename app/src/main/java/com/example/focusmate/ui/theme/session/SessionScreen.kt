package com.example.focusmate.ui.theme.session

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.focusmate.ui.theme.Red
import com.example.focusmate.ui.theme.components.DeleteDialog
import com.example.focusmate.ui.theme.components.SubListBottomSheet
import com.example.focusmate.ui.theme.components.studySessionList
import com.example.focusmate.ui.theme.util.Constants.ACTION_SERVICE_CANCEL
import com.example.focusmate.ui.theme.util.Constants.ACTION_SERVICE_START
import com.example.focusmate.ui.theme.util.Constants.ACTION_SERVICE_STOP
import com.example.focusmate.ui.theme.util.SnackbarEvent
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.DurationUnit

@RequiresApi(Build.VERSION_CODES.O)
@Destination(
    deepLinks = [
        DeepLink(
            action = Intent.ACTION_VIEW,
            uriPattern = "Focus_mate://dashboard/session"
        )
    ]
)
@Composable
fun SessScreenRoute(
    navigator: DestinationsNavigator,
    timerService: StudySessTimerService
){

    val viewModel : SessionScreenViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    SessionScreen(
        state = state,
        snackbarEvent = viewModel.snackbarEventFlow,
        onEvent = viewModel::onEvent,
        onbackBtn = {navigator.navigateUp()},
        timerService = timerService
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScreen(
    state: SessionState,
    snackbarEvent: SharedFlow<SnackbarEvent>,
    onEvent: (SessionEvent) -> Unit,
    onbackBtn: () -> Unit,
    timerService: StudySessTimerService
){

    val hours by timerService.hours
    val mintues by timerService.minutes
    val seconds by timerService.seconds
    val currentTimerState by timerService.currentTimerState

    val context = LocalContext.current

    var deletedialog by rememberSaveable { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()
    var isBottomSheetOpen by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        snackbarEvent.collectLatest {event ->
            when(event) {
                is SnackbarEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        event.message,
                        duration = event.duration
                    )
                }

            }
        }
    }

    LaunchedEffect(key1 = state.subjects) {
        val subjectId  = timerService.subjectId.value
        onEvent(
            SessionEvent.UpdateSubIdAndRelateSub(
                subjectId = subjectId,
                relatedToSub = state.subjects.find { it.subId == subjectId }?.name
            )
        )
    }


    DeleteDialog(
        isOpen = deletedialog,
        title = "Delete History Session",
        bodyTxt = "You want to delete",
        onDismissRequest = {deletedialog = false},
        onConfirmButton = {
            onEvent(SessionEvent.DeleteSession)
            deletedialog = false}
    )

    SubListBottomSheet(
        sheetState = sheetState,
        isOpen = isBottomSheetOpen,
        subject = state.subjects,
        onDismissRequest = {isBottomSheetOpen = false},
        onSujClicked = {subject  ->
            scope.launch { sheetState.hide() }.invokeOnCompletion{
                if (!sheetState.isVisible) isBottomSheetOpen = false
            }
            onEvent(SessionEvent.OnRelatedSubChange(subject))
        }
    )

    Scaffold (
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopBar(
                onbackBtn = onbackBtn
            )
        }
    ){paddingValues ->
        LazyColumn (
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
        ){
            item {
                TimerSec(
                    modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                    hours = hours,
                    minutes =  mintues,
                    seconds = seconds
                )
            }
            item {
                RelatedToSubSec(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 15.dp),
                    relatedToSub = state.relatedToSubj ?: "",
                    selectSubBtn = {isBottomSheetOpen = true},
                    seconds = seconds
                )
            }
            item {
                BtnSec(
                    modifier = Modifier.fillMaxWidth()
                        .padding(10.dp),
                    startBtn = {
                        if (state.subId != null && state.relatedToSubj != null){
                            ServiceHelper.triggerService(
                                context = context,
                                action = if(currentTimerState == TimerState.STARTED){
                                    ACTION_SERVICE_STOP
                                }else ACTION_SERVICE_START
                            )
                            timerService.subjectId.value = state.subId
                        }else{
                            onEvent(SessionEvent.CheckSubId)
                        }
                    },
                    cancelBtn = {
                        ServiceHelper.triggerService(
                            context = context,
                            action = ACTION_SERVICE_CANCEL
                        )
                    },
                    finishBtn = {
                        val duration = timerService.duration.toLong(DurationUnit.SECONDS)
                        if (duration >= 36){
                            ServiceHelper.triggerService(
                                context = context,
                                action = ACTION_SERVICE_CANCEL
                            )
                        }
                        onEvent(SessionEvent.SaveSession(duration))
                    },
                    timerState = currentTimerState,
                    seconds = seconds
                )
            }
            studySessionList(
                secTitle = "Study Session History",
                emptyLisTxt = "You don't have any Recent tasks.\n"+
                        "Start a Session to begin recording your session ",
                session = state.sessions,
                onDeleteIcon = {session ->
                    onEvent(SessionEvent.onDeleteSessionButtonClick(session))
                    deletedialog = true}
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    onbackBtn: () -> Unit,
){
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onbackBtn) {
                Icon(imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Arrow_Back"
                    )
            }
        },
        title = {
            Text("Study Session",
                style = MaterialTheme.typography.headlineSmall
                )
        }
    )
}

@Composable
private fun TimerSec(
    modifier: Modifier,
    hours: String,
    minutes: String,
    seconds: String
){
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ){
        Box(
            modifier = Modifier.size(255.dp)
                .border(5.dp,
                     MaterialTheme.colorScheme.surfaceVariant,CircleShape)
        )
        Row {
            AnimatedContent(
                targetState = hours,
                label = hours,
                transitionSpec = {timerAnimation()}
            ) { hours ->
                Text(
                    "$hours:",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 48.sp)
                )
            }
            AnimatedContent(
                targetState = minutes,
                label = minutes,
                transitionSpec = {timerAnimation()}
            ) { minutes ->
                Text(
                    "$minutes:",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 48.sp)
                )
            }
            AnimatedContent(
                targetState = seconds,
                label = seconds,
                transitionSpec = {timerAnimation()}
            ) { seconds ->
                Text(
                    seconds,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 48.sp)
                )
            }
        }
    }
}

@Composable
private  fun RelatedToSubSec(
    modifier: Modifier,
    relatedToSub: String,
    selectSubBtn:() -> Unit,
    seconds: String
){
    Column (modifier = modifier){
        Text("Related Subject",
            style = MaterialTheme.typography.bodySmall
        )
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Toc",
                style = MaterialTheme.typography.bodyLarge
            )
            IconButton(
                onClick = selectSubBtn,
                enabled = seconds == "00"
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Arrow_Drop_Down"
                )
            }
        }
    }
}

@Composable
private fun BtnSec(
    modifier: Modifier,
    startBtn: () -> Unit,
    cancelBtn: () -> Unit,
    finishBtn: () -> Unit,
    timerState: TimerState,
    seconds: String
){
    Row (
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Button(onClick = cancelBtn,
            enabled = seconds!= "00" && timerState != TimerState.STARTED
            ) {
            Text(
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                text = "Cancel"
            )
        }
        Button(
            onClick = startBtn,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (timerState == TimerState.STARTED) Red
                else MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )

        ) {
            Text(
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                text = when(timerState){
                    TimerState.STARTED -> "STOP"
                    TimerState.STOPED -> "Resume"
                    else -> "Start"
                }
            )
        }
        Button(
            onClick = finishBtn,
            enabled = seconds!= "00" && timerState != TimerState.STARTED
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                text = "Finish"
            )
        }
    }
}

private  fun timerAnimation(duration:Int =600): ContentTransform{
    return slideInVertically(animationSpec = tween(duration)){fullHeight -> fullHeight} +
            fadeIn(animationSpec = tween(duration)) togetherWith
            slideOutVertically (animationSpec = tween(duration)){fullHeight -> -fullHeight} +
            fadeOut(animationSpec = tween(duration))
}