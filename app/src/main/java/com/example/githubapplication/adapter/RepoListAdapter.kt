package com.example.githubapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.RecyclerView
import com.example.githubapplication.R
import com.example.githubapplication.databinding.RepoItemBinding
import com.example.githubapplication.databinding.UserItemBinding
import com.example.githubapplication.network.model.RepoItem
import com.example.githubapplication.network.model.UserItem

class RepoListAdapter(ctx: Context, itemList: ObservableList<RepoItem>) :
    RootAdapter<RepoItem, RepoListAdapter.RepoItemViewHolder>(ctx, itemList) {
    init {
        footerListener = object : ScrollToFooterListener {
            override fun onScrollToFooter() {

            }
        }
    }

    override fun onBindViewHolder(holder: RepoItemViewHolder, position: RepoItem?) {
        holder.itemBind.repoItem = position
    }


    override fun onCreateOwnViewHolder(parent: ViewGroup, viewType: Int): RepoItemViewHolder {
        val itemBind: RepoItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.user_item, parent, false)
        return RepoItemViewHolder(itemBind)
    }

    class RepoItemViewHolder(var itemBind: RepoItemBinding): RecyclerView.ViewHolder(itemBind.root)

}