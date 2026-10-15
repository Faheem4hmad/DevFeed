package com.example.devfeed.di

import android.app.Application
import androidx.room.Room
import com.example.devfeed.data.local.DevFeedDatabase
import com.example.devfeed.data.local.dao.RepoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    // 1. Poore app ke liye ek hi Database instance create karna
    @Provides
    @Singleton
    fun provideDatabase(app: Application): DevFeedDatabase {
        return Room.databaseBuilder(
            app,
            DevFeedDatabase::class.java,
            "devfeed_database.db" // Phone ke storage me is naam ki SQLite file banegi
        ).build()
    }

    // 2. ViewModel ko directly RepoDao provide karna
    @Provides
    @Singleton
    fun provideRepoDao(database: DevFeedDatabase): RepoDao {
        return database.repoDao()
    }
}