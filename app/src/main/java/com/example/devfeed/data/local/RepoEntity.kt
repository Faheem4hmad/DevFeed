package com.example.devfeed.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity ("saved_repos")
data class RepoEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val fullName: String,
    val description: String?,
    val language: String?,
    val stars: Int,
    val htmlUrl: String,
    val ownerUsername: String,
    val ownerAvatarUrl: String
)