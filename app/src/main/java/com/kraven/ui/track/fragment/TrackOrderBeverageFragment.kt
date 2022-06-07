package com.kraven.ui.track.fragment

import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.kraven.R
import com.kraven.core.StatusCode
import com.kraven.di.component.FragmentComponent
import com.kraven.extensions.clearNotification
import com.kraven.fcm.AppFirebaseNotification
import com.kraven.ui.my.order.model.BeverageHistoryDetails
import com.kraven.ui.track.viewmodel.TrackViewModel
import com.kraven.utils.ConstantApp
import com.kraven.utils.ConstantApp.Firebase.DELIVERED_BEVERAGE_ORDER_BY_DRIVER
import com.kraven.utils.ConstantApp.Firebase.PICKEDUP_BEVERAGE_ORDER_BY_DRIVER
import com.kraven.utils.ConstantApp.Firebase.STARTED_BEVERAGE_ORDER_BY_DRIVER
import com.kraven.utils.TextDecorator
import kotlinx.android.synthetic.main.track_order_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.IllegalStateException


class TrackOrderBeverageFragment : BaseTrackOrderBeverageFragment(), View.OnClickListener {

    private lateinit var viewModel: TrackViewModel

    private val orderId: String? by lazy {
        val args = arguments ?: throw IllegalStateException("Missing Arguments")
        args.getString(ConstantApp.PassValue.ORDER_ID)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProviders.of(this, viewModelFactory)[TrackViewModel::class.java]
        registerObserver()
    }

    override fun createLayout(): Int = R.layout.track_order_fragment

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    val UPDATE_INTERVAL = 10000L
    val updateWidgetHandler = Handler()
    private var updateWidgetRunnable: Runnable = Runnable {
        run {
            orderId?.let { viewModel.getOrderBeverageDetails(it) }
            updateWidgetHandler.postDelayed(updateWidgetRunnable, UPDATE_INTERVAL)
        }

    }

    override fun onPause() {
        super.onPause()
        updateWidgetHandler.removeCallbacks(updateWidgetRunnable);
    }

    override fun bindData() {
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle(getString(R.string.track_order))
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)

        textViewOrderOnTheWay.text = getString(R.string.beverage_on_the_way)
        TextDecorator.decorate(textViewOrderProcess, getString(R.string.order_process))
                .apply {
                    setAbsoluteSize(resources.getDimensionPixelSize(R.dimen._10sdp), 22, finalLength)
                }
        buttonTrack.setOnClickListener(this)
        showLoader(true)
        //orderId?.let { viewModel.getOrderBeverageDetails(it) }
        updateWidgetHandler.postDelayed(updateWidgetRunnable, 0)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val reload = menu.add(Menu.NONE, 1, Menu.NONE, "Reload")
        reload?.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_reload)
        reload?.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            1 -> {
                orderId?.let { viewModel.getOrderBeverageDetails(it) }
            }
        }
        return false
    }

    private fun registerObserver() {
        viewModel.getOrderBeverageDetails.observe(this, {
            showLoader(false)
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    setData(it.data!!)
                }
                else -> {
                    showMessage(it.message)
                }
            }
        }, { true })

    }

    private fun setData(orderDetails: BeverageHistoryDetails) {
        when {
            orderDetails.deliveryType == ConstantApp.PICKUP -> {
                getPickUpDate(orderDetails)
            }
            orderDetails.deliveryType == ConstantApp.DELIVERY -> {
                getDeliveryData(orderDetails)

            }
        }
    }


    override fun onResume() {
        super.onResume()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.buttonTrack -> {
                navigator.load(TrackGoogleMapBeverageFragment::class.java).setBundle(Bundle().apply {
                    putString(ConstantApp.PassValue.ORDER_ID, orderId)
                }).replace(true)
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(appFireBaseNotification: AppFirebaseNotification) {
        if(appFireBaseNotification.tag==STARTED_BEVERAGE_ORDER_BY_DRIVER ||appFireBaseNotification.tag==PICKEDUP_BEVERAGE_ORDER_BY_DRIVER||
                appFireBaseNotification.tag==DELIVERED_BEVERAGE_ORDER_BY_DRIVER)
            clearNotification(activity!!)
            viewModel.getOrderBeverageDetails(appFireBaseNotification.orderId.toString())
    }

}

