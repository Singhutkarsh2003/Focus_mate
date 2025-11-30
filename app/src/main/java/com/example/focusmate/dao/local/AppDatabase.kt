package com.example.focusmate.dao.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.focusmate.ui.theme.data.model.Session
import com.example.focusmate.ui.theme.data.model.Subjects
import com.example.focusmate.ui.theme.data.model.Task

@Database(
    entities = [Subjects::class , Session::class, Task:: class],
    version = 1
)

@TypeConverters(ColorListConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract  fun subjectDao() : SubDao

    abstract  fun taskDao(): TaskDao
    abstract  fun  sessionDao() : SessionDao
}