package com.kraven.ui.my.order.adapter

import android.view.View
import android.view.ViewGroup
import com.kraven.R
import com.kraven.coreadapter.AdvanceRecycleViewAdapter
import com.kraven.coreadapter.BaseHolder
import com.kraven.ui.my.order.model.OrderList
import kotlinx.android.synthetic.main.order_list_raw.view.*


class OrderListChildAdapter(private var itemClickListener: ItemClickListenerChild) :
        AdvanceRecycleViewAdapter<OrderListChildAdapter.OrderListChildHolder, OrderList>() {
    override fun createDataHolder(parent: ViewGroup?, viewType: Int): OrderListChildHolder {
        return OrderListChildAdapter.OrderListChildHolder(makeView(parent, R.layout.order_list_raw))
    }

    override fun onBindDataHolder(holder: OrderListChildHolder?, position: Int, item: OrderList) {
      /*  holder!!.imageViewVegNonVeg.setBackgroundResource(if (item!!.vegNonVeg.equals("1")) R.drawable.ic_veg else R.drawable.ic_nonveg)
        holder.textViewFoodName.text = item.foodName
        holder.textViewQty.text = String.format("%s %s", holder.itemView.context.getString(R.string.qty), item.qty)
        holder.textViewFoodType.text = item.foodType
        holder.textViewFoodPrice.text = item.foodPrice
        holder.textViewTopping.text = String.format("%s %s", holder.itemView.context.getString(R.string.topping), item.topping)

        if (item.topping.isNullOrEmpty()) {
            holder.textViewTopping.visibility = View.GONE
        } else {
            holder.textViewTopping.visibility = View.VISIBLE
        }

        holder.itemView.setOnClickListener {
            itemClickListener.onItemChildClick()
        }*/
    }

    class OrderListChildHolder(itemView: View) : BaseHolder<OrderList>(itemView) {
        var imageViewVegNonVeg = itemView.imageViewVegNonVeg!!
        var textViewFoodName = itemView.textViewFoodName!!
        var textViewQty = itemView.textViewQty!!
        var textViewFoodType = itemView.textViewFoodType!!
        var textViewFoodPrice = itemView.textViewFoodPrice!!
        var textViewSpecialNoteLabel = itemView.textViewSpecialNoteLabel!!
        var textViewSpecialNote = itemView.textViewSpecialNote!!

    }

    interface ItemClickListenerChild {
        fun OnItemChildClick()
    }
}