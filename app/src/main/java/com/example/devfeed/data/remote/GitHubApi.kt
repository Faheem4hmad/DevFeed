package com.example.devfeed.data.remote

import com.example.devfeed.data.remote.dto.GithubResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface GitHubApi {

    @GET("search/repositories")
    suspend fun getRepositories(
        @Query("q") query: String = "stars:>1000",
        @Query("sort") sort: String = "stars",
        @Query("order") order: String = "desc",
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): GithubResponseDto
}