package com.kraven.ui.restaurant.reservation.adapter


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
import com.kraven.ui.my.order.model.OrderList
import com.kraven.utils.Formatter


import kotlinx.android.synthetic.main.restaurant_order_raw.view.*

class RestaurantOrderListAdapter(private var itemClickListener: ItemClickListener) :
        AdvanceRecycleViewAdapter<RestaurantOrderListAdapter.OrderListHolder, OrderList>() {

    override fun createDataHolder(parent: ViewGroup?, viewType: Int): OrderListHolder {
        return OrderListHolder(makeView(parent, R.layout.restaurant_order_raw))
    }

    override fun onBindDataHolder(holder: OrderListHolder?, position: Int, item: OrderList?) {
        holder?.onBindView(item)

    }

    inner class OrderListHolder(itemView: View) : BaseHolder<OrderList>(itemView) {
        fun onBindView(item: OrderList?) = with(itemView) {
            textViewOrderId.text = itemView.context.getString(R.string.order_id, item?.id.toString())
            textViewOrderStatus.text = item?.status
            textViewOrderDateTime.text = itemView.context.getString(R.string.date_time, Formatter.utcToLocal(item?.orderDatetime!!,
                    "yyyy-MM-dd HH-mm-ss", Formatter.DD_MMM_YYYY_hh_mm_aa))

            Glide.with(itemView.context!!)
                    .load(item.restaurant.banner)
                    /*.apply(RequestOptions().placeholder(R.drawable.ic_user))*/
                    .apply(RequestOptions.bitmapTransform(MultiTransformation(CenterCrop(), RoundedCorners(15))))
                    .into(imageViewRestaurant)

            imageViewFav.background = if (item.restaurant.isFavourite == 1) ContextCompat.getDrawable(itemView.context, R.drawable.ic_fav) else ContextCompat.getDrawable(itemView.context, R.drawable.ic_unfav)

            textViewRestaurantName.text = item.restaurant.name
            textViewRestaurantItem.text = item.restaurant.cuisines?.joinToString(", ", transform = { it.cuisine })
            ratingBarRestaurant.count = item.restaurant.rating
            textViewReviewCount.text = itemView.context.getString(R.string.review_restaurant, item.restaurant.reviews.toString())

            setOnClickListener {
                itemClickListener.onItemClick(item)
            }
        }

    }

    interface ItemClickListener {
        fun onItemClick(orderDetails: OrderList)
        fun onClickRatingBar()
    }
}