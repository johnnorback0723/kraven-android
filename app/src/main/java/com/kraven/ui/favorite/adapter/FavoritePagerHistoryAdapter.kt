package com.kraven.ui.favorite.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.kraven.ui.favorite.fragment.FavoriteBeverageFragment
import com.kraven.ui.favorite.fragment.FavoriteRestaurantFragment

class FavoritePagerHistoryAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    private val tabs = arrayOf("Restaurant", "Beverage")

    override fun getItem(position: Int): Fragment {
        return if(position==0){
            FavoriteRestaurantFragment()
        }else{
            FavoriteBeverageFragment()
        }
    }

    override fun getCount(): Int {
        return tabs.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return tabs[position]
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }
}