package com.kraven.ui.favorite.fragment

import androidx.viewpager.widget.ViewPager
import com.kraven.R
import com.kraven.di.component.FragmentComponent
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.favorite.adapter.FavoritePagerHistoryAdapter
import com.kraven.ui.home.fragment.HomeNewFragment
import kotlinx.android.synthetic.main.favorite_fragment.*

class FavoriteFragment :BaseFragment() {
    private var myOrderPagerAdapter : FavoritePagerHistoryAdapter?=null
    override fun createLayout(): Int = R.layout.favorite_fragment

    override fun inject(fragmentComponent: FragmentComponent) =fragmentComponent.inject(this)

    override fun bindData() {
        setUpViewPager()
    }

    private fun setUpViewPager() {

        toolbar.showToolbar(true)
        toolbar.setToolbarTitle("Favorites")
        toolbar.setToolbarButton(false)
        toolbar.setButtonTextVisibility(false)

        myOrderPagerAdapter = FavoritePagerHistoryAdapter(childFragmentManager)
        viewPagerMyOrder.adapter = myOrderPagerAdapter
        tabLayoutMyOrder.setupWithViewPager(viewPagerMyOrder)
        viewPagerMyOrder.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                viewPagerMyOrder.adapter?.notifyDataSetChanged()
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }

    override fun onBackActionPerform(): Boolean {
        navigator.load(HomeNewFragment::class.java).clearHistory(null).replace(false)
        return false
    }

    override fun onResume() {
        super.onResume()
        if (!(myOrderPagerAdapter == null)) {
            myOrderPagerAdapter!!.notifyDataSetChanged()
        }
    }
}