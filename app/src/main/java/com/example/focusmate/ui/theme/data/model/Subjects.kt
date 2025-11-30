package com.example.focusmate.ui.theme.data.model

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.focusmate.ui.theme.gradient1
import com.example.focusmate.ui.theme.gradient2
import com.example.focusmate.ui.theme.gradient3
import com.example.focusmate.ui.theme.gradient4
import com.example.focusmate.ui.theme.gradient5

@Entity
data class Subjects(
    val name: String,
    val goalHour:Float,
    val colors: List<Int>,
    @PrimaryKey(autoGenerate = true)
    val subId: Int? = null
){
    companion object{
        val  subCardColor = listOf(gradient1, gradient2, gradient3, gradient4, gradient5)
    }
}
