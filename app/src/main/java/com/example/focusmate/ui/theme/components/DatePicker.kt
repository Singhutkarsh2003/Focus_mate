package com.example.focusmate.ui.theme.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePicker(
    state: DatePickerState,
    isOpen: Boolean,
    confirmBtnTxt: String ="Ok",
    dismissBtnTxt : String ="Cancel",
    onDismissBtn: () -> Unit,
    onConfirmBtn: () -> Unit
){

    val dateValidator = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            return utcTimeMillis >= today
        }

        override fun isSelectableYear(year: Int): Boolean {
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            return year in currentYear..(currentYear + 1)
        }
    }

    val state = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis(),
        selectableDates = dateValidator
    )

    if (isOpen){
        DatePickerDialog(
            onDismissRequest = onDismissBtn,
            confirmButton = {
                TextButton(onClick = onConfirmBtn) {
                    Text(confirmBtnTxt)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissBtn) {
                    Text(dismissBtnTxt)
                }
            },
            content = {
                DatePicker(
                    state = state,

                )
            }
        )
    }
}