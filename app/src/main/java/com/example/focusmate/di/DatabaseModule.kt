package com.example.focusmate.di

import android.app.Application
import androidx.room.Room
import com.example.focusmate.dao.local.AppDatabase
import com.example.focusmate.dao.local.SessionDao
import com.example.focusmate.dao.local.SubDao
import com.example.focusmate.dao.local.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        application: Application
    ): AppDatabase{
        return Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            "focusmate.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideSubjectDao(
        database: AppDatabase
    ): SubDao{
        return database.subjectDao()
    }

    @Provides
    @Singleton
    fun provideTaskDao(
        database: AppDatabase
    ): TaskDao{
        return database.taskDao()
    }

    @Provides
    @Singleton
    fun provideSessionDao(
        database: AppDatabase
    ): SessionDao{
        return database.sessionDao()
    }

}