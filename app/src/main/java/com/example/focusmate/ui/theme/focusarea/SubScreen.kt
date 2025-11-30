package com.example.focusmate.ui.theme.focusarea

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.focusmate.ui.theme.components.AddDialogSub
import com.example.focusmate.ui.theme.components.CountCard
import com.example.focusmate.ui.theme.components.DeleteDialog
import com.example.focusmate.ui.theme.components.studySessionList
import com.example.focusmate.ui.theme.components.taskList
import com.example.focusmate.ui.theme.destinations.TaskScreeenRouteDestination
import com.example.focusmate.ui.theme.plannerScreen.TaskScreenNav
import com.example.focusmate.ui.theme.util.SnackbarEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest


data class SubScreenNav (
        val subId : Int
        )



@RequiresApi(Build.VERSION_CODES.O)
@Destination(navArgsDelegate = SubScreenNav::class)
@Composable
fun SubScreenRoute(
    navigator: DestinationsNavigator
){

    val  viewModel: SubScreenViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    SubScreen(
        state = state,
        onEvent= viewModel::onEvent,
        snackbarEvent = viewModel.snackbarEventFlow,
        onBackPress = {navigator.navigateUp()},
        onAddTaskBtn = {
            val  navArg = TaskScreenNav(taskId = null , subId =state.currentSubId)
            navigator.navigate(TaskScreeenRouteDestination(navArgs = navArg))
        },
        onTaskCardClick = {taskId ->
            val navArg = TaskScreenNav(taskId =taskId, subId = null)
            navigator.navigate(TaskScreeenRouteDestination(navArgs = navArg))
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubScreen(
    state: SubState,
    onEvent :(SubEvent) -> Unit,
    snackbarEvent: SharedFlow<SnackbarEvent>,
    onBackPress: () -> Unit,
    onAddTaskBtn: () -> Unit,
    onTaskCardClick : (Int?) -> Unit
){

    val listState = rememberLazyListState()
    val fabExpanded by remember {
        derivedStateOf { listState.firstVisibleItemIndex ==0 } }
    val scrollBehavoir = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    var editSubDialog by rememberSaveable { mutableStateOf(false) }
    var deleteDialog by rememberSaveable { mutableStateOf(false) }
    var deleteSubDialog by rememberSaveable { mutableStateOf(false) }

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
    LaunchedEffect(key1 = state.studiedHrs , key2 = state.goalStudyHrs) {
        onEvent(SubEvent.UpdateProgress)
    }

    AddDialogSub(
        isOpen = editSubDialog,
        subName = state.subName,
        goalHrs = state.goalStudyHrs,
        onSubNameChange = {onEvent(SubEvent.OnSubNameChange(it))},
        onGoalHrsChange = {onEvent(SubEvent.OnGoalStudyHrsChange(it))},
        selectColor =state.subCardColor,
        onColorChange = {onEvent(SubEvent.OnSubCardColorChange(it))},
        onDismissRequest = {editSubDialog = false},
        onConfirmButton = {
            onEvent(SubEvent.UpdateSub)
            editSubDialog = false}
    )
    DeleteDialog(
        isOpen = deleteSubDialog,
        title = "Delete Subject",
        bodyTxt = "Are You want to delete this Subject",
        onDismissRequest = {deleteDialog = false},
        onConfirmButton = {
            onEvent(SubEvent.DeleteSub)
            deleteDialog = false
            if (state.isLoading.not()){
                onBackPress()
            }
        }
    )
    DeleteDialog(
        isOpen = deleteDialog,
        title = "Delete Session",
        bodyTxt = "Are You want to delete this Session",
        onDismissRequest = {deleteDialog = false},
        onConfirmButton = {
            onEvent(SubEvent.DeleteSession)
            deleteDialog= false}
    )


    Scaffold (
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier.nestedScroll(scrollBehavoir.nestedScrollConnection),
        topBar = {
          ScreenTopBar(
              title = state.subName,
              onBackPress = onBackPress,
              onDeleteBtn = {deleteSubDialog = true},
              onEditBtn = {editSubDialog = true},
             scrollBehavoir
          )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddTaskBtn,
                icon = {
                    Icon(imageVector = Icons.Default.Add,
                    contentDescription = "Add")
                },
                text = {
                    Text("Add Tasks")
                },
                expanded = fabExpanded
            )
        }
    ){ PaddingValues ->
        LazyColumn (
            state = listState,
            modifier = Modifier.fillMaxSize()
                .padding(PaddingValues)
        ){
            item {
                OverViewSec(
                    modifier = Modifier.fillMaxWidth()
                        .padding(15.dp),
                    studyHrs =  state.studiedHrs.toString(),
                    goalHrs =state.goalStudyHrs,
                    progress = state.progress
                )
            }
            taskList(
                secTitle = "UpComing Tasks",
                emptyLisTxt = "You don't have any upcoming tasks.\n"+
                        "Click the + Button in subject to Add new Task",
                task =state.upComingTask,
                onCheckBoxClick = {onEvent(SubEvent.OnTaskIsCompleteChange(it))},
                onTaskCardClick = onTaskCardClick
            )
            item {
                Spacer(modifier = Modifier.height(25.dp))
            }
            taskList(
                secTitle = "Completed Tasks",
                emptyLisTxt = "You don't have any Completed tasks.\n"+
                        "Click the + Check Button on completed task",
                task =state.completedTask,
                onCheckBoxClick = {onEvent(SubEvent.OnTaskIsCompleteChange(it))},
                onTaskCardClick = onTaskCardClick
            )
            item {
                Spacer(modifier = Modifier.height(25.dp))
            }
            studySessionList(
                secTitle = "Recent Study Session",
                emptyLisTxt = "You don't have any Recent tasks.\n"+
                        "Start a Session to begin recording your session ",
                session = state.recentSession,
                onDeleteIcon = {
                    deleteDialog = true
                    onEvent(SubEvent.OnDeleteSessionBtn(it))
                    }
            )

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private  fun ScreenTopBar(
    title: String,
    onBackPress:() -> Unit,
    onDeleteBtn: () -> Unit,
    onEditBtn: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
){
    LargeTopAppBar(
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(onClick = onBackPress) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back_Screen"
                )
            }
        },
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        actions = {
            IconButton(onClick = onDeleteBtn) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete Subject"
                )
            }
            IconButton(onClick = onEditBtn) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit Subject"
                )
            }
        }
    )
}


@Composable
private fun OverViewSec(
    modifier: Modifier,
    studyHrs: String,
    goalHrs: String,
    progress: Float
){
    val percentProgress = remember(progress){
        (progress * 100).toInt().coerceIn(0,100)
    }
    Row (
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ){
        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Goal Hrs",
            count = goalHrs
        )
        Spacer(modifier = Modifier.width(12.dp))
        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Study Hrs",
            count = studyHrs
        )
        Spacer(modifier = Modifier.width(12.dp))
        Box (
            modifier = Modifier.size(80.dp),
            contentAlignment = Alignment.Center
        ){
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(),
                progress = 1f,
                strokeWidth = 5.dp,
                strokeCap = StrokeCap.Round,
                color = MaterialTheme.colorScheme.surfaceVariant
            )
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(),
                progress = progress,
                strokeWidth = 5.dp,
                strokeCap = StrokeCap.Round
            )
            Text(text = "$percentProgress%")
        }
    }
}