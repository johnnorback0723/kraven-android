package com.kraven.ui.my.order.fragment

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kraven.R
import com.kraven.core.StatusCode
import com.kraven.coreadapter.EndlessRecyclerViewScrollListener
import com.kraven.di.component.FragmentComponent
import com.kraven.ui.activity.IsolatedActivity
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.my.order.model.OrderList
import com.kraven.ui.my.order.viewModel.OrderViewModel
import com.kraven.ui.restaurant.reservation.adapter.RestaurantOrderListAdapter
import com.kraven.utils.ConstantApp
import com.kraven.utils.ConstantApp.PassValue.Companion.ORDER_FRAGMENT_POSITION
import kotlinx.android.synthetic.main.my_order_list_fragment.*

class OrderFoodListFragment : BaseFragment() {

    private lateinit var viewModel: OrderViewModel
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var restaurantOrderListAdapter: RestaurantOrderListAdapter? = null
    private var fragmentPosition: Int? = null
    private var pages = 1

    override fun createLayout(): Int = R.layout.my_order_list_fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)[OrderViewModel::class.java]
        registerObserver()
    }

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    companion object {
        fun newInstance(position: Int): OrderFoodListFragment {
            val fragment = OrderFoodListFragment()
            fragment.arguments = bundleOf(ORDER_FRAGMENT_POSITION to position)
            return fragment
        }
    }

    override fun onResume() {
        super.onResume()
        pages = 1
        setUpRecyclerView()
        viewModel.getOrderHistory(ConstantApp.NOW, pages.toString())
    }

    override fun bindData() {}

    private fun registerObserver() {
        viewModel.getOrderHistory.observe(this, {
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    restaurantOrderListAdapter?.setItems(it.data, pages)
                }
                StatusCode.CODE_NO_DATA -> {
                    if (pages == 1) {
                        restaurantOrderListAdapter?.errorMessage = it.message
                    } else {
                        restaurantOrderListAdapter?.stopLoader()
                    }
                }
                else -> showMessage(it.message)
            }
        }) { true }
    }

    private fun setUpRecyclerView() {
        fragmentPosition = arguments!!.getInt(ORDER_FRAGMENT_POSITION)
        linearLayoutManager = LinearLayoutManager(activity)
        recyclerViewOrderList.layoutManager = linearLayoutManager

        restaurantOrderListAdapter = RestaurantOrderListAdapter(object : RestaurantOrderListAdapter.ItemClickListener {
            override fun onItemClick(orderDetails: OrderList) {
                val bundle = Bundle()
                bundle.putParcelable(ConstantApp.PassValue.ORDER_DETAIL, orderDetails)
                bundle.putInt(ORDER_FRAGMENT_POSITION, fragmentPosition!!)
                navigator.loadActivity(IsolatedActivity::class.java).setPage(MyOrderDetailsFragment::class.java).addBundle(bundle).start()
                //navigator.load(MyOrderDetailsFragment::class.java).setBundle(bundle).replace(true)
            }

            override fun onClickRatingBar() {}
        })
        recyclerViewOrderList.addOnScrollListener(object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                pages = page
                viewModel.getOrderHistory(ConstantApp.NOW, page.toString())
            }

        })
        recyclerViewOrderList.adapter = restaurantOrderListAdapter
    }
}