package com.kraven.ui.home.adapter


import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kraven.R
import com.kraven.core.AppExecutors
import com.kraven.coreadapter.AdvanceRecycleViewAdapter
import com.kraven.coreadapter.BaseHolder
import com.kraven.extensions.convertTwoDigit
import com.kraven.extensions.viewGone
import com.kraven.extensions.viewShow
import com.kraven.ui.home.model.ToppingListItem

import kotlinx.android.synthetic.main.toping_raw.view.*
import kotlin.collections.ArrayList


class ToppingEditAdapter(var onItemClickListener: OnClickListener, var isBeverage: Boolean) : AdvanceRecycleViewAdapter<ToppingEditAdapter.AdapterToppingViewHolder,
        ToppingListItem>() {


    var toppinInnerList = ArrayList<ToppingListItem>()
    var appExecutors = AppExecutors()


    override fun onBindDataHolder(holder: AdapterToppingViewHolder?, position: Int, item: ToppingListItem?) {
        holder?.onBindView(item)
    }

    override fun createDataHolder(parent: ViewGroup?, viewType: Int): AdapterToppingViewHolder {
        return AdapterToppingViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.toping_raw, parent, false))
    }


    inner class AdapterToppingViewHolder(view: View) : BaseHolder<ToppingListItem>(view) {
        fun onBindView(item: ToppingListItem?) = with(itemView) {
            checkBoxSelectTopping?.post {
                checkBoxSelectTopping?.isSelected = item!!.isCheckItem
            }

            textViewNameTopping.text = "${item!!.name} "

            if (item.price == 0.0F) {
                textViewPrice.viewGone()
            } else {
                textViewPrice.viewShow()
                if (isBeverage.not()) {
                    textViewPrice.text = "+".plus(convertTwoDigit(item.price!!))
                } else {
                    textViewPrice.text = convertTwoDigit(item.price!!)
                }
            }

            if (item.status == "Unavailable") {
                textViewNameTopping.paintFlags = textViewNameTopping.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                textViewCurrentlyUnavailable.visibility = View.VISIBLE
                textViewCurrentlyUnavailable.text = "${item.name} ${"is " + itemView.context.getString(R.string.currentlyUnavailable)}"

            } else {
                setOnClickListener {
                    onItemClickListener.onItemClick(item, adapterPosition, toppinInnerList)
                }
                textViewCurrentlyUnavailable.visibility = View.GONE
            }

        }
    }

    fun setCheckTrue(adapterPosition: Int) {
        items[adapterPosition].isCheckItem = !items[adapterPosition].isCheckItem
        notifyItemChanged(adapterPosition)
    }

    interface OnClickListener {
        fun onItemClick(toppingListItem: ToppingListItem?, adapterPosition: Int, toppinInnerList: ArrayList<ToppingListItem>)
    }
}

