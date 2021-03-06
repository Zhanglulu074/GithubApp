package com.example.githubapplication.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.RecyclerView
import com.example.githubapplication.R
import com.example.githubapplication.UserActivity
import com.example.githubapplication.databinding.UserItemBinding
import com.example.githubapplication.network.model.UserItem

class UserListAdapter(ctx: Context) :
    RootAdapter<UserItem, UserListAdapter.UserItemViewHolder>(ctx), View.OnClickListener {
    init {
        footerListener = object : ScrollToFooterListener {
            override fun onScrollToFooter() {

            }

        }
    }

    override fun onCreateOwnViewHolder(parent: ViewGroup, viewType: Int): UserItemViewHolder {
        val itemBind: UserItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.user_item, parent, false)
        return UserItemViewHolder(itemBind)
    }

    public override fun onBindViewHolder(holder: UserItemViewHolder, item: UserItem?) {
        holder.itemBind.userItem = item
        holder.itemBind.root.setOnClickListener {
            context?.startActivity(Intent(context, UserActivity::class.java).putExtra("username", item?.userName))
        }
    }

    class UserItemViewHolder(var itemBind: UserItemBinding): RecyclerView.ViewHolder(itemBind.root)

    override fun onClick(v: View?) {

    }
}