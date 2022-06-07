package com.kraven.ui.my.order.fragment

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kraven.R
import com.kraven.core.StatusCode
import com.kraven.coreadapter.EndlessRecyclerViewScrollListener
import com.kraven.di.component.FragmentComponent
import com.kraven.ui.activity.IsolatedActivity
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.my.order.adapter.AdapterBeverageHistoryList
import com.kraven.ui.my.order.model.BeverageHistory
import com.kraven.ui.my.order.model.OrderList
import com.kraven.ui.my.order.viewModel.OrderViewModel
import com.kraven.utils.ConstantApp
import kotlinx.android.synthetic.main.my_order_list_fragment.*

class OrderBeverageListFragment :BaseFragment() {

    private lateinit var viewModel: OrderViewModel
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var restaurantOrderListAdapter: AdapterBeverageHistoryList? = null
    private var fragmentPosition: Int? = null
    private var pages = 1

    private var orderList: OrderList? = null

    companion object {
        fun newInstance(position: Int): OrderBeverageListFragment {
            val args = Bundle()
            args.putInt(ConstantApp.PassValue.ORDER_FRAGMENT_POSITION, position)
            val fragment = OrderBeverageListFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)[OrderViewModel::class.java]
        if (arguments != null) {
            orderList = arguments!!.getParcelable(ConstantApp.PassValue.ORDER_DETAIL)
            fragmentPosition = arguments!!.getInt(ConstantApp.PassValue.ORDER_FRAGMENT_POSITION)
        }
        registerObserver()
    }

    override fun createLayout(): Int = R.layout.my_order_list_fragment

    override fun inject(fragmentComponent: FragmentComponent) =fragmentComponent.inject(this)

    override fun onResume() {
        super.onResume()
        pages = 1
        setUpRecyclerView()
        viewModel.getOrderBeverageHistory(pages.toString())
    }

    override fun bindData() {

    }

    private fun registerObserver() {
        viewModel.getOrderBeverageHistory.observe(this, {
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    restaurantOrderListAdapter?.setItems(it.data, pages)
                }
                StatusCode.CODE_NO_DATA -> {
                    if(pages==1) {
                        restaurantOrderListAdapter?.errorMessage = it.message
                    }else {
                        restaurantOrderListAdapter?.stopLoader()
                    }
                }
                else -> showMessage(it.message)
            }
        }) { true }
    }

    private fun setUpRecyclerView() {
        fragmentPosition = arguments!!.getInt(ConstantApp.PassValue.ORDER_FRAGMENT_POSITION)
        linearLayoutManager = LinearLayoutManager(activity)
        recyclerViewOrderList.layoutManager = linearLayoutManager
        restaurantOrderListAdapter = AdapterBeverageHistoryList(object : AdapterBeverageHistoryList.ItemClickListener {
            override fun onItemClick(orderDetails: BeverageHistory) {
                val bundle = Bundle()
                bundle.putParcelable(ConstantApp.PassValue.ORDER_DETAIL, orderDetails)
                bundle.putInt(ConstantApp.PassValue.ORDER_FRAGMENT_POSITION, fragmentPosition!!)
                navigator.loadActivity(IsolatedActivity::class.java).setPage(OrderDetailsBeverageFragment::class.java).addBundle(bundle).start()
                //navigator.load(OrderDetailsBeverageFragment::class.java).setBundle(bundle).replace(true)
            }

            override fun onClickRatingBar() {}
        })

        recyclerViewOrderList.addOnScrollListener(object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                pages = page
                viewModel.getOrderBeverageHistory(pages.toString())
            }

        })
        recyclerViewOrderList.adapter = restaurantOrderListAdapter
    }
}