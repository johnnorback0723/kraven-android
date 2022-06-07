package com.kraven.ui.order.beverage.fragment

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.kraven.R
import com.kraven.di.component.FragmentComponent
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.cart.fragment.BaseFragmentVisibility
import com.kraven.ui.my.order.model.OrderList
import com.kraven.ui.payment.fragment.PaymentFragment

import com.kraven.utils.ConstantApp
import kotlinx.android.synthetic.main.common_cart_layout.*
import kotlinx.android.synthetic.main.order_details_confirmed_fragment.*
import java.util.*


class OrderDetailsConfirmedFragment : BaseFragmentVisibility(), View.OnClickListener {

    private var orderDetails: OrderList? = null
    private var fragmentPosition: Int = 0
    var textViewStatus: AppCompatTextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            orderDetails = arguments!!.getParcelable(ConstantApp.PassValue.ORDER_DETAIL)
            fragmentPosition = arguments!!.getInt(ConstantApp.PassValue.ORDER_FRAGMENT_POSITION)
        }
    }


    override fun createLayout(): Int = R.layout.order_details_confirmed_fragment

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun bindData() {
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle(getString(R.string.my_order))
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)

        setHasOptionsMenu(true)
        textViewOrderId.text = String.format(Locale.US,"%s %s", getString(R.string.order_id), "123456")
        loadLayout()
        if (orderDetails!!.status.equals(getString(R.string.status_confirmed))) {
            buttonCancel.visibility = View.VISIBLE
            buttonPay.visibility = View.VISIBLE
            includeCode.visibility = View.VISIBLE
            paymentCommonLayout.visibility = View.GONE
        } else if (orderDetails!!.status.equals(getString(R.string.status_finished))) {
            buttonCancel.visibility = View.GONE
            buttonPay.visibility = View.GONE
            includeCode.visibility = View.GONE
            paymentCommonLayout.visibility = View.VISIBLE
        }

        buttonPay.setOnClickListener(this)
        buttonCancel.setOnClickListener(this)
        imageViewDeliveryInfo.setOnClickListener(this)
        imageViewCashInfo.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        //super.onClick(v)
        when (v!!.id) {
            R.id.buttonPay -> {
                val bundle = Bundle()
                bundle.putInt(ConstantApp.PassValue.PAYMENT_POSITION, 4)
                navigator.load(PaymentFragment::class.java).setBundle(bundle).replace(true)
            }
            R.id.buttonCancel -> {
                activity!!.onBackPressed()
            }
            R.id.imageViewDeliveryInfo -> {
                session.userSettings?.bevDeliveryChargeBtnInfo?.let {
                    commandDialog(getString(R.string.delivery_charge_info), it, object : BaseFragment.DialogInterface {
                        override fun onClickOk() {

                        }
                    })
                }
            }
            R.id.imageViewCashInfo -> {
                commandDialog(getString(R.string.cash_on_delivery_info), getString(R.string.cash_delivery_text), object : BaseFragment.DialogInterface {
                    override fun onClickOk() {

                    }
                })
            }
            R.id.buttonCheckout -> {
                if (radioButtonDebitCard.isChecked ) {
                    val bundle = Bundle()
                    bundle.putInt(ConstantApp.PassValue.PAYMENT_POSITION, 4)
                    navigator.load(PaymentFragment::class.java).setBundle(bundle).replace(true)
                } else {
                    showMessage(getString(R.string.please_select_payment_method))
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater!!.inflate(R.menu.menu_status, menu)

        textViewStatus = menu!!.findItem(R.id.notification).actionView.findViewById(R.id.textViewDetailsStatus) as AppCompatTextView
        if (orderDetails!!.status.equals(getString(R.string.status_confirmed))) {
            textViewStatus!!.text = getString(R.string.status_confirmed)
        } else if (orderDetails!!.status.equals(getString(R.string.status_finished))) {
            textViewStatus!!.text = getString(R.string.status_finished)
        }

        super.onCreateOptionsMenu(menu, inflater)
    }
}