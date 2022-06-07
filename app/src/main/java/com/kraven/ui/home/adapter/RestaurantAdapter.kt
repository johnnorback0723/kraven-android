package com.kraven.ui.home.adapter

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.kraven.R
import com.kraven.coreadapter.AdvanceRecycleViewAdapter
import com.kraven.coreadapter.BaseHolder
import com.kraven.extensions.getText
import com.kraven.extensions.viewGone
import com.kraven.extensions.viewShow
import com.kraven.ui.home.model.Restaurants
import com.kraven.utils.GlideApp
import com.kraven.utils.TextDecorator
import kotlinx.android.synthetic.main.restaurant_raw.view.*
import kotlin.math.roundToInt


class RestaurantAdapter(var isSubBranch:Boolean,var itemClickListener: ItemClickListener) :
        AdvanceRecycleViewAdapter<RestaurantAdapter.AdapterRestaurantViewHolder, Restaurants>() {

    override fun createDataHolder(parent: ViewGroup?, viewType: Int): AdapterRestaurantViewHolder {
        return AdapterRestaurantViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.restaurant_raw, parent, false))
    }

    override fun onBindDataHolder(holder: AdapterRestaurantViewHolder?, position: Int, item: Restaurants) {
        holder?.bindView(item)
        holder?.itemView?.textViewReviewCount?.setOnClickListener {
            itemClickListener.onItemClick(item)
        }
        holder?.itemView?.ratingBarRestaurant?.setOnClickListener {
            itemClickListener.onItemClick(item)
        }
    }

    inner class AdapterRestaurantViewHolder(view: View) : BaseHolder<Restaurants>(view) {
        fun bindView(item: Restaurants) = with(itemView) {

            GlideApp.with(imageViewRestaurant)
                    .load(item?.banner)
                    .apply(RequestOptions.bitmapTransform(MultiTransformation(CenterCrop(), RoundedCorners(15))))
                    .into(imageViewRestaurant)
            textViewRestaurantName.text = item?.name
            if (item?.cuisines != null && item.cuisines.isNotEmpty()) {
                textViewRestaurantItem.viewShow()
                textViewRestaurantItem.text = item.cuisines.joinToString(", ", transform = { it.cuisine })
            } else {
                textViewRestaurantItem.viewGone()
            }

            if (item.mainBranchId != "0") {
                ratingBarRestaurant.viewGone()
                textViewReviewCount.viewGone()
            } else {
                ratingBarRestaurant.viewShow()
                textViewReviewCount.viewShow()
                ratingBarRestaurant.count = item.rating!!
                textViewReviewCount.text = itemView.context.getString(R.string.review_restaurant, item.reviews.toString())
            }

            textViewDetailsStatus.text = item.availabilityStatus

            if (getText(textViewDetailsStatus) == "Available") {
                textViewDetailsStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.green))
            } else {
                textViewDetailsStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.red))
            }

            if (getText(textViewDetailsStatus) == "Closed" || getText(textViewDetailsStatus) == "Currently Unavailable") {
                val colorMatrix = ColorMatrix()
                colorMatrix.setSaturation(0f)
                val filter = ColorMatrixColorFilter(colorMatrix)
                imageViewRestaurant.colorFilter = filter
            } else {
                imageViewRestaurant.colorFilter = null
            }
            //textViewMiles.text = itemView.context.getString(R.string.mile_restaurant, ("%.2f".format(item.distance)))
            textViewMiles.text = itemView.context.getString(R.string.mile_restaurant, String.format("%.2f", item.distance))

            when (item.deliveryType) {
                "Pickup,Delivery" -> textViewDeliveryType.text = "Delivery & Pickup"
                "Pickup" -> textViewDeliveryType.text = "Pickup Only"
                "Delivery" -> textViewDeliveryType.text = "Delivery Only"
            }

            imageViewFav.setOnClickListener {
                itemClickListener.onClickFavourite(item)
                imageViewFav.isSelected = !imageViewFav.isSelected
                item.isFavourite = if (imageViewFav.isSelected) 1 else 0
                notifyDataSetChanged()
            }
            imageViewFav.isSelected = item.isFavourite == 1
            setOnClickListener {
                itemClickListener.onItemClick(item)
            }

            if (item.averagePrepTime != null) {
                TextDecorator.decorate(textViewAvrPrpTime, "Average Preparation Time : ".plus(item.averagePrepTime.toDouble().roundToInt().toString()).plus(" mins")).apply {
                    setTextColor(R.color.dark_gray, "Average Preparation Time : ")
                    setTextColor(R.color.black, item.averagePrepTime.toDouble().roundToInt().toString().plus(" mins"))
                    setTypeface(R.font.work_sans_regular, "Average Preparation Time : ")
                    setTypeface(R.font.work_sans_medium, item.averagePrepTime.toDouble().roundToInt().toString().plus(" mins"))
                }.build()
            }

        }
    }


    interface ItemClickListener {
        fun onItemClick(item: Restaurants)
        fun onClickRatingBar()
        fun onClickFavourite(item: Restaurants)
    }

}


