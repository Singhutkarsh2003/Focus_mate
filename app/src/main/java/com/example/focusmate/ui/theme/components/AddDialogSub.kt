package com.example.focusmate.ui.theme.components


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.focusmate.ui.theme.data.model.Subjects

@Composable
fun AddDialogSub(
    isOpen : Boolean,
    title: String = "ADD/ Update Subject",
    subName: String,
    goalHrs : String,
    onSubNameChange: (String) -> Unit,
    onGoalHrsChange: (String) -> Unit,
    selectColor: List<Color>,
    onColorChange: (List<Color>) -> Unit,
    onDismissRequest : ()-> Unit,
    onConfirmButton : () -> Unit
){
    var goalHrsError by rememberSaveable { mutableStateOf<String?>(null) }
    var subError by rememberSaveable { mutableStateOf<String?>(null) }

    subError = when{
        subName.isBlank() -> "Enter Your Subject"
        subName.length< 2 -> "Too Short Length"
        subName.length> 25 -> "Max Length is Less than 25"
        else -> null
    }
    goalHrsError = when{
        goalHrs.isBlank() -> "Enter Your Goal"
        goalHrs.toFloatOrNull() == null -> "Invaild Number"
        goalHrs.toFloat() <1f -> " Set At least 1 hrs "
       goalHrs.toFloat() > 1000f -> "Set less than 1000hrs"
        else -> null
    }


    if (isOpen){
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(text = title) },
            text = {
                Column {
                    Row (
                        modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ){
                        Subjects.subCardColor.forEach { colors ->
                            Box(
                                modifier = Modifier.size(25.dp)
                                    .clip(CircleShape)
                                    .border(width = 2.dp,
                                        color = if (colors == selectColor)
                                            Color.Gray
                                        else Color.Transparent,
                                        shape = CircleShape
                                        )
                                    .background(brush = Brush.verticalGradient(colors))
                                    .clickable{onColorChange(colors)}
                            )
                        }
                    }
                    OutlinedTextField(
                        value = subName,
                        onValueChange = onSubNameChange,
                        label = {Text("Subject Name")},
                        singleLine = true,
                        isError = subError != null && subName.isNotBlank(),
                        supportingText = {Text(text = subError.orEmpty())}
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = goalHrs,
                        onValueChange = onGoalHrsChange,
                        label = {Text("Study Hours")},
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError =  goalHrsError != null && goalHrs.isNotBlank(),
                        supportingText = {Text(text =goalHrsError.orEmpty() )}
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text("Cancel")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirmButton,
                    enabled = subError == null && goalHrsError == null
                    ) {
                    Text("Save")
                }
            }
        )
    }
}