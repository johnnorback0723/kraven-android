package com.kraven.ui.bartender.adapter

import android.view.View
import android.view.ViewGroup
import com.kraven.R
import com.kraven.coreadapter.AdvanceRecycleViewAdapter
import com.kraven.coreadapter.BaseHolder
import com.kraven.ui.bartender.model.BartenderOrderList
import kotlinx.android.synthetic.main.bartender_order_raw.view.*
import java.util.*

class BartenderOrderListAdapter(var itemClickListener: BartenderOrderListAdapter.ItemClickListener) : AdvanceRecycleViewAdapter<BartenderOrderListAdapter.ViewHolder, BartenderOrderList>() {

    override fun createDataHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(makeView(parent, R.layout.bartender_order_raw))
    }

    override fun onBindDataHolder(holder: ViewHolder?, position: Int, item: BartenderOrderList?) {
        holder!!.textViewOrderId.text = String.format(Locale.US,"%s %s ", holder.itemView.context.getString(R.string.order_id), item!!.orderId)
        holder.textViewStatus.text = item.orderStatus
        holder.textViewDateTime.text = String.format(Locale.US,"%s %s ", holder.itemView.context.getString(R.string.date_time), item.dateTime)
        holder.textViewPrice.text = String.format(Locale.US,"%s %s ", holder.itemView.context.getString(R.string.price), item.price)

        holder.itemView.setOnClickListener{
            itemClickListener.OnItemClick(position,item)
        }
    }


    class ViewHolder(itemView: View) : BaseHolder<BartenderOrderList>(itemView) {
        var textViewOrderId = itemView.textViewOrderId!!
        var textViewStatus = itemView.textViewDetailsStatus!!
        var textViewDateTime = itemView.textViewDateTime!!
        var textViewPrice = itemView.textViewPrice!!

    }

    interface ItemClickListener {
        fun OnItemClick(position: Int, orderDetails: BartenderOrderList)
    }
}