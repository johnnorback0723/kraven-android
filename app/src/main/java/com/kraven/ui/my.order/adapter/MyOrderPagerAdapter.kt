package com.kraven.ui.my.order.adapter

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.kraven.ui.my.order.fragment.*
import com.kraven.ui.order.beverage.fragment.SpecialBeverageListFragment
import timber.log.Timber

class MyOrderPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    //private val tabs = arrayOf("Order Food", "Future Food Order", "Order Beverage", "Special Beverage")
    private val tabs = arrayOf("Order Food", "Order Beverage", "Special Beverage")

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> OrderFoodListFragment.newInstance(position)
            1 -> OrderBeverageListFragment.newInstance(position)/*FutureFoodOrderListFragment.newInstance(position)*/
            2 -> SpecialBeverageListFragment.newInstance()
            else -> OrderFoodListFragment.newInstance(position)
        }
    }

    override fun getCount(): Int {
        return tabs.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return tabs[position]
    }

    /*override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }*/
}