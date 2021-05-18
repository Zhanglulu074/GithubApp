package com.example.githubapplication.network

import com.example.githubapplication.network.model.RepoItem
import com.example.githubapplication.network.model.UserItem
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubUserService {

    @GET("users/{username}")
    fun getUserForName(@Path("username") username: String?): Single<Response<UserItem>>

    @GET("users/{username}/repos")
    fun getUserRepositories(
        @Path("username") username: String?,
        @Query("page") page: Long
    ): Single<Response<List<RepoItem>>>
}