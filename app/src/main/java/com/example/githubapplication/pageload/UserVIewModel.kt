package com.example.githubapplication.pageload

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.githubapplication.network.model.UserItem

class UserVIewModel: ViewModel {
    public lateinit var userPagedList: LiveData<PagedList<UserItem>>

    constructor() {
        val config: PagedList.Config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(20)
            .setPrefetchDistance(3)
            .build()
        val factory = UserDataSourceFactory()
        userPagedList = LivePagedListBuilder<Int, UserItem>(factory, config).build()
    }

}