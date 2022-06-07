package com.kraven.ui.order.beverage.adapter

import android.view.View
import android.view.ViewGroup
import com.kraven.R
import com.kraven.coreadapter.AdvanceRecycleViewAdapter
import com.kraven.coreadapter.BaseHolder
import com.kraven.ui.order.beverage.model.BeverageAdd
import kotlinx.android.synthetic.main.beverate_add_raw.view.*

class BeverageAddAdapter(val onItemClickListner: OnItemClickListner) : AdvanceRecycleViewAdapter<BeverageAddAdapter.ViewHolder, BeverageAdd>() {

    override fun createDataHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(makeView(parent, R.layout.beverate_add_raw))
    }

    override fun onBindDataHolder(holder: ViewHolder?, position: Int, item: BeverageAdd?) {
        holder?.bindView(item)
    }

    inner class ViewHolder(val view: View) : BaseHolder<BeverageAdd>(view) {
        fun bindView(item: BeverageAdd?) = with(view) {
            textViewDate.text = itemView.context.getString(R.string.column_text,item?.deliveryDate)
            textViewName.text = itemView.context.getString(R.string.column_text,item?.beverageName)
            textViewBrand.text = itemView.context.getString(R.string.column_text,item?.beverageBrand)
            textViewQuantityType.text = itemView.context.getString(R.string.column_text,item?.quantityType)
            textViewQuantity.text =itemView.context.getString(R.string.column_text,item?.quantity)
            textViewEditAddress.setOnClickListener { onItemClickListner.onEdit(item,adapterPosition) }
            textViewDeleteAddress.setOnClickListener { onItemClickListner.onDelete(item) }
        }

    }

    interface OnItemClickListner {
        fun onEdit(item: BeverageAdd?,position: Int)
        fun onDelete(item: BeverageAdd?)
    }
}