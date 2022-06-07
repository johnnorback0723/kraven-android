package com.kraven.coreadapter


import android.view.View

import androidx.recyclerview.widget.RecyclerView

open class BaseHolder<E> : RecyclerView.ViewHolder, View.OnClickListener {

    private var onRecycleItemClick: OnRecycleItemClick<E>? = null
    private var hasItem: HasItem<E>? = null

    val current: E
        get() = hasItem!!.getItem(layoutPosition)

    constructor(itemView: View) : super(itemView) {
        itemView.setOnClickListener(this)
    }

    constructor(itemView: View, onRecycleItemClick: OnRecycleItemClick<E>) : super(itemView) {
        this.onRecycleItemClick = onRecycleItemClick
        itemView.setOnClickListener(this)
    }

    fun setHasItem(hasItem: HasItem<E>) {
        this.hasItem = hasItem
    }

    override fun onClick(v: View) {
        onRecycleItemClick?.onClick(hasItem!!.getItem(layoutPosition), v)
    }
}