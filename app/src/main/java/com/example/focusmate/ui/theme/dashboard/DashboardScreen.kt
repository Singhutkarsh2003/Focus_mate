package com.example.focusmate.ui.theme.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.focusmate.R
import com.example.focusmate.ui.theme.components.AddDialogSub
import com.example.focusmate.ui.theme.components.CountCard
import com.example.focusmate.ui.theme.components.DeleteDialog
import com.example.focusmate.ui.theme.components.SubCard
import com.example.focusmate.ui.theme.components.studySessionList
import com.example.focusmate.ui.theme.components.taskList
import com.example.focusmate.ui.theme.data.model.Session
import com.example.focusmate.ui.theme.data.model.Subjects
import com.example.focusmate.ui.theme.data.model.Task
import com.example.focusmate.ui.theme.destinations.SessScreenRouteDestination
import com.example.focusmate.ui.theme.destinations.SubScreenRouteDestination
import com.example.focusmate.ui.theme.destinations.TaskScreeenRouteDestination
import com.example.focusmate.ui.theme.focusarea.SubScreenNav
import com.example.focusmate.ui.theme.plannerScreen.TaskScreenNav
import com.example.focusmate.ui.theme.util.SnackbarEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

@RootNavGraph(start = true)
@Destination
@Composable
fun  DashboardRoute(
    navigator: DestinationsNavigator
){

    val viewModel: DashboardViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val recentSession by viewModel.recentSession.collectAsStateWithLifecycle()

    DashboardScreen(
        state= state,
        tasks = tasks,
        recentSession= recentSession,
        onEvent = viewModel::onEvent,
        snackbarEvent = viewModel.snackbarEventFlow,
        onSubCardClick = {subjectId ->
            subjectId?.let {
                val navArg = SubScreenNav(subId = subjectId)
                navigator.navigate(SubScreenRouteDestination(navArgs = navArg))
            }
        },
        onTaskCardClick = {taskId ->
            val navArg = TaskScreenNav(taskId = taskId, subId = null)
            navigator.navigate(TaskScreeenRouteDestination(navArgs = navArg))
        },
        onStartSessionBtnClick = {
            navigator.navigate(SessScreenRouteDestination())
        }
    )
}

@Composable
fun DashboardScreen(
    state: DashboardState,
    tasks : List<Task>,
    recentSession: List<Session>,
    onEvent: (DashBoardEvent) -> Unit,
    snackbarEvent: SharedFlow<SnackbarEvent>,
    onSubCardClick :(Int?) -> Unit,
    onTaskCardClick : (Int?) -> Unit,
    onStartSessionBtnClick : () -> Unit

){

    var addSubDialog by rememberSaveable { mutableStateOf(false) }
    var deleteDialog by rememberSaveable { mutableStateOf(false) }

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

    AddDialogSub(
        isOpen = addSubDialog,
        subName = state.subName,
        goalHrs = state.goalStudyHrs,
        onSubNameChange = { onEvent(DashBoardEvent.OnSubNameChange(it))},
        onGoalHrsChange = {onEvent(DashBoardEvent.OnGoalStudyHrsChange(it))},
        selectColor = state.subjectCardColor,
        onColorChange = {onEvent(DashBoardEvent.OnSubCardColorChange(it))},
        onDismissRequest = {addSubDialog = false},
        onConfirmButton = {
            onEvent(DashBoardEvent.SaveSub)
            addSubDialog = false}
    )

    DeleteDialog(
        isOpen = deleteDialog,
        title = "Delete Session",
        bodyTxt = "Are You want to delete this Session",
        onDismissRequest = {deleteDialog = false},
        onConfirmButton = {
            onEvent(DashBoardEvent.DeleteSession)
            deleteDialog = false}
    )


    Scaffold (
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {DashboardScreenTopBar()}
    ){ paddingValues ->
        LazyColumn (
            modifier = Modifier.fillMaxSize()
                .padding(paddingValues)
        ){
            item {
                CountCardSection(
                    modifier = Modifier.fillMaxWidth().padding(15.dp),
                    subjectCount = state.totalSubCount,
                    studyHours = state.totalStudiesHrs.toString(),
                    goalHours = state.totalGoalHrs.toString()
                )
            }
            item {
                SubCardSec(
                    modifier = Modifier.fillMaxWidth(),
                    subjectList =state.subject,
                    onAddIcon =  { addSubDialog = true },
                    onSubCardClick = onSubCardClick
                )
            }
            item {
                Button(
                    onClick = onStartSessionBtnClick,
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 50.dp, vertical = 25.dp)
                ) {
                    Text("Start Session")
                }
            }
            taskList(
                secTitle = "UpComing Tasks",
                emptyLisTxt = "You don't have any upcoming tasks.\n"+
                        "Click the + Button in subject to Add new Task",
                task =tasks,
                onCheckBoxClick = {onEvent(DashBoardEvent.OnTaskIsCompleteChange(it))},
                onTaskCardClick = onTaskCardClick
            )
            item {
                Spacer(modifier = Modifier.height(25.dp))
            }
            studySessionList(
                secTitle = "Recent Study Session",
                emptyLisTxt = "You don't have any Recent tasks.\n"+
                        "Start a Session to begin recording your session ",
                session = recentSession,
                onDeleteIcon = {
                    onEvent(DashBoardEvent.OnDeleteSessionBtn(it))
                    deleteDialog = true}
                )

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreenTopBar(){
    CenterAlignedTopAppBar(
        title = {
            Text("FocusMate",
                style = MaterialTheme.typography.headlineMedium
                )
        }
    )
}

@Composable
private  fun CountCardSection(
    modifier: Modifier,
    subjectCount : Int,
    studyHours: String,
    goalHours: String
){
    Row (modifier= modifier){
        CountCard(
            headingText = "Subject ",
            count = "$subjectCount",
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(12.dp))

        CountCard(
            headingText = "Studies Hours ",
            count = studyHours,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(12.dp))
        CountCard(
            headingText = "Goal Study Hours",
            count = goalHours,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SubCardSec(
    modifier: Modifier,
    subjectList: List<Subjects>,
    emptyLisTxt : String = "You don't have any subject. " +
            "\nClick The + Button to Add New Subject",
    onAddIcon: () -> Unit,
    onSubCardClick: (Int?) -> Unit
){
    Column(modifier = Modifier) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Absolute.SpaceBetween
        ){
            Text("SUBJECTS",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 15.dp)
            )
            IconButton(onClick = onAddIcon) {
                Icon(imageVector = Icons.Default.Add, contentDescription ="Add")
            }
        }
        if (subjectList.isEmpty()){
            Image(
                modifier = Modifier.size(150.dp)
                    .align(Alignment.CenterHorizontally),
                painter = painterResource(R.drawable.img1_book),
                contentDescription = emptyLisTxt
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text =emptyLisTxt,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
       LazyRow(
           horizontalArrangement = Arrangement.spacedBy(15.dp),
           contentPadding = PaddingValues(start = 15.dp , end  = 12.dp)
       ) {
           items(subjectList){subject ->
               SubCard(
                   subName = subject.name,
                   gradientColor = subject.colors.map {  Color(it)},
                   onClick = {onSubCardClick(subject.subId)}
               )
           }
       }
    }
}