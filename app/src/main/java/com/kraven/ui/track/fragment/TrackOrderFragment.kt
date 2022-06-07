package com.kraven.ui.track.fragment


import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.kraven.R
import com.kraven.core.StatusCode
import com.kraven.data.pojo.Parameters
import com.kraven.di.component.FragmentComponent
import com.kraven.extensions.clearNotification
import com.kraven.fcm.AppFirebaseNotification
import com.kraven.ui.track.model.OrderDetail
import com.kraven.ui.track.viewmodel.TrackViewModel
import com.kraven.utils.ConstantApp
import com.kraven.utils.TextDecorator
import kotlinx.android.synthetic.main.track_order_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class TrackOrderFragment : BaseTrackOrderFragment(), View.OnClickListener {

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val reload = menu.add(Menu.NONE, 1, Menu.NONE, "Reload")
        reload?.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_reload)
        reload?.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            1 -> {
                orderId?.let { viewModel.getOrderDetails(it) }
            }
        }
        return false
    }

    override fun createLayout(): Int = R.layout.track_order_fragment

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    val UPDATE_INTERVAL = 10000L
    val updateWidgetHandler = Handler()
    private var updateWidgetRunnable: Runnable = Runnable {
        run {
            orderId?.let { viewModel.getOrderDetails(it) }
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

        TextDecorator.decorate(textViewOrderProcess, getString(R.string.order_process))
                .apply {
                    setAbsoluteSize(resources.getDimensionPixelSize(R.dimen._10sdp), 22, finalLength)

                }
        buttonTrack.setOnClickListener(this)

        showLoader(true)
        //orderId?.let { viewModel.getOrderDetails(it) }
        updateWidgetHandler.postDelayed(updateWidgetRunnable, 0)
    }


    private fun registerObserver() {
        viewModel.getOrderDetails.observe(this, {
            showLoader(false)
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    setData(it.data!!)
                }
                else -> {
                    showMessage(it.message)
                }
            }
        })
        viewModel.getDriverLatLngLiveData.observe(this, {
            showLoader(false)
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    Log.d("Hlink", "it.data = ${it.data}")
                }
                else -> showMessage(it.message)
            }
        })
    }

    private fun setData(orderDetails: OrderDetail) {
        when {
            orderDetails.deliveryType == ConstantApp.PICKUP -> {
                getPickUpDate(orderDetails)
            }
            orderDetails.deliveryType == ConstantApp.DELIVERY -> {
                /*Log.d("Hlink","orderDetails.driverDetails.latitude = ${orderDetails.driverDetails.latitude}")
                Log.d("Hlink","orderDetails.driverDetails.longitude = ${orderDetails.driverDetails.longitude}")
                if (orderDetails.orderDriver?.status == ConstantApp.DriverStatus.PICKEDUP) {
                    showLoader(true)
                    viewModel.getDriverLatLong(Parameters(driverId = orderDetails.driverDetails.id.toString()))
                } else {*/
                    getDeliveryData(orderDetails)
                //}


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
                navigator.load(TrackGoogleMapFragment::class.java).setBundle(Bundle().apply {
                    putString(ConstantApp.PassValue.ORDER_ID, orderId)
                }).replace(true)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(appFireBaseNotification: AppFirebaseNotification) {
        if(appFireBaseNotification.tag== ConstantApp.Firebase.PICKEDUP_FOOD_ORDER_BY_DRIVER ||
                appFireBaseNotification.tag== ConstantApp.Firebase.ACCEPT_FOODORDER ||
                appFireBaseNotification.tag== ConstantApp.Firebase.DELIVERED_FOOD_ORDER_BY_DRIVER){
            clearNotification(activity!!)
            viewModel.getOrderDetails(appFireBaseNotification.orderId.toString())
        }

    }
}

