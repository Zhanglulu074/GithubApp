package com.example.githubapplication.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.RecyclerView
import com.example.githubapplication.SearchFragment
import com.example.githubapplication.network.model.RepoItem
import com.example.githubapplication.network.model.UserItem

class SearchAdapter : RootAdapter<Any, RecyclerView.ViewHolder> {

    var searchMode: Int = SearchFragment.TYPE_USER

    private lateinit var userListAdapter: UserListAdapter
    private lateinit var repoListAdapter: RepoListAdapter

    constructor(ctx: Context) : super(ctx) {
        userListAdapter = UserListAdapter(ctx)
        repoListAdapter = RepoListAdapter(ctx)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any?) {
        when (searchMode) {
            SearchFragment.TYPE_REPO -> {
                if (holder is RepoListAdapter.RepoItemViewHolder) {
                    repoListAdapter.onBindViewHolder(
                        holder as RepoListAdapter.RepoItemViewHolder,
                        item as RepoItem
                    )
                }
            }
            SearchFragment.TYPE_USER -> {
                if (holder is UserListAdapter.UserItemViewHolder) {
                    userListAdapter.onBindViewHolder(
                        holder as UserListAdapter.UserItemViewHolder,
                        item as UserItem
                    )
                }
            }
        }
    }

    override fun onCreateOwnViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (searchMode) {
            SearchFragment.TYPE_USER -> {
                userListAdapter.onCreateViewHolder(parent, viewType)
            }
            SearchFragment.TYPE_REPO -> {
                repoListAdapter.onCreateViewHolder(parent, viewType)
            }
            else -> {
                repoListAdapter.onCreateViewHolder(parent, viewType)
            }
        }
    }

}