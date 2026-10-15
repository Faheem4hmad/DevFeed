package com.example.devfeed.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GithubResponseDto(
    @SerializedName("total_count")
    val totalCount: Int,

    @SerializedName("incomplete_results")
    val incompleteResults: Boolean,

    @SerializedName("items")
    val items: List<GithubRepoDto>

)
