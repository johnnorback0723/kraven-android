package com.kraven.ui.my.order.fragment

import androidx.viewpager.widget.ViewPager
import com.kraven.R
import com.kraven.di.component.FragmentComponent
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.comman.adapter.DrawerAdapter
import com.kraven.ui.home.fragment.HomeNewFragment
import com.kraven.ui.my.order.adapter.MyOrderPagerAdapter
import kotlinx.android.synthetic.main.my_order_fragment.*

class MyOrderHistoryFragment : BaseFragment() {

    private var myOrderPagerAdapter : MyOrderPagerAdapter?=null
    override fun createLayout(): Int = R.layout.my_order_fragment

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun bindData() {
        showLoader(false)
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle(getString(R.string.order_history))
        toolbar.setToolbarTextColorWhite(false)
        toolbar.setButtonTextVisibility(false)

        setUpViewPager()
    }

    private fun setUpViewPager() {
        myOrderPagerAdapter = MyOrderPagerAdapter(childFragmentManager)
        viewPagerMyOrder.adapter = myOrderPagerAdapter
        viewPagerMyOrder.offscreenPageLimit = 3
        tabLayoutMyOrder.setupWithViewPager(viewPagerMyOrder)
        viewPagerMyOrder.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                viewPagerMyOrder.adapter?.notifyDataSetChanged()
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    override fun onBackActionPerform(): Boolean {
        navigator.load(HomeNewFragment::class.java).clearHistory(null).replace(false)
        return false
    }

    /*override fun onResume() {
        super.onResume()
        if (myOrderPagerAdapter != null) {
            myOrderPagerAdapter!!.notifyDataSetChanged()
        }
    }*/
}