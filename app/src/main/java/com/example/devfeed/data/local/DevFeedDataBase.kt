package com.example.devfeed.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.devfeed.data.local.dao.RepoDao

@Database(
    entities = [RepoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class DevFeedDatabase : RoomDatabase() {
    abstract fun repoDao(): RepoDao
}