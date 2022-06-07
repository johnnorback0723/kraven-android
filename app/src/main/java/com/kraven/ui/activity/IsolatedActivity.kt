package com.kraven.ui.activity

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.kraven.R
import com.kraven.core.AppExecutors
import com.kraven.data.mvnsource.OrderCancelService
import com.kraven.data.mvnsource.OrderPaymentStatus
import com.kraven.data.mvnsource.UpdateNotifyStatus
import com.kraven.di.component.ActivityComponent
import com.kraven.ui.base.BaseActivity
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.manager.ActivityStarter.Companion.ACTIVITY_FIRST_PAGE
import com.kraven.ui.track.viewmodel.TrackViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject


/**
 * Created by hlink on 3/11/17.
 */

class IsolatedActivity : BaseActivity() {

    val appExecutors = AppExecutors()
    private lateinit var ratingViewModel: TrackViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun findFragmentPlaceHolder() = R.id.mainContainer

    override fun findContentView() = R.layout.activity_isolated

    override fun inject(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (supportActionBar != null)
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        /*supportFragmentManager
                .beginTransaction()
                .replace(R.id.navigation_container, DrawerFragment(), DrawerFragment::class.java.simpleName)
                .commitAllowingStateLoss()

        drawerLayout.setScrimColor(ContextCompat.getColor(applicationContext!!, R.color.black_transparent))

        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        val params = navigation_view.layoutParams as DrawerLayout.LayoutParams
        params.width = metrics.widthPixels
        navigation_view.layoutParams = params*/
        if (savedInstanceState == null) {
            showBackButton(true)
            val page = intent.getSerializableExtra(ACTIVITY_FIRST_PAGE) as? Class<BaseFragment>

            if (page != null)
                load(page)
                        .setBundle(intent.extras!!)
                        .add(false)

        }

        ratingViewModel = ViewModelProviders.of(this, viewModelFactory)[TrackViewModel::class.java]

        /*val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post(object : Runnable {
            override fun run() {
                ratingViewModel.getOrderHistory(ConstantApp.NOW, "1")
                mainHandler.postDelayed(this, 15000)
            }
        })

        ratingViewModel.getOrderHistory(ConstantApp.NOW, "1")
        ratingViewModel.getOrderHistory.observe(this, {
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    orderPaymentStatus(it.data?.get(0)?.id ?: 0)
                }
                StatusCode.CODE_NO_DATA -> {
                }
            }
        }) { true }*/
    }

    fun orderPaymentStatus(orderId: Int) {
        val retrofit = Retrofit.Builder()
                //.baseUrl("http://test-delivery.kravenbahamas.com:7171")
                .baseUrl("http://uat-delivery.kravenbahamas.com:7171")
                //.baseUrl("http://kravenbahamas.com:7171")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val service = retrofit.create(OrderCancelService::class.java)
        val call = service.orderPaymentStatus(orderId)
        call.enqueue(object : Callback<OrderPaymentStatus> {
            override fun onResponse(call: Call<OrderPaymentStatus>, response: Response<OrderPaymentStatus>) {
                if (response.code() == 200) {
                    val response = response.body()!!
                    if (response.result.code == 1) {
                        val sharedPreference = getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
                        val isShown = sharedPreference.getBoolean(orderId.toString(), false)
                        if (!isShown)
                            showPaymentInfo(orderId, response.result.data)
                    }
                }
            }

            override fun onFailure(call: Call<OrderPaymentStatus>, t: Throwable) {
            }
        })
    }

    internal fun showPaymentInfo(orderID: Int, message: String) {

        val sharedPreference = getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putBoolean(orderID.toString(), true)
        editor.apply()
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.dialog_payment_status)
        val body = dialog.findViewById(R.id.textViewInfo) as TextView
        body.text = message
        val yesBtn = dialog.findViewById(R.id.buttonOk) as Button
        yesBtn.setOnClickListener {
            updateNotifyStatus(orderID)
            dialog.dismiss()
        }
        if (!dialog.isShowing)
            dialog.show()
    }

    fun updateNotifyStatus(orderID: Int) {
        val retrofit = Retrofit.Builder()
                //.baseUrl("http://test-delivery.kravenbahamas.com:7171")
                .baseUrl("http://uat-delivery.kravenbahamas.com:7171")
                //.baseUrl("http://kravenbahamas.com:7171")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val service = retrofit.create(OrderCancelService::class.java)
        val call = service.updateNotifyStatus(orderID)
        call.enqueue(object : Callback<UpdateNotifyStatus> {
            override fun onResponse(call: Call<UpdateNotifyStatus>, response: Response<UpdateNotifyStatus>) {
                if (response.code() == 200) { response.body()!! }
            }
            override fun onFailure(call: Call<UpdateNotifyStatus>, t: Throwable) {
            }
        })
    }

    /*override fun onResume() {
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(close: String) {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
    }
*/
}
