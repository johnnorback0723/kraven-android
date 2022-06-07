package com.kraven.ui.order.beverage.adapter

import android.view.View
import android.view.ViewGroup
import com.kraven.R
import com.kraven.coreadapter.AdvanceRecycleViewAdapter
import com.kraven.coreadapter.BaseHolder
import com.kraven.ui.order.beverage.model.SpecialBeverage
import com.kraven.utils.Formatter
import kotlinx.android.synthetic.main.raw_sepcial_beverage.view.*

class AdapterSpecialBeverage(private var itemClickListener: ItemClickListener) :
        AdvanceRecycleViewAdapter<AdapterSpecialBeverage.ViewHolder, SpecialBeverage>() {

    override fun createDataHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(makeView(parent, R.layout.raw_sepcial_beverage))
    }

    override fun onBindDataHolder(holder: ViewHolder, position: Int, item: SpecialBeverage) {
        holder.onBindView(item)
    }

    inner class ViewHolder(view: View) : BaseHolder<SpecialBeverage>(view) {
        fun onBindView(item: SpecialBeverage) = with(itemView) {
            textViewOrderId.text = itemView.context.getString(R.string.order_id, item.id.toString())
            textViewOrderStatus.text = item.status
            textViewOrderDateTime.text = itemView.context.getString(R.string.date_time, Formatter.utcToLocal(item.deliveryDatetime!!,
                    "yyyy-MM-dd HH-mm-ss", Formatter.DD_MMM_YYYY_hh_mm_aa))

            setOnClickListener {
                itemClickListener.onItemClick(item)
            }
        }
    }

    interface ItemClickListener {
        fun onItemClick(specialBeverage: SpecialBeverage)
        fun onClickRatingBar()
    }
}