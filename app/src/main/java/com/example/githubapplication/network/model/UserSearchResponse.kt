package com.example.githubapplication.network.model

import com.google.gson.annotations.SerializedName

class UserSearchResponse(
    @SerializedName("items")
    var users: List<UserItem>,
    @SerializedName("total_count")
    var totalCount: Long
) {
    companion object {
        public const val PAGE_SIZE = 10
    }
}

class UserItem(
    @SerializedName("login")
    var userName: String,
    @SerializedName("avatar_url")
    var userAvatarUrl: String
)