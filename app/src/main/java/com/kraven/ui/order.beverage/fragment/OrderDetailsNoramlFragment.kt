package com.kraven.ui.order.beverage.fragment

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.kraven.R
import com.kraven.di.component.FragmentComponent
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.home.viewmodel.HomeViewModel
import com.kraven.ui.my.order.model.OrderList
import com.kraven.ui.order.beverage.adapter.BeverageOrderDetailsAdapter
import com.kraven.ui.track.fragment.TrackOrderFragment

import com.kraven.utils.ConstantApp
import kotlinx.android.synthetic.main.order_details_beverage_fragment.*


class OrderDetailsNoramlFragment : BaseFragment(), View.OnClickListener {

    private var beverageOrderDetailsAdapter: BeverageOrderDetailsAdapter? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var viewModel: HomeViewModel
    private var orderDetails: OrderList? = null
    private var fragmentPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            orderDetails = arguments!!.getParcelable(ConstantApp.PassValue.ORDER_DETAIL)
            fragmentPosition = arguments!!.getInt(ConstantApp.PassValue.ORDER_FRAGMENT_POSITION)
        }
    }


    override fun createLayout(): Int = R.layout.order_details_beverage_fragment

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun bindData() {
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle(getString(R.string.my_order_details))
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)

        setHasOptionsMenu(true)


//        textViewOrderId.text = String.format("%s %s", getString(R.string.order_id), orderDetails!!.orderId)
        //      textViewOrderDateTime.text = String.format("%s %s", getString(R.string.date_time), orderDetails!!.dateTime)
        imageViewDeliveryInfo.setOnClickListener(this)
        buttonTrack.setOnClickListener(this)
        buttonCancel.setOnClickListener(this)

        setUpRecyclerView()

        viewModel = ViewModelProviders.of(this, viewModelFactory)[HomeViewModel::class.java]
       // viewModel.getBeverageOrderDetailsList()

        viewModel.getBeverageOrderDetailsList.observe(this,
                { responseBody ->
                    beverageOrderDetailsAdapter?.items = responseBody.data
                }) { true }


    }

    private fun setUpRecyclerView() {
        linearLayoutManager = LinearLayoutManager(activity)
        recyclerViewOrderList.layoutManager = linearLayoutManager
        beverageOrderDetailsAdapter = BeverageOrderDetailsAdapter(object : BeverageOrderDetailsAdapter.ItemClickListenerChild {
            override fun OnItemChildClick() {

            }

        })
        recyclerViewOrderList.adapter = beverageOrderDetailsAdapter
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.imageViewDeliveryInfo -> {
                session.userSettings?.bevDeliveryChargeBtnInfo?.let {
                    commandDialog(getString(R.string.delivery_charge_info), it, object : BaseFragment.DialogInterface {
                        override fun onClickOk() {

                        }
                    })
                }
            }
            R.id.buttonTrack -> {
                navigator.load(TrackOrderFragment::class.java).replace(true)
            }
            R.id.buttonCancel -> {
                activity!!.onBackPressed()
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater!!.inflate(R.menu.menu_status, menu)
        val textViewDone = menu!!.findItem(R.id.notification).actionView.findViewById(R.id.linearLayoutStatus) as LinearLayout
        val textViewStatus = menu.findItem(R.id.notification).actionView.findViewById(R.id.textViewDetailsStatus) as AppCompatTextView
        textViewStatus.text = orderDetails?.status
        super.onCreateOptionsMenu(menu, inflater)
    }
}