package com.kraven.ui.notification.fragment

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kraven.R
import com.kraven.core.StatusCode
import com.kraven.coreadapter.EndlessRecyclerViewScrollListener
import com.kraven.data.pojo.Parameters
import com.kraven.di.component.FragmentComponent
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.home.viewmodel.HomeViewModel
import com.kraven.ui.my.order.fragment.MyOrderDetailsFragment
import com.kraven.ui.my.order.fragment.OrderDetailsBeverageFragment
import com.kraven.ui.notification.adapter.NotificationAdapter
import com.kraven.ui.notification.model.Notifications
import com.kraven.ui.order.beverage.fragment.SpecialBeverageDetailsFragment
import com.kraven.utils.ConstantApp
import kotlinx.android.synthetic.main.notification_fragment.*

class NotificationFragment : BaseFragment() {

    var notificationAdapter: NotificationAdapter? = null
    private lateinit var viewModel: HomeViewModel

    private var pages = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)[HomeViewModel::class.java]
        registerObserver()
    }

    private fun registerObserver() {
        viewModel.getNotificationList.observe(this, {
            showLoader(false)
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    notificationAdapter?.setItems(it.data, pages)
                }
                StatusCode.CODE_NO_DATA -> {
                    if (pages == 1) {
                        notificationAdapter?.errorMessage = it.message
                    } else {
                        notificationAdapter?.stopLoader()
                    }
                }
                else -> showMessage(it.message)
            }
        })
    }

    override fun createLayout(): Int = R.layout.notification_fragment

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun bindData() {
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle(getString(R.string.notification))
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)

        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        val layoutManager = LinearLayoutManager(activity!!)
        recyclerViewNotificationList.layoutManager = layoutManager
        if (notificationAdapter == null) {
            notificationAdapter = NotificationAdapter(object : NotificationAdapter.ItemClickListener {
                override fun onItemClick(item: Notifications) {
                    if (item.orderType == "SpecialBeverage") {
                        val bundle = Bundle()
                        bundle.putString(ConstantApp.PassValue.ORDER_ID, item.orderId)
                        navigator.load(SpecialBeverageDetailsFragment::class.java).setBundle(bundle).replace(true)
                    }else if(item.orderType=="Beverage"){
                        val bundle = Bundle()
                        bundle.putString("orderId", item.orderId)
                        navigator.load(OrderDetailsBeverageFragment::class.java).setBundle(bundle).replace(true)
                    }
                    else {
                        val bundle = Bundle()
                        bundle.putString("orderId", item.orderId)
                        navigator.load(MyOrderDetailsFragment::class.java).setBundle(bundle).replace(true)
                    }
                }

            })
            viewModel.getNotificationList(Parameters(userType = "User", page = pages.toString()))
        }
        recyclerViewNotificationList.adapter = notificationAdapter
        recyclerViewNotificationList.addOnScrollListener(object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                pages = page
                viewModel.getNotificationList(Parameters(userType = "User", page = pages.toString()))
            }
        })

    }

}