package com.example.focusmate.ui.theme.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Session(
    val sessionSubId: Int,
    val relatedSub: String,
    val date: Long,
    val duration: Long,
    @PrimaryKey(autoGenerate = true)
    val sessionId: Int,
)
