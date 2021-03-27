package com.example.githubapplication.network

import com.example.githubapplication.network.model.RepoSearchResponse
import com.example.githubapplication.network.model.UserSearchResponse
import io.reactivex.Single
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

public interface GithubSearchService {
    @GET("/search/users")
    fun searchUsers(
        @Query("q") query: String?,
        @Query("sort") sort: String?,
        @Query("order") order: String?,
        @Query("page") page: Long,
        @Query("per_page") pageSize: Long
    ): Single<Response<UserSearchResponse>>

    @GET("/search/repositories")
    fun searchRepositories(
        @Query("q") query: String?,
        @Query("sort") sort: String?,
        @Query("order") order: String?,
        @Query("page") page: Long,
        @Query("per_page") pageSize: Long
    ): Single<Response<RepoSearchResponse>>
}