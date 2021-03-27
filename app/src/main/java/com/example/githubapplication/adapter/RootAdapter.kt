package com.example.githubapplication.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.githubapplication.databinding.UserItemBinding


public abstract class RootAdapter<T, VH : ViewHolder> :
    RecyclerView.Adapter<RecyclerView.ViewHolder> {

    companion object {
        const val FOOT_VIEW_TYPE = 1
        const val NORMAL_ITEM_TYPE = 2

    }

    class FootViewHolder(var v: View): RecyclerView.ViewHolder(v)


    var mObjects: ObservableList<T>? = null
    var context: Context? = null
    public var footView: View? = null
    var footerListener: ScrollToFooterListener? = null

    constructor(ctx: Context, itemList: ObservableList<T>): super() {
        mObjects = itemList
        context = ctx
        mObjects?.addOnListChangedCallback(object :
            ObservableList.OnListChangedCallback<ObservableList<T>>() {
            override fun onChanged(sender: ObservableList<T>?) {
                notifyDataSetChanged()
            }

            override fun onItemRangeRemoved(
                sender: ObservableList<T>?,
                positionStart: Int,
                itemCount: Int
            ) {
                notifyItemRangeRemoved(positionStart, itemCount)
            }

            override fun onItemRangeMoved(
                sender: ObservableList<T>?,
                fromPosition: Int,
                toPosition: Int,
                itemCount: Int
            ) {
            }

            override fun onItemRangeInserted(
                sender: ObservableList<T>?,
                positionStart: Int,
                itemCount: Int
            ) {
                notifyItemInserted(positionStart)
            }

            override fun onItemRangeChanged(
                sender: ObservableList<T>?,
                positionStart: Int,
                itemCount: Int
            ) {
                notifyItemRangeChanged(positionStart, itemCount)
            }

        })
    }

    fun clear() {
        mObjects?.clear()
    }

    fun add(item: T) {
        mObjects?.add(item)
    }

    fun remove(item: T) {
        mObjects?.remove(item)
    }

    fun addAll(items: List<T>) {
        mObjects?.addAll(items)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == FOOT_VIEW_TYPE) {
            return FootViewHolder(footView!!)
        } else {
            return onCreateOwnViewHolder(parent, viewType)
        }
    }

    override fun getItemCount(): Int {
        return (mObjects?.size ?: 0) + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d("zll", "onBindViewHolder: ${position}")
        if (holder is FootViewHolder) {
            footerListener?.onScrollToFooter()
        } else {
            onBindViewHolder(holder as VH, mObjects?.get(position))
        }
    }

    protected abstract fun onBindViewHolder(holder: VH, position: T?)

    protected abstract fun onCreateOwnViewHolder(parent: ViewGroup, viewType: Int): VH

    override fun getItemViewType(position: Int): Int {
        Log.d("MainActivity", "getItemViewType: $position ${(mObjects?.size?:0) - 1}")
        return if (position == (mObjects?.size?:0)) {
            FOOT_VIEW_TYPE
        } else {
            NORMAL_ITEM_TYPE
        }
    }

    interface ScrollToFooterListener {
        fun onScrollToFooter()
    }


}