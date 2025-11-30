package com.example.focusmate.ui.theme.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun DeleteDialog(
    isOpen : Boolean,
    title: String,
    bodyTxt : String,
    onDismissRequest : ()-> Unit,
    onConfirmButton : () -> Unit
){

    if (isOpen){
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(text = title) },
            text = {
                Text(text = bodyTxt)
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text("Cancel")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirmButton,
                ) {
                    Text("Delete")
                }
            }
        )
    }
}