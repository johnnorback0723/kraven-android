package com.kraven.ui.my.order.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kraven.R
import com.kraven.coreadapter.AdvanceRecycleViewAdapter
import com.kraven.coreadapter.BaseHolder
import com.kraven.ui.home.model.ToppingsItems
import kotlinx.android.synthetic.main.topping_raw.view.*

class ToppingDetailsAdapter : AdvanceRecycleViewAdapter<ToppingDetailsAdapter.ViewHolder, ToppingsItems>() {
    override fun createDataHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.topping_raw, parent, false))
    }

    override fun onBindDataHolder(holder: ViewHolder?, position: Int, item: ToppingsItems?) {
        holder?.onBindView(item)
    }

    inner class ViewHolder(view: View) : BaseHolder<ViewHolder>(view) {
        fun onBindView(item: ToppingsItems?) = with(itemView) {

            textViewToppingsName.text = item?.type
            val adapterInnerList = ToppingInnerListAdapter()
            recyclerViewToppingInnerList.adapter = adapterInnerList
            adapterInnerList.items = item?.toppingList

            item?.toppingList?.forEach { toppingListItem ->
                if (toppingListItem.status != "Unavailable") {
                    //item.minCount = item.minCount?.plus(1)
                    toppingListItem.isCheckItem = true
                }
            }
        }


    }
}