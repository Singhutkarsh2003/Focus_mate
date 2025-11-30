package com.example.focusmate.ui.theme.plannerScreen


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.focusmate.ui.theme.components.CustomDatePicker
import com.example.focusmate.ui.theme.components.DeleteDialog
import com.example.focusmate.ui.theme.components.SubListBottomSheet
import com.example.focusmate.ui.theme.components.TaskCheBox
import com.example.focusmate.ui.theme.util.Priority
import com.example.focusmate.ui.theme.util.SnackbarEvent
import com.example.focusmate.ui.theme.util.millisDateToString
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.Instant


data class TaskScreenNav(
    val taskId: Int?,
    val subId: Int?
)

@RequiresApi(Build.VERSION_CODES.O)
@Destination(navArgsDelegate = TaskScreenNav::class)
@Composable
fun TaskScreeenRoute(
    navigator: DestinationsNavigator
){
    val viewModel: TaskViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    TaskScreen(
        state = state,
        onEvent=viewModel::onEvent,
        snackbarEvent = viewModel.snackbarEventFlow,
        onBackClick = {navigator.navigateUp()}
    )
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    state: TaskState,
    onEvent:(TaskEvent) -> Unit,
    snackbarEvent: SharedFlow<SnackbarEvent>,
    onBackClick: () -> Unit
){

    var deletedialog by rememberSaveable { mutableStateOf(false) }

    var datePickerDialog by rememberSaveable { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    )

    val sheetState = rememberModalBottomSheetState()
    var isBottomSheetOpen by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()


    var taskTitleError  by rememberSaveable { mutableStateOf<String?>(null) }
    taskTitleError = when{
       state.title.isBlank() -> "Please Enter a Title"
       state.title.length<2 -> "Task Title is to Short to describe"
       state.title.length> 25 -> " Task length is less than 25 word"
        else  -> null
    }

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

   DeleteDialog(
       isOpen = deletedialog,
       title = "Delete Task",
       bodyTxt = "You want to delete",
       onDismissRequest = {deletedialog = false},
       onConfirmButton = {
           onEvent(TaskEvent.DeleteTask)
           deletedialog = false}
   )

    CustomDatePicker(
        state = datePickerState,
        isOpen = datePickerDialog,
        onDismissBtn = {datePickerDialog = false},
        onConfirmBtn = {
            onEvent(TaskEvent.OnDateChange(millis = datePickerState.selectedDateMillis))
            datePickerDialog = false}
    )

    SubListBottomSheet(
        sheetState = sheetState,
        isOpen = isBottomSheetOpen,
        subject = state.subjects,
        onDismissRequest = {isBottomSheetOpen = false},
        onSujClicked = {subject ->
            scope.launch { sheetState.hide() }.invokeOnCompletion{
                if (!sheetState.isVisible) isBottomSheetOpen = false
            }
            onEvent(TaskEvent.OnRelatedSubjectSelect(subject))
        }
    )

    Scaffold (
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TaskTopBar(
                isTskExit = state.currentTaskId !=null,
                isCompleted = state.isTaskComplete,
                onBackClick = onBackClick,
                checkBoxBorderColor = state.priority.color,
                onDeleteClick = {deletedialog = true},
                onCheckBoxClick = {onEvent(TaskEvent.OnIsCompleteChange)},

            )
        }
    ){ paddingValues ->
        Column (
            modifier = Modifier.fillMaxSize()
                .verticalScroll(state = rememberScrollState())
                .padding(paddingValues)
                .padding(horizontal = 15.dp)
        ){
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.title,
                onValueChange = {onEvent(TaskEvent.OnTitleChange(it))},
                label = {Text("Title")},
                singleLine =  true,
                isError =  taskTitleError != null && state.title.isNotBlank(),
                supportingText = {Text(text = taskTitleError.orEmpty())}
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.description,
                onValueChange = {onEvent(TaskEvent.OnDescriptionChange(it))},
                label = {Text("Description")},
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text("Due Date",
                style = MaterialTheme.typography.bodySmall
            )
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = state.dueDate.millisDateToString(),
                    style = MaterialTheme.typography.bodyLarge
                )
                IconButton(onClick = {datePickerDialog = true}) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Date_Range"
                    )
                }
            }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    "Priority",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row (
                    modifier = Modifier.fillMaxWidth()
                ){
                    Priority.entries.forEach { priority ->
                        PriorityBtn(
                            modifier = Modifier.weight(1f),
                            label = priority.title,
                            backgroundColor = priority.color,
                            borderColor = if (priority == state.priority){
                                Color.White
                            }else Color.Transparent,
                            labelColor = if (priority == state.priority){
                                Color.White
                            }else Color.White.copy(alpha = 0.7f),
                            onClick = {onEvent(TaskEvent.OnPriorityChange(priority))}
                        )

                    }
                }

            Spacer(modifier = Modifier.height(20.dp))
            Text("Related Subject",
                style = MaterialTheme.typography.bodySmall
            )
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val sub = state.subjects.firstOrNull()?.name ?: ""
                Text(
                    text =state.relatedToSub ?:sub,
                    style = MaterialTheme.typography.bodyLarge
                )
                IconButton(onClick = {isBottomSheetOpen = true}) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Arrow_Drop_Down"
                    )
                }
            }
            Button(
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 20.dp),
                enabled = taskTitleError == null,
                onClick = {onEvent(TaskEvent.SaveTask)}
            ) {
                Text("Save")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskTopBar(
    isTskExit : Boolean,
    isCompleted: Boolean,
    checkBoxBorderColor: Color,
    onBackClick: () -> Unit,
    onDeleteClick:() -> Unit,
    onCheckBoxClick:() -> Unit
){
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = {onBackClick()}) {
                Icon(imageVector = Icons.Default.ArrowBack ,
                    contentDescription = "back")
            }
        },
        title = {Text(text = "Task",
            style = MaterialTheme.typography.headlineSmall
            )
                },
        actions = {
            if(isTskExit){
                TaskCheBox(
                    isComplete = isCompleted,
                    borderColor = checkBoxBorderColor,
                    onCheckBoxClick = onCheckBoxClick
                )
                IconButton(onClick = {onDeleteClick()}) {
                    Icon(imageVector = Icons.Default.Delete,
                        contentDescription = "Delete")
                }

            }
        }
    )
}

@Composable
fun PriorityBtn(
    modifier: Modifier = Modifier,
    label: String,
    backgroundColor: Color,
    borderColor: Color,
    labelColor: Color,
    onClick: () -> Unit
){
    Box(
        modifier = modifier.background(backgroundColor)
            .clickable{onClick()}
            .padding(5.dp)
            .border(1.dp, borderColor, RoundedCornerShape(5.dp))
            .padding(5.dp),
        contentAlignment  = Alignment.Center
    ){
        Text(
            text = label, color = labelColor
        )
    }
}