package com.kraven.ui.order.beverage.adapter

import android.view.View
import android.view.ViewGroup
import com.kraven.R
import com.kraven.coreadapter.AdvanceRecycleViewAdapter
import com.kraven.coreadapter.BaseHolder
import com.kraven.ui.order.beverage.model.Beverage
import kotlinx.android.synthetic.main.order_beverage_list_raw.view.*

class BeverageOrderDetailsAdapter(private var itemClickListener: ItemClickListenerChild) : AdvanceRecycleViewAdapter<BeverageOrderDetailsAdapter.OrderListChildeHolder, Beverage>() {
    override fun createDataHolder(parent: ViewGroup?, viewType: Int): BeverageOrderDetailsAdapter.OrderListChildeHolder {
        return BeverageOrderDetailsAdapter.OrderListChildeHolder(makeView(parent, R.layout.order_beverage_list_raw))
    }

    override fun onBindDataHolder(holder: BeverageOrderDetailsAdapter.OrderListChildeHolder?, position: Int, item: Beverage?) {
       /* Glide.with(holder?.itemView?.context!!)
                .load(item?.beverageImage)
                .apply(RequestOptions().placeholder(R.drawable.ic_user))
                .apply(RequestOptions.bitmapTransform(MultiTransformation(CenterCrop(), RoundedCorners(15))))
                .into(holder.imageViewBeverage)
        holder.textViewBeverageName.text = item!!.beverageName
        holder.textViewBeverageItem.text = item.beverageType
        holder.textViewBeveragePrice.text = String.format("%s %s", holder.itemView.context.getString(R.string.doller_sign), item.beveragePrice.toString())
        holder.textViewItemMl.text = item.beverageMl

        if (item.special_note.equals("1")) {
            holder.textViewAddSpecialNote.visibility = View.VISIBLE
            holder.textViewSpecialNote.visibility = View.VISIBLE
            holder.textViewSpecialNote.text = holder.itemView.context.getString(R.string.long_text)
        } else {
            holder.textViewAddSpecialNote.visibility = View.GONE
            holder.textViewSpecialNote.visibility = View.GONE
        }*/
    }


    class OrderListChildeHolder(itemView: View) : BaseHolder<Beverage>(itemView) {
        var imageViewBeverage = itemView.imageViewBeverage!!
        var textViewBeverageName = itemView.textViewBeverageName!!
        var textViewQty = itemView.textViewQty!!
        var textViewBeverageItem = itemView.textViewBeverageItem!!
        var textViewBeveragePrice = itemView.textViewBeveragePrice!!
        var textViewItemMl = itemView.textViewItemMl!!
        var textViewAddSpecialNote = itemView.textViewAddSpecialNote!!
        var textViewSpecialNote = itemView.textViewSpecialNote!!

    }

    interface ItemClickListenerChild {
        fun OnItemChildClick()
    }
}
