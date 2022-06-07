package com.kraven.ui.home.fragment


import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread
import com.kraven.R
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.home.adapter.ViewPagerCarAdapter
import com.kraven.ui.home.model.Promocode
import kotlinx.android.synthetic.main.home_fragment.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.fixedRateTimer


abstract class HomeSliderFragment : BaseFragment(), ViewPager.OnPageChangeListener {

    private var dots: ArrayList<ImageView>? = null
   // var dotsCount: Int = 0

   // private val ivArrayDotsPager: Array<ImageView>? = null
    lateinit var viewPagerCarAdapter: ViewPagerCarAdapter
    var page: Int = 0
    var timer = Timer()
    private val promoCodeListTop = ArrayList<Promocode>()


    fun setUpViewPager(promoCodeList: List<Promocode>?) {
        promoCodeListTop.clear()
        promoCodeList?.let { promoCodeListTop.addAll(it) }
        viewPagerCarAdapter = ViewPagerCarAdapter(activity!!, promoCodeListTop)
        viewPagerFoodImages.adapter = viewPagerCarAdapter
        viewPagerFoodImages.currentItem = 0
        //viewPagerFoodImages.addOnPageChangeListener(this)
        viewPagerIndicator.setViewPager(viewPagerFoodImages)
        //setUiPageViewController()


        timer = fixedRateTimer("", false, 5000, 5000) {
            runOnUiThread {
                if (viewPagerCarAdapter.count - 1 == page) {
                    page = 0
                } else {
                    page++
                }
                if(viewPagerFoodImages!=null) {
                    viewPagerFoodImages.setCurrentItem(page, true)
                }
            }
        }
    }

    private fun setUiPageViewController() {

        dots = ArrayList()
        dots?.clear()
       // dots = Array(promoCodeListTop.size)
        //dotsCount = viewPagerCarAdapter.count
        //dots = arrayOfNulls(promoCodeListTop.size)
        for (i in 0 until promoCodeListTop.size) {

            val dot = ImageView(context)
            dot.setImageDrawable(ContextCompat.getDrawable(activity!!, R.drawable.nonselecteditem_dot))
            val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(10, 0, 10, 0)
            //linearLayoutDotsCount.addView(dot, params)
            dots?.add(dot)
           /* dots!![i] = ImageView(activity)


            val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAPs_CONTENT
            )

            params.setMargins(10, 0, 10, 0)

            linearLayoutDotsCount.addView(dots!![i], params)*/
        }


        //dots?.get(0)?.setImageDrawable(ContextCompat.getDrawable(activity!!, R.drawable.selecteditem_dot))
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        page = position
        for (i in 0 until promoCodeListTop.size) {
            dots?.get(i)?.setImageDrawable(resources.getDrawable(R.drawable.nonselecteditem_dot))
        }
        dots?.get(position)?.setImageDrawable(resources.getDrawable(R.drawable.selecteditem_dot))
    }


}