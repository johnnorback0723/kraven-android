package com.kraven.ui.home.fragment

import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread
import com.kraven.R
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.home.adapter.ViewPagerRestaurantAdapter
import com.kraven.ui.home.model.BannersItem
import kotlinx.android.synthetic.main.restaurant_details.*
import kotlinx.android.synthetic.main.restaurant_toolbar_layout.*
import kotlinx.android.synthetic.main.restaurant_toolbar_layout.viewPagerRestaurantImages
import java.util.*
import kotlin.concurrent.fixedRateTimer
import com.google.android.material.tabs.TabLayout



abstract class RestaurantSliderFragment : BaseFragment(), ViewPager.OnPageChangeListener {

    var dots = arrayOf<ImageView?>()
    var dotsCount: Int = 0
    lateinit var viewPagerRestaurantAdapter: ViewPagerRestaurantAdapter
    var timer = Timer()
    var page: Int = 0
    fun setUpViewPager(joinToString: List<BannersItem>?, status: String) {
        viewPagerRestaurantAdapter = ViewPagerRestaurantAdapter(activity!!, joinToString,status)
        viewPagerRestaurantImages.adapter = viewPagerRestaurantAdapter
        viewPagerRestaurantImages.offscreenPageLimit = 0
       // viewPagerRestaurantImages.addOnPageChangeListener(this)
        viewPagerIndicator.setViewPager(viewPagerRestaurantImages)


        timer = fixedRateTimer("", false, 3000, 3000) {
            runOnUiThread {
                if (viewPagerRestaurantAdapter.count - 1 == page) {
                    page = 0
                } else {
                    page++
                }
                if(viewPagerRestaurantImages!=null) {
                    viewPagerRestaurantImages.setCurrentItem(page, true)
                }
            }
        }

       // setUiPageViewController()
    }

    private fun setUiPageViewController() {

        dotsCount = viewPagerRestaurantAdapter.count
        dots = arrayOfNulls(dotsCount)

        for (i in 0 until dotsCount) {
            dots[i] = ImageView(activity)
            dots[i]?.setImageDrawable(context?.let { ContextCompat.getDrawable(it,R.drawable.nonselecteditem_dot) })

            val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            )

            params.setMargins(20, 5, 20, 5)

            linearLayoutDotsCount.addView(dots[i], params)
        }

        dots[0]?.setImageDrawable(context?.let { ContextCompat.getDrawable(it,R.drawable.selecteditem_dot) })
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        page = position
        Log.d("Hlink","onPageScrolled page ${page}")
    }

    override fun onPageSelected(position: Int) {
        page = position
        Log.d("Hlink","onPageSelected page ${page}")
        for (i in 0 until dotsCount) {
            dots[i]?.setImageDrawable(context?.let { ContextCompat.getDrawable(it,R.drawable.nonselecteditem_dot) })
        }
        dots[position]?.setImageDrawable(context?.let { ContextCompat.getDrawable(it,R.drawable.selecteditem_dot) })
    }


}
