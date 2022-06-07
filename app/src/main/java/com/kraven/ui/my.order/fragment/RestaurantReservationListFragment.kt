package com.kraven.ui.my.order.fragment

import android.os.Bundle
import com.kraven.R
import com.kraven.di.component.FragmentComponent
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.my.order.adapter.OrderListAdapter
import com.kraven.utils.ConstantApp

class RestaurantReservationListFragment : BaseFragment() {

    private var ordrListAdapter: OrderListAdapter? = null
private var fragmentPosition:Int?=null

    companion object {
        fun newInstance(position: Int): RestaurantReservationListFragment {
            val args = Bundle()
            args.putInt(ConstantApp.PassValue.ORDER_FRAGMENT_POSITION, position)
            val fragment = RestaurantReservationListFragment()
            fragment.arguments = args
            return fragment
        }
    }


    override fun createLayout(): Int = R.layout.my_order_list_fragment

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun bindData() {
        fragmentPosition = arguments!!.getInt(ConstantApp.PassValue.ORDER_FRAGMENT_POSITION)
    }
}