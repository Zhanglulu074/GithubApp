package com.example.githubapplication.pageload

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.githubapplication.R
import com.example.githubapplication.databinding.UserItemBinding
import com.example.githubapplication.network.model.UserItem

class UserPagedListAdapter(ctx: Context) :
    PagedListAdapter<UserItem, UserPagedListAdapter.UserItemViewHolder>(DIFF_CALLBACK) {

    private var context: Context = ctx

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UserItem>() {
            override fun areItemsTheSame(oldItem: UserItem, newItem: UserItem): Boolean {
                return oldItem.userName == newItem.userName
            }

            override fun areContentsTheSame(oldItem: UserItem, newItem: UserItem): Boolean {
                return oldItem.userAvatarUrl == newItem.userAvatarUrl
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserItemViewHolder {
        val binding = DataBindingUtil.inflate<UserItemBinding>(
            LayoutInflater.from(context), R.layout.user_item, parent, false)
        return UserItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserItemViewHolder, position: Int) {
        val userItem = getItem(position)
        holder.userItemBinding.userItem = userItem
    }

    class UserItemViewHolder(var userItemBinding: UserItemBinding): RecyclerView.ViewHolder(userItemBinding.root)
}