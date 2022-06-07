package com.kraven.ui.portable.bar.rental.adapter

import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kraven.R
import com.kraven.coreadapter.AdvanceRecycleViewAdapter
import com.kraven.coreadapter.BaseHolder

import com.kraven.ui.portable.bar.rental.model.PortableOrderList
import kotlinx.android.synthetic.main.portable_order_raw.view.*
import java.util.*

class PortableOrderListAdapter(private var itemClickListener: ItemClickListener) : AdvanceRecycleViewAdapter<PortableOrderListAdapter.ViewHolder, PortableOrderList>() {
    override fun createDataHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(makeView(parent, R.layout.portable_order_raw))
    }

    override fun onBindDataHolder(holder: ViewHolder?, position: Int, item: PortableOrderList?) {
        Glide.with(holder!!.itemView.context)
                .load(item!!.barimage)
                .apply(RequestOptions().centerInside())
                .into(holder.imageViewBar)

        holder.textViewOrderId.text = String.format(Locale.US,"%s %s ", holder.itemView.context.getString(R.string.order_id), item.orderId)
        holder.textViewStatus.text = item.orderStatus
        holder.textViewDateTime.text = String.format(Locale.US,"%s %s ", holder.itemView.context.getString(R.string.date_time), item.dateTime)
        holder.textViewPrice.text = String.format(Locale.US,"%s %s ", holder.itemView.context.getString(R.string.price), item.price)

        holder.itemView.setOnClickListener{
            itemClickListener.OnItemClick(position,item)
        }


    }

    class ViewHolder(itemView: View) : BaseHolder<PortableOrderList>(itemView) {
        var imageViewBar = itemView.imageViewBar!!
        var textViewOrderId = itemView.textViewOrderId!!
        var textViewStatus = itemView.textViewDetailsStatus!!
        var textViewDateTime = itemView.textViewDateTime!!
        var textViewPrice = itemView.textViewPrice!!
    }

    interface ItemClickListener {
        fun OnItemClick(position: Int, orderDetails: PortableOrderList)
    }
}