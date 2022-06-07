package com.kraven.ui.my.order.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.kraven.R
import com.kraven.coreadapter.AdvanceRecycleViewAdapter
import com.kraven.coreadapter.BaseHolder
import com.kraven.ui.my.order.model.OrderList
import kotlinx.android.synthetic.main.order_id_raw.view.*


class OrderListAdapter(var fragmentPosition: Int, private var itemClickListener: ItemClickListener)
    : AdvanceRecycleViewAdapter<OrderListAdapter.OrderListHolder, OrderList>() {

    private var ordrListAdapter: OrderListChildAdapter? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    override fun createDataHolder(parent: ViewGroup, viewType: Int): OrderListHolder {
        return OrderListHolder(makeView(parent, R.layout.order_id_raw))
    }

    override fun onBindDataHolder(holder: OrderListHolder, position: Int, item: OrderList) {

       // holder.textViewOrderId.text = String.format("%s %s", holder.itemView.context.getString(R.string.order_id), item.orderId)
       // holder.textViewOrderStatus.text = item.orderStatus

        /*if (fragmentPosition == 0 && item.orderStatus.equals("Delivered")) {
            holder.buttonReorder.visibility = View.VISIBLE
            holder.buttonReorder.setOnClickListener{
                itemClickListener.onClickReOrder()
            }
        }*/

        /*if (fragmentPosition == 2) {
            holder.viewDivider.visibility = View.VISIBLE
            holder.textViewOrderStatus.setTextSize(TypedValue.COMPLEX_UNIT_PX, holder.itemView.context.resources.getDimension(R.dimen._12ssp))
            holder.textViewBeverageBrand.visibility = View.VISIBLE
            holder.recyclerViewChildOrderList.visibility = View.GONE
            holder.textViewBeverageBrand.text = item.beverageBrand
            holder.textViewOrderDateTime.visibility = View.VISIBLE
            holder.textViewOrderDateTime.text = String.format("%s %s", holder.itemView.context.getString(R.string.date_time), item.dateTime)

            holder.itemView.setOnClickListener {
                itemClickListener.onItemClick(position, item)
            }
        } else {
            holder.viewDivider.visibility = View.GONE
            holder.textViewBeverageBrand.visibility = View.GONE
            holder.recyclerViewChildOrderList.visibility = View.VISIBLE

            linearLayoutManager = LinearLayoutManager(holder.itemView.context)
            holder.recyclerViewChildOrderList.layoutManager = linearLayoutManager
            ordrListAdapter = OrderListChildAdapter(object : OrderListChildAdapter.ItemClickListenerChild {
                override fun onItemChildClick() {
                    itemClickListener.onItemClick(position, item)
                }
            })
            holder.recyclerViewChildOrderList.adapter = ordrListAdapter
            ordrListAdapter!!.items = item.serviceList

            if (item.dateTime.isNullOrEmpty()) {
                holder.textViewOrderDateTime.visibility = View.GONE
            } else {
                holder.textViewOrderDateTime.text = String.format("%s %s", holder.itemView.context.getString(R.string.date_time), item.dateTime)
            }
        }*/
    }


    class OrderListHolder(itemView: View) : BaseHolder<OrderList>(itemView) {
        var textViewOrderId = itemView.textViewOrderId!!
        var textViewOrderStatus = itemView.textViewOrderStatus!!
        var recyclerViewChildOrderList = itemView.recyclerViewChildOrderList!!
        var textViewOrderDateTime = itemView.textViewOrderDateTime!!
        var textViewBeverageBrand = itemView.textViewBeverageBrand!!
        var viewDivider = itemView.viewDivider!!
        var buttonReorder = itemView.buttonReorder!!

    }

    interface ItemClickListener {
        fun OnItemClick(position: Int, orderDetails: OrderList)
        fun onClickReOrder()
    }
}