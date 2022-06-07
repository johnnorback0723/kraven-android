package com.kraven.ui.my.offer.adapter

import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kraven.R
import com.kraven.coreadapter.AdvanceRecycleViewAdapter
import com.kraven.coreadapter.BaseHolder
import com.kraven.extensions.viewGone
import com.kraven.extensions.viewShow
import com.kraven.ui.home.model.Promocode
import kotlinx.android.synthetic.main.my_offer_raw.view.*

class MyOfferAdapter(var onItemClickListener: OnItemClickListener) : AdvanceRecycleViewAdapter<MyOfferAdapter.ViewHolder, Promocode>() {


    interface OnItemClickListener {
        fun onItemClick(item: Promocode)
    }

    override fun createDataHolder(parent: ViewGroup?, viewType: Int): ViewHolder = ViewHolder(makeView(parent, R.layout.my_offer_raw))

    override fun onBindDataHolder(holder: ViewHolder?, position: Int, item: Promocode?) {
        Glide.with(holder!!.itemView.context)
                .load(item!!.banner)
                .apply(RequestOptions().placeholder(R.drawable.placeholder_car))
                .apply(RequestOptions().fitCenter())
                .into(holder.imageViewOffer)


        if(item.restaurantId==0 && item.beverageId==0){
            holder.buttonOrderNow.viewGone()
            if(item.description?.isNotEmpty()!! || item.promocode?.isNotEmpty()!!){
                holder.textViewOffer.text = if(item.description?.isNotEmpty()!!)item.description+ " | " + item.promocode else item.promocode
            }else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.imageViewOffer.foreground = ColorDrawable(ContextCompat.getColor(holder.itemView.context, android.R.color.transparent))
                }
            }

        }else{
            holder.buttonOrderNow.viewShow()
            //promoCodeList[position].description + " | " + promoCodeList[position].promocode
            //holder.textViewOffer.text = if(item.description?.isNotEmpty()!!)item.description+ " | " + item.promocode else item.promocode
            if(item.description?.isNotEmpty()!! || item.promocode?.isNotEmpty()!!){
                holder.textViewOffer.text = if(item.description?.isNotEmpty()!!)item.description+ " | " + item.promocode else item.promocode
            }else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.imageViewOffer.foreground = ColorDrawable(ContextCompat.getColor(holder.itemView.context, android.R.color.transparent))
                }
            }
        }

        holder.buttonOrderNow.setOnClickListener {
            onItemClickListener.onItemClick(item)
        }
    }


    class ViewHolder(itemView: View) : BaseHolder<Promocode>(itemView) {
        var imageViewOffer = itemView.imageViewOffer!!
        var textViewOffer = itemView.textViewOffer!!
        var buttonOrderNow = itemView.buttonOrderNow!!
    }
}