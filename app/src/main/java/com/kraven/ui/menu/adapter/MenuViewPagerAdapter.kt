package com.kraven.ui.menu.adapter


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.PagerAdapter
import com.kraven.ui.home.model.MenusItem
import com.kraven.ui.home.model.RestaurantDetails
import com.kraven.ui.menu.fragment.MenuListFragment
import com.kraven.utils.WrapViewPager.WrappingFragmentStatePagerAdapter


class MenuViewPagerAdapter(fm: FragmentManager, var menus: ArrayList<MenusItem>?, var restaurantId: String,
                           var status: String, var restaurantDetails: RestaurantDetails?, var orderPage: String) :
        WrappingFragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return MenuListFragment.newInstance(position, menus, restaurantId, status, restaurantDetails, orderPage)
    }

    override fun getCount(): Int {
        return menus?.size?:0
    }


    override fun getPageTitle(position: Int): CharSequence {
        return menus?.get(position)!!.name
    }


    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

}