package com.kraven.ui.payment.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kraven.R
import com.kraven.coreadapter.AdvanceRecycleViewAdapter
import com.kraven.coreadapter.BaseHolder
import com.kraven.ui.payment.setting.model.Card
import kotlinx.android.synthetic.main.raw_card.view.*

class CardListAdapter(var onItemClickListener: OnItemClickListener, var onItemDeleteClickListener: OnItemDeleteClickListener) :
        AdvanceRecycleViewAdapter<CardListAdapter.AdapterCardListViewHolder, Card>() {

    private var lastCheckedPosition = -1

    override fun createDataHolder(parent: ViewGroup?, viewType: Int): AdapterCardListViewHolder {
        return AdapterCardListViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.raw_card, parent, false))
    }

    override fun onBindDataHolder(holder: AdapterCardListViewHolder?, position: Int, item: Card?) {
        //holder?.textViewCardNumber?.text = item?.cardToken


        if(item?.cardToken!!.isNotEmpty()) {
            val firstFour = item.cardToken?.substring(0, 4)
            val lastFour = item.cardToken?.substring(item?.cardToken?.length!! - 4, item.cardToken?.length!!)

            if (item.isExpired != null && item.isExpired == "1") {
                holder?.textViewCardNumber?.text = firstFour + " xxxx xxxx " + lastFour + " (expired)"
                holder?.textViewCardNumber?.setTextColor(Color.RED)
                //holder?.textViewCardNumber?.text = firstFour + " xxxx xxxx " + lastFour
                //holder?.textViewCardNumber?.setTextColor(Color.BLACK)
            } else {
                //holder?.textViewCardNumber?.text = firstFour + " xxxx xxxx " + lastFour + " (expired)"
                //holder?.textViewCardNumber?.setTextColor(Color.RED)
                holder?.textViewCardNumber?.text = firstFour + " xxxx xxxx " + lastFour
                holder?.textViewCardNumber?.setTextColor(Color.BLACK)
            }
        }

        holder?.radioButtonSelectedCard?.isChecked = position == lastCheckedPosition

        holder?.itemView?.setOnClickListener {
            lastCheckedPosition = position
            notifyDataSetChanged()
            onItemClickListener.onItemClick(position)
        }

        holder?.imageViewDelete?.setOnClickListener {
            onItemDeleteClickListener.onItemDeleteClick(item)
        }

    }

    class AdapterCardListViewHolder(view: View) : BaseHolder<Card>(view) {
        var radioButtonSelectedCard = view.radioButtonSelectedCard!!
        var textViewCardNumber = view.textViewCardNumber!!
        var imageViewCardType = view.imageViewCardType!!
        var imageViewDelete = view.imageViewDelete2!!
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    interface OnItemDeleteClickListener {
        fun onItemDeleteClick(card: Card)
    }
}