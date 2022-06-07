package com.kraven.ui.favorite.adapter

import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.kraven.R
import com.kraven.coreadapter.AdvanceRecycleViewAdapter
import com.kraven.coreadapter.BaseHolder
import com.kraven.ui.home.model.Restaurants
import kotlinx.android.synthetic.main.favorite_restaurant_raw.view.*


class FavoriteRestaurantAdapter(private var itemClickListener: ItemClickListener) :
        AdvanceRecycleViewAdapter<FavoriteRestaurantAdapter.FavoriteRestaurantAdapterViewHolder, Restaurants>() {

    override fun createDataHolder(parent: ViewGroup?, viewType: Int): FavoriteRestaurantAdapterViewHolder {
        return FavoriteRestaurantAdapterViewHolder(makeView(parent, R.layout.favorite_restaurant_raw))
    }

    override fun onBindDataHolder(holder: FavoriteRestaurantAdapterViewHolder?, position: Int, item: Restaurants?) {
        holder?.textViewRestaurantName?.text = item?.name
        Glide.with(holder?.itemView?.context!!)
                .load(item?.banner)
               /* .apply(RequestOptions().placeholder(R.drawable.ic_user))*/
                .apply(RequestOptions.bitmapTransform(MultiTransformation(CenterCrop(), RoundedCorners(15))))
                .into(holder.imageViewRestaurant)
        holder.textViewRestaurantItem.text = item?.cuisines?.joinToString(", ", transform = { it.cuisine })
        holder. ratingBarRestaurant.count = item?.rating!!
        holder.textViewReviewCount.text = holder.itemView.context.getString(R.string.review_restaurant, item.reviews.toString())

        holder.imageViewFav.isSelected = item.isFavourite == 1

        holder.ratingBarRestaurant.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                itemClickListener.onClickRatingBar()
            }
            return@setOnTouchListener true
        }

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(position, item)
        }
        holder.textViewReviewCount.setOnClickListener {
            itemClickListener.onClickRatingBar()
        }
    }


    interface ItemClickListener {
        fun onItemClick(position: Int, item: Restaurants)
        fun onClickRatingBar()
    }


    class FavoriteRestaurantAdapterViewHolder(itemView: View) : BaseHolder<Restaurants>(itemView) {
        var textViewRestaurantName = itemView.textViewRestaurantName!!
        var textViewRestaurantItem = itemView.textViewRestaurantItem!!
        var ratingBarRestaurant = itemView.ratingBarRestaurant!!
        var textViewReviewCount = itemView.textViewReviewCount!!
        var imageViewRestaurant = itemView.imageViewRestaurant!!
        var imageViewFav = itemView.imageViewFav!!
    }
}