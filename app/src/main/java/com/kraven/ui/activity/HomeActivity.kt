package com.kraven.ui.activity

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.kraven.R
import com.kraven.application.KravenCustomer
import com.kraven.core.StatusCode
import com.kraven.data.URLFactory
import com.kraven.data.mvnsource.OrderCancelService
import com.kraven.data.mvnsource.OrderPaymentStatus
import com.kraven.data.mvnsource.UpdateNotifyStatus
import com.kraven.di.component.ActivityComponent
import com.kraven.fcm.AppFirebaseNotification
import com.kraven.ui.base.BaseActivity
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.comman.adapter.DrawerAdapter
import com.kraven.ui.comman.fragment.DrawerFragment
import com.kraven.ui.edit.profile.fragment.EditProfileFragment
import com.kraven.ui.home.fragment.HomeNewFragment
import com.kraven.ui.my.order.fragment.MyOrderHistoryFragment
import com.kraven.ui.notification.fragment.NotificationFragment
import com.kraven.ui.order.beverage.fragment.SpecialBeverageDetailsFragment
import com.kraven.ui.rating.RatingOrderBeverageFragment
import com.kraven.ui.rating.RatingOrderHistoryFragment
import com.kraven.ui.track.fragment.TrackOrderBeverageFragment
import com.kraven.ui.track.fragment.TrackOrderFragment
import com.kraven.ui.track.viewmodel.TrackViewModel
import com.kraven.utils.ConstantApp
import com.kraven.utils.Formatter
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import javax.inject.Inject

class HomeActivity : BaseActivity(),
        EditProfileFragment.UpdateUserInfo {

    private lateinit var ratingViewModel: TrackViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    /*  override fun close() {
          if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START))
              drawerLayout.closeDrawer(GravityCompat.START)
      }*/

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(close: String) {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
    }

    override fun updateUser() {
        val drawerFragment = supportFragmentManager.findFragmentByTag(DrawerFragment::class.java.simpleName) as DrawerFragment
        drawerFragment.updateUser()
    }

    override fun findFragmentPlaceHolder(): Int = R.id.mainContainer

    override fun findContentView(): Int = R.layout.activity_main

    override fun inject(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (session.transactionIdCard == null || session.transactionIdCard == "") {
            val currentDate = Formatter.format(Date().toString(), Formatter.YYYY_MM_DD_T_HH_MM_SSS_Z, "yyyyMMdd_HHmmss", true).toString()
            session.transactionIdCard = "TRN_".plus(session.user?.id!!).plus("_").plus(if (URLFactory.IS_LOCAL) getString(R.string.MERCHANT_ID_TESTING) else getString(R.string.MERCHANT_ID_LIVE)).plus(currentDate)
        }
        ratingViewModel = ViewModelProviders.of(this, viewModelFactory)[TrackViewModel::class.java]
        if (KravenCustomer.isWaitingPlaceOrder) {
            KravenCustomer.isWaitingPlaceOrder = false
            DrawerAdapter.row_index = 5
            load(MyOrderHistoryFragment::class.java).replace(false)
        }
        else {
            DrawerAdapter.row_index = 0
            load(HomeNewFragment::class.java).replace(false)
        }
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.navigation_container, DrawerFragment(), DrawerFragment::class.java.simpleName)
                .commitAllowingStateLoss()

        drawerLayout.setScrimColor(ContextCompat.getColor(applicationContext!!, R.color.black_transparent))

        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        val params = navigation_view.layoutParams as DrawerLayout.LayoutParams
        params.width = metrics.widthPixels
        navigation_view.layoutParams = params

        if (intent.hasExtra(ConstantApp.PassValue.ORDER_ID)) {
            val orderId = intent.getStringExtra(ConstantApp.PassValue.ORDER_ID)
            val tag = intent.getStringExtra(ConstantApp.PassValue.TAG)
            openScreenTagWise(tag!!, orderId!!)
        }

        ratingViewModel.getOrderDetails.observe(this, {
            toggleLoader(false, "")
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    if (it.data?.deliveryType == ConstantApp.PICKUP) {
                        if (it.data.restaurantReview == 0) {
                            loadActivity(IsolatedActivity::class.java).setPage(RatingOrderHistoryFragment::class.java)
                                    .addBundle(Bundle().apply {
                                        putParcelable("OrderDetails", it.data)
                                    }).start()
                        }
                    } else if (it.data?.deliveryType == ConstantApp.DELIVERY) {
                        if (it.data.restaurantReview == 0 || it.data.driverReview == 0) {
                            loadActivity(IsolatedActivity::class.java).setPage(RatingOrderHistoryFragment::class.java)
                                    .addBundle(Bundle().apply {
                                        putParcelable("OrderDetails", it.data)
                                    }).start()
                        }
                    }
                }
                else -> {
                    showErrorMessage(it.message)
                }
            }
        }, { true })

        ratingViewModel.getOrderBeverageDetails.observe(this, {
            toggleLoader(false, "")
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    if (it.data?.deliveryType == ConstantApp.PICKUP) {
                        if (it.data.beverageReview == 0) {
                            loadActivity(IsolatedActivity::class.java).setPage(RatingOrderBeverageFragment::class.java)
                                    .addBundle(Bundle().apply {
                                        putParcelable("OrderDetails", it.data)
                                    }).start()
                        }
                    } else if (it.data?.deliveryType == ConstantApp.DELIVERY) {
                        if (it.data.driverReview == 0 || it.data.beverageReview == 0) {
                            loadActivity(IsolatedActivity::class.java).setPage(RatingOrderBeverageFragment::class.java)
                                    .addBundle(Bundle().apply {
                                        putParcelable("OrderDetails", it.data)
                                    }).start()
                        }
                    }
                }
                else -> {
                    showErrorMessage(it.message)
                }
            }
        })

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
            override fun onResponse(call: Call<UpdateNotifyStatus>, response: Response<UpdateNotifyStatus>) {}

            override fun onFailure(call: Call<UpdateNotifyStatus>, t: Throwable) {}
        })
    }


    fun updateApplication() {
        commandDialog(getString(R.string.app_name), "A new version of Kraven User is available. Please update now.",
                object : BaseFragment.DialogInterface {
                    override fun onClickOk() {
                        val appPackageName = packageName
                        try {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
                        } catch (anfe: ActivityNotFoundException) {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
                        }
                        finishAffinity()
                    }
                })

        /* val SIREN_JSON_URL = if (URLFactory.IS_LOCAL) ("http://").plus(getString(R.string.test_host_server).plus("/version.json")) else ("http://").plus(getString(R.string.HOST_IS_SERVER).plus("/version.json"))
         val siren = Siren.getInstance(this)
         siren.setSirenListener(object : ISirenListener {
             override fun onDetectNewVersionWithoutAlert(message: String?) {

             }

             override fun onCancel() {

             }

             override fun onShowUpdateDialog() {

             }

             override fun onSkipVersion() {

             }

             override fun onLaunchGooglePlay() {
                 finishAffinity()

             }

             override fun onError(p0: Exception?) {
                 p0?.printStackTrace()
             }
         })
         siren.setMajorUpdateAlertType(SirenAlertType.FORCE)
         siren.setMinorUpdateAlertType(SirenAlertType.FORCE)
         siren.setPatchUpdateAlertType(SirenAlertType.FORCE)
         siren.setRevisionUpdateAlertType(SirenAlertType.FORCE)
         siren.setVersionCodeUpdateAlertType(SirenAlertType.FORCE)
         siren.checkVersion(this, SirenVersionCheckType.IMMEDIATELY, SIREN_JSON_URL)*/
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent!!.hasExtra(ConstantApp.Firebase.KEY_NOTIFICATION)) {
            val notification = intent.getParcelableExtra<AppFirebaseNotification>(ConstantApp.Firebase.KEY_NOTIFICATION)
            if (notification != null) {
                openScreenTagWise(notification.tag, notification.orderId.toString())
            }
        }
    }

    private fun openScreenTagWise(tag: String, orderId: String) {
        when (tag) {
            //Food Order Open Rating Screen
            ConstantApp.Firebase.DELIVERED_FOOD_ORDER_BY_RESTAURANT,
            ConstantApp.Firebase.DELIVERED_FOOD_ORDER_BY_DRIVER,
            ConstantApp.Firebase.COMPLETE_FOOD_ORDER_AUTOMATICALLY -> openRatingScreen(orderId)

            //Cancel Food Order and Beverage Order
            ConstantApp.Firebase.CANCEL_FOODORDER,
            ConstantApp.Firebase.CANCEL_BEVERAGEORDER -> openOrderHistory()

            //Accept And Pickup Food Order
            ConstantApp.Firebase.ACCEPT_FOODORDER,
            ConstantApp.Firebase.PICKEDUP_FOOD_ORDER_BY_DRIVER -> openTrackScreen(orderId, tag)


            //Accept And Pickup Beverage Order
            ConstantApp.Firebase.ACCEPT_BEVERAGEORDER,
            ConstantApp.Firebase.STARTED_BEVERAGE_ORDER_BY_DRIVER,
            ConstantApp.Firebase.PICKEDUP_BEVERAGE_ORDER_BY_DRIVER -> openBeverageTrackScreen(orderId, tag)

            //Beverage Order Open Rating Screen
            ConstantApp.Firebase.DELIVERED_BEVERAGE_ORDER_BY_DRIVER,
            ConstantApp.Firebase.COMPLETE_BEVERAG_ORDER_AUTOMATICALLY -> openRatingScreenBeverage(orderId)

            //Accept,Reject,Complete,Cancel Special Beverage Order
            ConstantApp.Firebase.ACCEPT_SPECIAL_BEVERAGE,
            ConstantApp.Firebase.REJECT_SPECIAL_BEVERAGE,
            ConstantApp.Firebase.COMPLETE_SPECIAL_BEVERAGE,
            ConstantApp.Firebase.CANCEL_SPECIAL_BEVERAGE -> openSpecialBeverageDetails(orderId, tag)

            else -> {
                load(NotificationFragment::class.java).replace(true)
            }
        }
    }

    private fun openTrackScreen(orderId: String, tag: String) {

        load(TrackOrderFragment::class.java).setBundle(
                Bundle().apply {
                    putString(ConstantApp.PassValue.ORDER_ID, orderId)
                    putString("tag", tag)
                }).replace(true)
    }


    private fun openBeverageTrackScreen(orderId: String, tag: String) {

        load(TrackOrderBeverageFragment::class.java).setBundle(
                Bundle().apply {
                    putString(ConstantApp.PassValue.ORDER_ID, orderId)
                    putString("tag", tag)
                }).replace(true)
    }

    private fun openSpecialBeverageDetails(orderId: String, tag: String) {

        load(SpecialBeverageDetailsFragment::class.java).setBundle(
                Bundle().apply {
                    putString(ConstantApp.PassValue.ORDER_ID, orderId)
                    putString("tag", tag)
                }
        ).replace(true)

    }


    private fun openOrderHistory() {
        load(MyOrderHistoryFragment::class.java).clearHistory(null).replace(false)
    }

    private fun openRatingScreen(orderId: String) {
        ratingViewModel.getOrderDetails(orderId)
    }

    private fun openRatingScreenBeverage(orderId: String) {
        ratingViewModel.getOrderBeverageDetails(orderId)
    }

}