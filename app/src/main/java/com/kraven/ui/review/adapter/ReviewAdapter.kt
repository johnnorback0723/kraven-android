package com.kraven.ui.review.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kraven.R
import com.kraven.coreadapter.AdvanceRecycleViewAdapter
import com.kraven.coreadapter.BaseHolder
import com.kraven.ui.review.model.Review
import com.kraven.utils.Formatter
import kotlinx.android.synthetic.main.review_raw.view.*

class ReviewAdapter : AdvanceRecycleViewAdapter<ReviewAdapter.AdapterReviewHolder, Review>() {

    override fun createDataHolder(parent: ViewGroup?, viewType: Int): AdapterReviewHolder? =
            AdapterReviewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.review_raw, parent, false))

    override fun onBindDataHolder(holder: AdapterReviewHolder?, position: Int, item: Review?) {

        Glide.with(holder?.itemView?.context!!)
                .load(item?.profile_image)
               /* .apply(RequestOptions().placeholder(R.drawable.ic_user))*/
                .apply(RequestOptions.circleCropTransform())
                .into(holder.imageViewUser)

        holder.textViewUserName.text = item?.name
        holder.textViewDate.text = Formatter.format(item?.insertdate!!,Formatter.YYYY_MM_DD_HH_MM_SS_NEW,Formatter.DD_MMM_YYYY)
        holder.ratingBarRestaurant.count = item.rate!!.toInt()
        holder.textViewComments.text = item.comment

    }


    class AdapterReviewHolder(view: View) : BaseHolder<Review>(view) {
        var imageViewUser = view.imageViewUser!!
        var textViewUserName = view.textViewUserName!!
        var textViewDate = view.textViewDate!!
        var ratingBarRestaurant = view.ratingBarRestaurant!!
        var textViewComments = view.textViewComments!!

    }
}