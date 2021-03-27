package com.example.githubapplication.network.model

import com.google.gson.annotations.SerializedName

class RepoSearchResponse(
    @SerializedName("items")
    var repos: List<RepoItem>,
    @SerializedName("total_count")
    var totalCount: Long
) {
    companion object {
        public const val PAGE_SIZE = 10
    }
}

class RepoItem(
    @SerializedName("full_name")
    var repoName: String,
    @SerializedName("language")
    var repoLanguage: String,
    @SerializedName("stargazers_count")
    var startCount: String,
    @SerializedName("size")
    var repoSize: String
)