package com.kraven.ui.comman.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat


import com.kraven.R

import androidx.recyclerview.widget.RecyclerView
import com.kraven.coreadapter.AbstractRecycleViewAdapter
import kotlinx.android.synthetic.main.drawer_row.view.*

/**
 * Created by Android Dev on 1/5/18.
 */
class DrawerAdapter(mList: List<String>, private var drawerAdapterInterface: DrawerAdapterInterface) :
        AbstractRecycleViewAdapter<String, DrawerAdapter.DrawerHolder>(mList) {


    companion object {
        var row_index: Int? = 0
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): DrawerHolder {
        return DrawerHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.drawer_row, viewGroup, false))
    }

    override fun onBindViewHolder(drawerHolder: DrawerHolder, position: Int) {
        drawerHolder.textViewName.text = list?.get(position)
        drawerHolder.textViewName.setOnClickListener {
            drawerAdapterInterface.onClickItem(position)
            row_index = position
            notifyDataSetChanged()
        }
        drawerHolder.textViewName.setTextColor(if (row_index == position) ContextCompat.getColor(drawerHolder.itemView.context, R.color.orange)
        else ContextCompat.getColor(drawerHolder.itemView.context, R.color.black))
    }

    fun setPosition(position: Int) {
        row_index = position
        notifyDataSetChanged()
    }

    override fun onAdded() {

    }

    override fun onRemoved() {

    }


    interface DrawerAdapterInterface {
        fun onClickItem(position: Int)
    }

    inner class DrawerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName = itemView.textViewName!!
    }


}
