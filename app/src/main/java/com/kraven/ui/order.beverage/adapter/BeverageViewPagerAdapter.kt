package com.kraven.ui.order.beverage.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.kraven.ui.order.beverage.fragment.BeverageMenuListFragment

class BeverageViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    companion object {
        val tabs = arrayOf("Beer", "Brandy", "Champagne", "Cognac", "Gin", "Liqueurs", "Rum", "Scotch", "Tequila", "Vodka",
                "Wine", "Whiskey",  "Vermouth", "Other")
    }

    override fun getItem(position: Int): Fragment {
        return BeverageMenuListFragment.newInstance(position)
    }

    override fun getCount(): Int {
        return tabs.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return tabs[position]
    }

}