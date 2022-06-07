package com.kraven.ui.order.beverage.adapter

import android.view.View
import android.view.ViewGroup
import com.kraven.R
import com.kraven.coreadapter.AdvanceRecycleViewAdapter
import com.kraven.coreadapter.BaseHolder
import com.kraven.ui.order.beverage.model.BeverageStore
import kotlinx.android.synthetic.main.beverage_store_raw.view.*

class BeverageStoreAdapter(val itemClickListener: ItemClickListener) : AdvanceRecycleViewAdapter<BeverageStoreAdapter.ViewHolder, BeverageStore>() {

    override fun createDataHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(makeView(parent, R.layout.beverage_store_raw))
    }

    override fun onBindDataHolder(holder: ViewHolder?, position: Int, item: BeverageStore?) {
        holder?.textViewStoreName?.text = item?.storeName
        holder?.itemView?.setOnClickListener {
            itemClickListener.onItemClick()
        }
    }


    class ViewHolder(itemView: View) : BaseHolder<BeverageStore>(itemView) {
        var textViewStoreName = itemView.textViewStoreName!!
    }

    interface ItemClickListener {
        fun onItemClick()
    }
}