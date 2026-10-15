package com.example.devfeed.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.devfeed.data.local.RepoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RepoDao {

    // 1. Data Insert / Save karna
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun bookmarkRepo(repo: RepoEntity)

    // 2. Data Delete karna
    @Delete
    suspend fun unbookmarkRepo(repo: RepoEntity)

    // 3. Saved Repositories ki live list fetch karna
    @Query("SELECT * FROM saved_repos ORDER BY stars DESC")
    fun getAllSavedRepos(): Flow<List<RepoEntity>>
}