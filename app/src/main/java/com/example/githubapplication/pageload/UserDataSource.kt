package com.example.githubapplication.pageload

import android.util.Log
import androidx.paging.PageKeyedDataSource
import com.example.githubapplication.network.ServiceFactory
import com.example.githubapplication.network.GithubSearchService
import com.example.githubapplication.network.model.UserItem

class UserDataSource : PageKeyedDataSource<Int, UserItem>() {
    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, UserItem>
    ) {
        val job = ServiceFactory.createService(GithubSearchService::class.java)
            .searchUsers("zhanglulu07", null, null, 1, 20)
        val dis = job.subscribe({
            callback.onResult(it.body()!!.users, 1, 2)
        }, {
            Log.d("zll", "loadInitial: $it")
        })
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, UserItem>) {
        val job = ServiceFactory.createService(GithubSearchService::class.java)
            .searchUsers("zhanglulu07", null, null, params.key.toLong(), 20)
        val dis = job.subscribe({
            callback.onResult(it.body()!!.users, params.key + 1)
        }, {
            Log.d("zll", "loadAfter: ${it.message}")
        })
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, UserItem>) {
    }
}