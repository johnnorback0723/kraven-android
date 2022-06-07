package com.kraven.ui.home.adapter


import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kraven.R
import com.kraven.ui.home.model.BannersItem


class ViewPagerRestaurantAdapter(private val mContext: Context, private val mResources: List<BannersItem>?, val status: String) : PagerAdapter() {

    override fun getCount(): Int {
        return mResources?.size!!
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as AppCompatImageView
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {


        val itemView = LayoutInflater.from(mContext).inflate(R.layout.restaurant_silder_raw, container, false)

        val imageViewRestaurant = itemView.findViewById<AppCompatImageView>(R.id.imageViewRestaurant)

        Glide.with(mContext)
                .load(mResources?.get(position)?.banner)
                .apply(RequestOptions().placeholder(R.drawable.placeholder_car))
                .into(imageViewRestaurant)

        container.addView(itemView)

        if (status == "Closed" || status == itemView.context.getString(R.string.currentlyUnavailable)) {
            val colorMatrix = ColorMatrix()
            colorMatrix.setSaturation(0f)
            val filter = ColorMatrixColorFilter(colorMatrix)
            imageViewRestaurant.colorFilter = filter
        }

        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as AppCompatImageView)
    }
}
