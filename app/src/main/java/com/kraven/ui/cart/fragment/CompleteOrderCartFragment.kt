package com.kraven.ui.cart.fragment

import android.view.KeyEvent
import android.view.View
import com.kraven.R
import com.kraven.application.KravenCustomer
import com.kraven.di.component.FragmentComponent
import com.kraven.extensions.transactionId
import com.kraven.ui.activity.HomeActivity
import com.kraven.ui.base.BaseFragment
import kotlinx.android.synthetic.main.complete_order_cart_fragment.*


class CompleteOrderCartFragment : BaseFragment() {

    override fun createLayout(): Int = R.layout.complete_order_cart_fragment

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)
    private var isWallet = false

    private val name: String? by lazy {
        val args = arguments ?: throw IllegalStateException("Missing arguments")
        args.getString("name")
    }

    override fun bindData() {
        if (arguments != null) {
            if (arguments!!.getBoolean("isWallet")) {
                isWallet = arguments!!.getBoolean("isWallet")
            }
        }
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle("")
        toolbar.setButtonVisibility(false)
        toolbar.setButtonTextVisibility(false)

        textViewOk.setOnClickListener {
            session.saveTempParameters = null
            session.saveOrderPage = ""
            session.saveRestaurantName = ""
            //navigator.finish()
            KravenCustomer.isWaitingPlaceOrder = true
            navigator.loadActivity(HomeActivity::class.java).byFinishingAll().start()
            //navigator.load(MyOrderHistoryFragment::class.java).replace(true)
            //navigator.loadActivity(HomeActivity::class.java).setPage(MyOrderHistoryFragment::class.java).byFinishingAll().start()
        }
        textViewOrderPlaced.text = "Your payment is processing.\nPlease wait.\nKRAVEN will update you momentarily."
        //textViewOrderPlaced.text = "Thank you for your order! \nYour order has been sent to " + "\"" + name.toUpperCase() + "\"" + " to confirm."
//        if (isWallet) {
//            textViewOrderPlaced.text = getString(R.string.thank_you_for_order, name.toUpperCase(Locale.US))
//        } else {
//            textViewOrderPlaced.text = "Your payment is processing. Please wait. KRAVEN will update you momentarily."
//        }

        view?.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                    session.saveTempParameters = null
                    session.saveOrderPage = ""
                    session.saveRestaurantName = ""
                    navigator.loadActivity(HomeActivity::class.java).byFinishingAll().start()
                    return true
                }
                return false
            }
        })
    }

    override fun onPause() {
        super.onPause()
        //Log.d("Hlink", "onDestroyView")
        session.transactionIdCard = session.user?.id?.let { transactionId(it) }
        //Log.d("Hlink", "")
        session.saveTempParameters = null
        session.saveOrderPage = ""
        session.saveRestaurantName = ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //Log.d("Hlink", "onDestroyView")
        session.saveTempParameters = null
        session.saveOrderPage = ""
        session.saveRestaurantName = ""
    }

    override fun onDestroy() {
        super.onDestroy()
        //Log.d("Hlink", "onDestroy")
        session.saveTempParameters = null
        session.saveOrderPage = ""
        session.saveRestaurantName = ""
    }

}