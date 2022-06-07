package com.kraven.ui.order.food.adapter

import android.view.View
import android.view.ViewGroup
import com.kraven.R
import com.kraven.coreadapter.AdvanceRecycleViewAdapter
import com.kraven.coreadapter.BaseHolder
import com.kraven.ui.order.food.model.Filters

import kotlinx.android.synthetic.main.row_filter_name.view.*


class FilterNameAdapter(val onItemClickListener: OnItemClickListener) : AdvanceRecycleViewAdapter<FilterNameAdapter.ViewHolderName, Filters.FilterName>() {

    private var deliveryType=ArrayList<String>()
    private var cuisinList = ArrayList<String>()

    interface OnItemClickListener {
        fun passData(deliveryType: ArrayList<String>, cuisinList: ArrayList<String>)
    }


    override fun onBindDataHolder(holder: ViewHolderName?, position: Int, item: Filters.FilterName?) {
        holder?.textViewName?.text = item?.name

        holder?.textViewName?.setOnClickListener {
            it.isSelected = !it.isSelected

            if (item!!.id != 0) {
                if (it.isSelected) cuisinList.add(item.id.toString()) else cuisinList.remove(item.id.toString())
            } else {
                if (it.isSelected){
                    deliveryType.add(item.name)
                }else{
                    deliveryType.remove(item.name)
                }


                //deliveryType = if (it.isSelected) getText(holder.textViewName).toString() else ""
            }
            onItemClickListener.passData(deliveryType, cuisinList)
        }
    }

    override fun createDataHolder(parent: ViewGroup?, viewType: Int): ViewHolderName {
        return ViewHolderName(makeView(parent, R.layout.row_filter_name))
    }

    class ViewHolderName(itemView: View) : BaseHolder<Filters.FilterName>(itemView) {
        var textViewName = itemView.textViewName!!
    }
}