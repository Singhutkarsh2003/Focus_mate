package com.example.focusmate.di

import com.example.focusmate.dao.repository.SessionRepositoryImpl
import com.example.focusmate.dao.repository.SubRepositoryImpl
import com.example.focusmate.dao.repository.TaskRepositoryImpl
import com.example.focusmate.ui.theme.data.repository.SessionRepository
import com.example.focusmate.ui.theme.data.repository.SubRepository
import com.example.focusmate.ui.theme.data.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
 abstract class RepositoryModule {

     @Singleton
     @Binds
     abstract fun bindSubRepository(
         impl: SubRepositoryImpl
     ): SubRepository

    @Singleton
    @Binds
    abstract fun bindTaskRepository(
        impl: TaskRepositoryImpl
    ): TaskRepository

    @Singleton
    @Binds
    abstract fun bindSessionRepository(
        impl: SessionRepositoryImpl
    ): SessionRepository
}