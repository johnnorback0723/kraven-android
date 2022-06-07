package com.kraven.ui.edit.profile.adapter

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import com.kraven.R
import com.kraven.coreadapter.AdvanceRecycleViewAdapter
import com.kraven.coreadapter.BaseHolder
import com.kraven.coreadapter.OnRecycleItemClick
import com.kraven.ui.payment.setting.model.Card
import kotlinx.android.synthetic.main.raw_saved_cards.view.*

class AdapterSavedCards(private val onRecycleItemClick: OnRecycleItemClick<Card>) : AdvanceRecycleViewAdapter<AdapterSavedCards.ViewModel, Card>() {


    override fun createDataHolder(parent: ViewGroup?, viewType: Int): ViewModel {
        return ViewModel(makeView(parent, R.layout.raw_saved_cards))
    }

    override fun onBindDataHolder(holder: ViewModel, position: Int, item: Card) {
        holder.onBindView(item)
    }


    inner class ViewModel(view: View) : BaseHolder<Card>(view) {
        fun onBindView(item: Card) = with(itemView) {
            if (item.cardToken != null && item.cardToken!!.isNotEmpty()) {
                val firstFour = item.cardToken?.substring(0, 4)
                val lastFour = item.cardToken?.substring(item.cardToken?.length!! - 4, item.cardToken?.length!!)

                if (item.isExpired != null && item.isExpired == "1") {
                    textViewCardNumber?.text = firstFour + " xxxx xxxx " + lastFour + " (expired)"
                    textViewCardNumber?.setTextColor(Color.RED)
                    //textViewCardNumber?.text = firstFour + " xxxx xxxx " + lastFour
                    //textViewCardNumber?.setTextColor(Color.BLACK)
                } else {
                    //textViewCardNumber?.text = firstFour + " xxxx xxxx " + lastFour + " (expired)"
                    //textViewCardNumber?.setTextColor(Color.RED)
                    textViewCardNumber?.text = firstFour + " xxxx xxxx " + lastFour
                    textViewCardNumber?.setTextColor(Color.BLACK)
                }
            }
            imageViewDelete.setOnClickListener {
                onRecycleItemClick.onClick(item, it)
            }
        }
    }
}