package com.kraven.ui.order.beverage.fragment

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View

import androidx.appcompat.widget.AppCompatTextView
import com.kraven.R
import com.kraven.di.component.FragmentComponent
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.my.order.model.OrderList
import com.kraven.utils.ConstantApp

import kotlinx.android.synthetic.main.order_details_pending_fragment.*
import java.util.*

class OrderDetailsPendingFragment : BaseFragment() {

    private var orderDetails: OrderList? = null
    private var fragmentPosition: Int = -1
    var textViewStatus: AppCompatTextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            orderDetails = arguments!!.getParcelable(ConstantApp.PassValue.ORDER_DETAIL)
            fragmentPosition = arguments!!.getInt(ConstantApp.PassValue.ORDER_FRAGMENT_POSITION)
        }
    }


    override fun createLayout(): Int = R.layout.order_details_pending_fragment

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)


    override fun bindData() {
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle(getString(R.string.my_order_details))
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)

        setHasOptionsMenu(true)
        textViewOrderId.text = String.format(Locale.US,"%s %s", getString(R.string.order_id), "123456")
        buttonCancel.setOnClickListener {
            activity?.onBackPressed()
        }
        if (fragmentPosition == 2 && orderDetails?.status.equals(getString(R.string.status_canceled))) {
            buttonCancel.visibility = View.GONE
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater!!.inflate(R.menu.menu_status, menu)

        textViewStatus = menu!!.findItem(R.id.notification).actionView.findViewById(R.id.textViewDetailsStatus) as AppCompatTextView
        textViewStatus!!.text = orderDetails?.status

        super.onCreateOptionsMenu(menu, inflater)
    }


}