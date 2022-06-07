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
import com.kraven.ui.my.order.model.OrderList
import com.kraven.ui.my.order.viewModel.OrderViewModel
import com.kraven.ui.restaurant.reservation.adapter.RestaurantOrderListAdapter
import com.kraven.utils.ConstantApp
import kotlinx.android.synthetic.main.my_order_list_fragment.*


class FutureFoodOrderListFragment : BaseFragment() {

    private var restaurantOrderListAdapter: RestaurantOrderListAdapter? = null
    private lateinit var viewModel: OrderViewModel

    private lateinit var linearLayoutManager: LinearLayoutManager
    private var fragmentPosition: Int = 0
    private var pages = 1
    companion object {
        fun newInstance(position: Int): FutureFoodOrderListFragment {
            val args = Bundle()
            args.putInt(ConstantApp.PassValue.ORDER_FRAGMENT_POSITION, position)
            val fragment = FutureFoodOrderListFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun createLayout(): Int = R.layout.my_order_list_fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)[OrderViewModel::class.java]

         viewModel.getOrderHistoryFuture.observe(this, {
             showLoader(false)
             when (it.code) {
                 StatusCode.CODE_SUCCESS -> {
                     restaurantOrderListAdapter?.setItems(it.data, pages)
                 }
                 StatusCode.CODE_NO_DATA -> {
                     restaurantOrderListAdapter?.errorMessage = it.message
                 }
                 else -> showMessage(it.message)
             }
         }) { true }

    }

   /* override fun onResume() {
        super.onResume()
        pages = 1
    }*/

    override fun bindData() {
        fragmentPosition = arguments!!.getInt(ConstantApp.PassValue.ORDER_FRAGMENT_POSITION)
         setUpRecyclerView()
         viewModel.getOrderHistoryFuture(ConstantApp.FUTURE, pages.toString())
    }

    private fun setUpRecyclerView() {
        linearLayoutManager = LinearLayoutManager(activity)
        recyclerViewOrderList.layoutManager = linearLayoutManager

        restaurantOrderListAdapter = RestaurantOrderListAdapter(object : RestaurantOrderListAdapter.ItemClickListener {
            override fun onItemClick(orderDetails: OrderList) {
                val bundle = Bundle()
                bundle.putParcelable(ConstantApp.PassValue.ORDER_DETAIL, orderDetails)
                bundle.putInt(ConstantApp.PassValue.ORDER_FRAGMENT_POSITION, fragmentPosition)
                navigator.loadActivity(IsolatedActivity::class.java).setPage(MyOrderDetailsFragment::class.java).addBundle(bundle).start()
                //navigator.load(MyOrderDetailsFragment::class.java).setBundle(bundle).replace(true)
            }

            override fun onClickRatingBar() {

            }

        })
        recyclerViewOrderList.addOnScrollListener(object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                pages = page
                viewModel.getOrderHistory(ConstantApp.FUTURE, page.toString())
            }

        })
        recyclerViewOrderList.adapter = restaurantOrderListAdapter
    }
}