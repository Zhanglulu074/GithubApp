package com.example.githubapplication.pageload

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.example.githubapplication.network.model.UserItem


class UserDataSourceFactory: DataSource.Factory<Int, UserItem>() {

    private var liveDataSource: MutableLiveData<UserDataSource> = MutableLiveData()

    override fun create(): DataSource<Int, UserItem> {
        val dataSource = UserDataSource()

        liveDataSource.postValue(dataSource)
        return dataSource
    }
}