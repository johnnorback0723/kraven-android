package com.kraven.ui.my.order.adapter


import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.kraven.R
import com.kraven.coreadapter.AdvanceRecycleViewAdapter
import com.kraven.coreadapter.BaseHolder
import com.kraven.extensions.viewGone
import com.kraven.ui.my.order.model.BeverageHistory

import com.kraven.utils.Formatter
import kotlinx.android.synthetic.main.restaurant_order_raw.view.*

class AdapterBeverageHistoryList(private var itemClickListener: ItemClickListener) :
        AdvanceRecycleViewAdapter<AdapterBeverageHistoryList.OrderListHolder, BeverageHistory>() {

    override fun createDataHolder(parent: ViewGroup?, viewType: Int): OrderListHolder {
        return OrderListHolder(makeView(parent, R.layout.restaurant_order_raw))
    }

    override fun onBindDataHolder(holder: OrderListHolder, position: Int, item: BeverageHistory) {
        holder.onBindView(item)
    }

    inner class OrderListHolder(itemView: View) : BaseHolder<BeverageHistory>(itemView) {
        fun onBindView(item: BeverageHistory) = with(itemView) {
            textViewRestaurantItem.viewGone()
            textViewOrderId.text = itemView.context.getString(R.string.order_id, item.id.toString())
            textViewOrderStatus.text = item.status

            textViewOrderDateTime.text = itemView.context.getString(R.string.date_time, Formatter.utcToLocal(item.orderDatetime!!,
                    "yyyy-MM-dd HH-mm-ss", Formatter.DD_MMM_YYYY_hh_mm_aa))

            Glide.with(itemView.context!!)
                    .load(item.beverage?.banner)
                    .apply(RequestOptions.bitmapTransform(MultiTransformation(CenterCrop(), RoundedCorners(15))))
                    .into(imageViewRestaurant)

            imageViewFav.background = if (item.beverage?.isFavourite == 1) ContextCompat.getDrawable(itemView.context, R.drawable.ic_fav) else ContextCompat.getDrawable(itemView.context, R.drawable.ic_unfav)
            textViewRestaurantName.text = item.beverage?.name
            textViewRestaurantItem.text = item.orderItems?.joinToString(", ", transform = { it.menu!! })
            ratingBarRestaurant.count = if(item.beverage?.rating!=null)item.beverage.rating else 0
            textViewReviewCount.text = itemView.context.getString(R.string.review_restaurant, item.beverage?.reviews.toString())

            setOnClickListener {
                itemClickListener.onItemClick(item)
            }
        }

    }

    interface ItemClickListener {
        fun onItemClick(orderDetails: BeverageHistory)
        fun onClickRatingBar()
    }
}