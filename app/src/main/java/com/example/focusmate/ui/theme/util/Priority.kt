package com.example.focusmate.ui.theme.util

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.graphics.Color
import com.example.focusmate.ui.theme.Blue
import com.example.focusmate.ui.theme.Green
import com.example.focusmate.ui.theme.Red
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

enum class Priority(
    val title: String,
    val color: Color,
    val value: Int
) {
    LOW(title = "Low", color = Green, value =0),
    MEDIUM(title = "Medium", color = Blue, value =1),
    HIGH(title = "High", color = Red, value =2);

    companion object{
        fun fromInt(value: Int) = values().firstOrNull {  it.value == value} ?:MEDIUM
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun Long?.millisDateToString(): String{
    val date: LocalDate = this?.let {
        Instant
            .ofEpochMilli(it)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }?: LocalDate.now()
    return date.format(DateTimeFormatter.ofPattern("dd/MM/YYYY"))
}

fun Long.toHours(): Float{
    val hours = this.toFloat() / 360f
    return "%.2f".format(hours).toFloat()
}

sealed class  SnackbarEvent{
    data class  ShowSnackbar(
        val message : String,
        val duration: SnackbarDuration = SnackbarDuration.Short
    ) : SnackbarEvent()

//     data object  NavigatedUp: SnackbarEvent()
}
fun Int.pad(): String{
    return this.toString().padStart(length = 2, padChar = '0')
}