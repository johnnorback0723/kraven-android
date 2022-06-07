package com.kraven.ui.home.adapter


import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.bumptech.glide.Glide

import com.bumptech.glide.request.RequestOptions
import com.kraven.R

import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.PagerAdapter
import com.kraven.extensions.viewGone
import com.kraven.extensions.viewShow
import com.kraven.ui.home.model.Promocode
import com.kraven.ui.portable.bar.rental.model.PortableBar
import jp.wasabeef.glide.transformations.BlurTransformation


class ViewPagerCarAdapter(private val mContext: Context, private val promoCodeList: List<Promocode>) : PagerAdapter() {

    override fun getCount(): Int {
        return promoCodeList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as ConstraintLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = LayoutInflater.from(mContext).inflate(R.layout.pager_item, container, false)

        val textViewOfferText = itemView.findViewById<AppCompatTextView>(R.id.textViewOfferText)
        val imageView = itemView.findViewById<AppCompatImageView>(R.id.imageViewCar)
        if(promoCodeList[position].description?.isNotEmpty()!! || promoCodeList[position].promocode?.isNotEmpty()!!){
            textViewOfferText.text = if(promoCodeList[position].description?.isNotEmpty()!!)promoCodeList[position].description+ " | " + promoCodeList[position].promocode else promoCodeList[position].promocode
        }else{
            //imageView.foreground =null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                imageView.foreground = ColorDrawable(ContextCompat.getColor(mContext, android.R.color.transparent))
            }
        }

        Glide.with(mContext)
                .load(promoCodeList[position].banner)
                .apply(RequestOptions().placeholder(R.drawable.placeholder_car))
                .apply(RequestOptions().fitCenter())
                .into(imageView)
        container.addView(itemView)

        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ConstraintLayout)
    }
}
