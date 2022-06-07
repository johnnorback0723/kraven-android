package com.kraven.ui.order.food.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.kraven.R
import com.kraven.coreadapter.AdvanceRecycleViewAdapter
import com.kraven.coreadapter.BaseHolder
import com.kraven.ui.order.food.model.Filters
import kotlinx.android.synthetic.main.row_filter.view.*

class FilterAdapter(val onItemClickListener: OnItemClickListener) : AdvanceRecycleViewAdapter<FilterAdapter.ViewHolder, Filters>() {

    interface OnItemClickListener{
        fun passData(deliveryType:ArrayList<String>,cuisinList :ArrayList<String>)
    }


    override fun createDataHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(makeView(parent, R.layout.row_filter))
    }

    override fun onBindDataHolder(holder: ViewHolder?, position: Int, item: Filters?) {
        holder?.textViewFilterLabel?.text = item?.filterLabel
        holder?.recyclerViewFilterName?.layoutManager = LinearLayoutManager(holder?.itemView?.context)
        val adapterFilterName  = FilterNameAdapter(object :FilterNameAdapter.OnItemClickListener{
            override fun passData(deliveryType: ArrayList<String>, cuisinList: ArrayList<String>) {
                onItemClickListener.passData(deliveryType,cuisinList)
            }

        })
        holder?.recyclerViewFilterName?.adapter = adapterFilterName
        adapterFilterName.items =item?.filterList

    }

    class ViewHolder(itemView: View) : BaseHolder<Filters>(itemView) {
        var textViewFilterLabel = itemView.textViewFilterLabel!!
        var recyclerViewFilterName = itemView.recyclerViewFilterName!!
    }
}
