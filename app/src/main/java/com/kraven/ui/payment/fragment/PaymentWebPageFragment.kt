package com.kraven.ui.payment.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.webkit.*
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.kraven.R
import com.kraven.core.StatusCode
import com.kraven.data.mvnsource.HostedPageOrderResponse
import com.kraven.data.mvnsource.OrderCancelService
import com.kraven.di.component.FragmentComponent
import com.kraven.extensions.addFireBaseLogs
import com.kraven.extensions.extraNotNull
import com.kraven.extensions.setAdjustResize
import com.kraven.ui.activity.HomeActivity
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.cart.fragment.CompleteOrderCartFragment
import com.kraven.ui.cart.viewModel.CartViewModel
import com.kraven.ui.home.viewmodel.HomeViewModel
import com.kraven.ui.order.beverage.SpecialBeverageViewModel
import com.kraven.ui.payment.model.HostedPageModel
import com.kraven.ui.payment.viewmodel.PaymentViewModel
import com.kraven.utils.ConstantApp
import com.kraven.utils.Formatter
import kotlinx.android.synthetic.main.web_view_fragment.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.HashMap


class PaymentWebPageFragment : BaseFragment(), BaseFragment.OnCompated {

    companion object {
        var progressDialog: ProgressDialog? = null
        var hostedPageModel: HostedPageModel? = null
    }

    private var callActivity: CallAPI? = null
    private var restaurantname: String? = null
    private var parameters: HashMap<String, Any>? = null
    private var orderPage: String? = null
    private var isWallet: Boolean? = null
    private var amount: Float? = null
    private var hostedPageOrderId:Int = -1

    //private var transactionId: String? = null
    private lateinit var viewModel: PaymentViewModel
    private lateinit var viewModelCart: CartViewModel
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var specialBeverageViewModel: SpecialBeverageViewModel
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private val transactionId by extraNotNull<String>("transaction_id")
    private var callDeleteAPI = false
    private var cancelOrderFlag = false
    private var errorMessage:String?=null

    /* private val hostedPageModel: HostedPageModel by lazy {
         val args = arguments ?: throw IllegalArgumentException("Missing arguments")
         args.getParcelable("card") as HostedPageModel
     }
 */

    override fun onStart() {
        super.onStart()
        setAdjustResize()
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        firebaseAnalytics = Firebase.analytics
        if (arguments != null) {
            hostedPageModel = arguments?.getParcelable("card")
        }
        viewModel = ViewModelProviders.of(this, viewModelFactory)[PaymentViewModel::class.java]
        viewModelCart = ViewModelProviders.of(this, viewModelFactory)[CartViewModel::class.java]
        specialBeverageViewModel = ViewModelProviders.of(this, viewModelFactory)[SpecialBeverageViewModel::class.java]
        homeViewModel = ViewModelProviders.of(this, viewModelFactory)[HomeViewModel::class.java]
        if (arguments != null) {
            isWallet = arguments!!.getBoolean("isWallet")
            if (isWallet as Boolean) {
                amount = arguments!!.getFloat("amount")
            }
            parameters = arguments!!.getSerializable("CART") as HashMap<String, Any>?
            restaurantname = arguments!!.getString("restaurantname")
            orderPage = arguments!!.getString(ConstantApp.PassValue.ORDER_FOOD)
            hostedPageOrderId = arguments!!.getInt("hostedPageOrderId", -1)
        }
        callActivity = CallAPI()
        callActivity?.onCompeted = this

        progressDialog = ProgressDialog(activity!!)
        //progressDialog!!.setMessage("Please wait. Your payment will take a minute and 30 seconds to process.\nPlease do not close the application as this may disrupt the payment process.")
        progressDialog!!.setMessage("Your card is being proceed.")
        progressDialog!!.setCancelable(false)
        progressDialog!!.setCanceledOnTouchOutside(false)

        registerObserver()
    }

    private fun registerObserver() {

        viewModelCart.deleteFacLogLiveData.observe(this, {
            callDeleteAPI = true
            showLoader(false)
            if (progressDialog != null && progressDialog?.isShowing!!) {
                progressDialog?.dismiss()
            }
            if(errorMessage!=null){
                showMessage(errorMessage!!)
                  Handler().postDelayed({
                      navigator.goBack()
                  }, 500)
            }else{
                navigator.goBack()
            }
        })
        homeViewModel.addMoneyToWallet.observe(this, {
            if (progressDialog != null && progressDialog?.isShowing!!) {
                progressDialog?.dismiss()
            }
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    session.saveTempWalletParameters = null
                    val resultIntent = Intent()
                    activity!!.setResult(Activity.RESULT_OK, resultIntent)
                    navigator.finish()
                }
                StatusCode.CODE_INVALID_REQUEST -> {
                    addFireBaseLogs("1013", "Payment Web View code 0 Fragment Wallet $transactionId", it.message)
                    showMessage(it.message)
                    navigator.loadActivity(HomeActivity::class.java).byFinishingAll().start()
                }
                else -> {
                    addFireBaseLogs("1013", "Payment Web View Fragment  Wallet - $transactionId", it.message)
                    showMessage(it.message)
                }
            }
        }) { true }

        viewModelCart.cancelHostedPageFoodLiveData.observe(this, {
            if (progressDialog != null && progressDialog?.isShowing!!) {
                progressDialog?.dismiss()
            }

            navigator.goBack()
        })

        viewModelCart.cancelHostedPageBeverageLiveData.observe(this, {
            if (progressDialog != null && progressDialog?.isShowing!!) {
                progressDialog?.dismiss()
            }

            navigator.goBack()
        })
    }

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    @SuppressLint("SetJavaScriptEnabled")
    override fun createLayout(): Int = R.layout.web_view_fragment

    @SuppressLint("JavascriptInterface", "SetJavaScriptEnabled")
    override fun bindData() {

        toolbar.showToolbar(true)
        toolbar.setToolbarTitle("Payment")
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)

        webView.webViewClient = MyBrowser()
        webView.settings.loadsImagesAutomatically = true
        webView.settings.javaScriptEnabled = true
        webView.settings.defaultZoom = WebSettings.ZoomDensity.MEDIUM
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true
        webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        showLoader(true)
        webView.post {
            hostedPageModel?.data?.hostedPagesAuthorizationPayURL?.let { webView.loadUrl(it) }
        }
    }

    inner class MyBrowser : WebViewClient() {

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            Log.d("LZX"," onPageStarted URL = ${url}")
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            Log.d("LZX"," onPageFinished URL = ${url}")
            if (url.toString().contains("/MerchantPages/"))
                showLoader(false)
            if (hostedPageOrderId != -1)
                toolbar.setButtonTextVisibility(true)

            if (url == hostedPageModel?.data?.hostedPagesAuthorizationPayURL && progressDialog != null && progressDialog?.isShowing!!) {
                activity?.runOnUiThread {
                    progressDialog?.dismiss()
                }
            }
        }

        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
            Log.d("LZX", "onReceivedError : ${error?.description}")
            super.onReceivedError(view, request, error)
            addFireBaseLogs("1008", "Payment Web Page Fragment", "Display Error ${error.toString()} didFailLoadWithError")
            if (isWallet != false) {
                if (progressDialog != null && progressDialog?.isShowing!!) {
                    progressDialog?.dismiss()
                }
                val resultIntent = Intent()
                activity!!.setResult(Activity.RESULT_OK, resultIntent)
                navigator.finish()
            } else {
                if (progressDialog != null && progressDialog?.isShowing!!) {
                    progressDialog?.dismiss()
                }
                session.cartModel = null
                session.cartCount = "0"
                Log.d("LZX", "load completion fragment")
                /*navigator.load(CompleteOrderCartFragment::class.java).setBundle(Bundle().apply {
                    putString("name", restaurantname)
                }).replace(false)*/
            }
        }

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            if (callActivity != null && callActivity?.status == AsyncTask.Status.PENDING) {
                if (progressDialog != null && progressDialog?.isShowing?.not()!!) {
                    activity?.runOnUiThread {
                        toolbar.showToolbar(false)
                        webView.visibility = View.GONE
                        llView.visibility = View.VISIBLE
                    }
                }
                callActivity!!.execute(request?.url.toString())
            }
            return true
        }
    }

    class CallAPI : AsyncTask<String, Void, String>() {
        private var exception: Exception? = null
        var onCompeted: OnCompated? = null

        override fun doInBackground(vararg params: String?): String? {
            try {

                val mURL = URL(params[0])
                with(mURL.openConnection() as HttpURLConnection) {
                    requestMethod = "GET"
                    BufferedReader(InputStreamReader(inputStream)).use {
                        val response = StringBuffer()

                        var inputLine = it.readLine()
                        while (inputLine != null) {
                            response.append(inputLine)
                            inputLine = it.readLine()
                        }
                        it.close()
                        return response.toString()
                    }
                }
            } catch (e: Exception) {
                this.exception = e
                return null
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            onCompeted!!.finished(result!!)
        }
    }

    override fun finished(resutl: String) {
        Log.d("LZX", "finished : ${resutl}")
        if (isWallet != false) {
            try {

                val jsonObject = JSONObject(resutl)
                if (jsonObject.has("HostedPageResultsResult")) {
                    val jsonHostedPageResultsResult = jsonObject.getJSONObject("HostedPageResultsResult")
                    val jsonAuthResponse = jsonHostedPageResultsResult.getJSONObject("AuthResponse")
                    val jsonCreditCardTransactionResults = jsonAuthResponse.getJSONObject("CreditCardTransactionResults")

                    Log.d("LZX", "PaymentWebPageFragment Wallet: ${jsonCreditCardTransactionResults.getString("OriginalResponseCode")}")
                    Log.d("LZX", "PaymentWebPageFragment Wallet : ${jsonCreditCardTransactionResults.getString("ResponseCode")}")
                    Log.d("LZX", "PaymentWebPageFragment Wallet : ${jsonCreditCardTransactionResults.getString("ReasonCodeDescription")}")

                    if (jsonCreditCardTransactionResults.getString("OriginalResponseCode") == "00" &&
                            jsonCreditCardTransactionResults.getString("ResponseCode") == "1") {

                        if (progressDialog != null && progressDialog?.isShowing!!) {
                            progressDialog?.dismiss()
                        }
                        val resultIntent = Intent()
                        activity!!.setResult(Activity.RESULT_OK, resultIntent)
                        navigator.finish()
                    } else {
                        if (progressDialog != null && progressDialog?.isShowing!!) {
                            progressDialog?.dismiss()
                        }
                        //showMessage(jsonCreditCardTransactionResults.getString("ReasonCodeDescription"))
                        Handler().postDelayed(
                            {
                                //here
                                val bundle = Bundle()
                                bundle.putBoolean("isDeclined", true)
                                bundle.putString("errorMessage", jsonCreditCardTransactionResults.getString("ReasonCodeDescription"))
                                bundle.putString("responseCode", jsonCreditCardTransactionResults.getString("ResponseCode"))
                                setFragmentResult("walletDeclined", bundle)
                                navigator.goBack()
                            }, 500)
                    }
                } else {

                    if (progressDialog != null && progressDialog?.isShowing!!) {
                        progressDialog?.dismiss()
                    }
                    val resultIntent = Intent()
                    activity!!.setResult(Activity.RESULT_OK, resultIntent)
                    navigator.finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()

                if (progressDialog != null && progressDialog?.isShowing!!) {
                    progressDialog?.dismiss()
                }
                val resultIntent = Intent()
                activity!!.setResult(Activity.RESULT_OK, resultIntent)
                navigator.finish()
            }
        } else {
            try {
                val jsonObject = JSONObject(resutl)
                if (jsonObject.has("HostedPageResultsResult")) {
                    Log.d("LZX", "not wallet / 1")
                    val jsonHostedPageResultsResult = jsonObject.getJSONObject("HostedPageResultsResult")
                    val jsonAuthResponse = jsonHostedPageResultsResult.getJSONObject("AuthResponse")
                    val jsonCreditCardTransactionResults = jsonAuthResponse.getJSONObject("CreditCardTransactionResults")

                    Log.d("LZX", "PaymentWebPageFragment : ${jsonCreditCardTransactionResults.getString("OriginalResponseCode")}")
                    Log.d("LZX", "PaymentWebPageFragment : ${jsonCreditCardTransactionResults.getString("ResponseCode")}")
                    Log.d("LZX", "PaymentWebPageFragment : ${jsonCreditCardTransactionResults.getString("ReasonCodeDescription")}")

                    if (jsonCreditCardTransactionResults.getString("OriginalResponseCode") == "00" &&
                            jsonCreditCardTransactionResults.getString("ResponseCode") == "1") {

                        parameters?.set("order_datetime", Formatter.format(Date().toString(), Formatter.YYYY_MM_DD_T_HH_MM_SSS_Z, Formatter.YYYY_MM_DD_HH_MM_SS, true).toString())
                        parameters!!["card_token"] = jsonCreditCardTransactionResults.getString("TokenizedPAN")
                        parameters!!["transaction_id"] = jsonAuthResponse.getString("OrderNumber")

                        if (progressDialog != null && progressDialog?.isShowing!!) {
                            progressDialog?.dismiss()
                        }
                        session.cartModel = null
                        session.cartCount = "0"
                        navigator.load(CompleteOrderCartFragment::class.java).setBundle(Bundle().apply {
                            putString("name", restaurantname)
                        }).replace(false)

                    } else {
                        //showMessage(jsonCreditCardTransactionResults.getString("ReasonCodeDescription"))
                        if (progressDialog != null && progressDialog?.isShowing!!) {
                            progressDialog?.dismiss()
                        }

                        val bundle = Bundle()
                        bundle.putBoolean("isDeclined", true)
                        bundle.putString("errorMessage", jsonCreditCardTransactionResults.getString("ReasonCodeDescription"))
                        bundle.putString("orderPage", orderPage)
                        bundle.putInt("hostedPageOrderId", hostedPageOrderId)
                        bundle.putString("responseCode", jsonCreditCardTransactionResults.getString("ResponseCode"))
                        setFragmentResult("paymentDeclined", bundle)
                        navigator.goBack()
                        //navigator.load(CartFragment::class.java).replace(false)
                        addFireBaseLogs("1005", "Payment Web Page Fragment", jsonCreditCardTransactionResults.getString("ReasonCodeDescription"))
                    }
                } else {
                    Log.d("LZX", "not wallet / 2")
                    if (progressDialog != null && progressDialog?.isShowing!!) {
                        progressDialog?.dismiss()
                    }
                    addFireBaseLogs("1005", "Payment Web Page Fragment", jsonObject.getString("ReasonCodeDesc"))
                    session.cartModel = null
                    session.cartCount = "0"
                    navigator.load(CompleteOrderCartFragment::class.java).setBundle(Bundle().apply {
                        putString("name", restaurantname)
                    }).replace(false)

                }
            } catch (e: Exception) {
                Log.d("LZX", "not wallet / 3 : ${e.localizedMessage}")
                e.printStackTrace()
                if (progressDialog != null && progressDialog?.isShowing!!) {
                    progressDialog?.dismiss()
                }
                addFireBaseLogs("1006", "Payment Web Page Fragment", "Could not get json response from webpage FAC")

                session.cartModel = null
                session.cartCount = "0"
                navigator.load(CompleteOrderCartFragment::class.java).setBundle(Bundle().apply {
                    putString("name", restaurantname)
                }).replace(false)

            }
        }
    }

    /*override fun onBackActionPerform(): Boolean {
        return if (callDeleteAPI) {
            true
        } else {
            showLoader(true)
            when (orderPage) {
                ConstantApp.PassValue.ORDER_FOOD -> {
                    viewModelCart.deleteFacLog(Parameters(transactionId = transactionId, orderType = "Food"))
                }
                ConstantApp.PassValue.ORDER_BEVERAGE_SPECIAL -> {
                    viewModelCart.deleteFacLog(Parameters(transactionId = transactionId, orderType = "SpecialBeverage"))
                }
                else -> {
                    viewModelCart.deleteFacLog(Parameters(transactionId = transactionId, orderType = "Beverage"))
                }
            }
            false
        }
    }*/

    override fun onBackActionPerform(): Boolean {
        if (orderPage != null && hostedPageOrderId != -1 && !cancelOrderFlag) {
            if (orderPage == ConstantApp.PassValue.ORDER_FOOD) {
                showLoader(true)
                //viewModelCart.cancelHostedPageFood(hostedPageOrderId.toString())
                cancelOrderFlag = true
                canCancelHostedPageOrder(true, hostedPageOrderId.toString())
                return false
            }
            if (orderPage == ConstantApp.PassValue.ORDER_BEVERAGE) {
                showLoader(true)
                cancelOrderFlag = true
                //viewModelCart.cancelHostedPageBeverage(hostedPageOrderId.toString())
                canCancelHostedPageOrder(false, hostedPageOrderId.toString())
                return false
            }
        }
        return super.onBackActionPerform()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (progressDialog != null)
            progressDialog!!.dismiss()
    }

    private fun canCancelHostedPageOrder(isFood: Boolean, orderID: String) {

        val retrofit = Retrofit.Builder()
            //.baseUrl("http://test-delivery.kravenbahamas.com/")
            .baseUrl("http://uat-delivery.kravenbahamas.com/")
            //.baseUrl("http://kravenbahamas.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(OrderCancelService::class.java)
        val call: Call<HostedPageOrderResponse> =
            if (isFood) service.cancelHostedPageFoodOrderRetrofit(orderID)
            else service.cancelHostedPageBeverageOrderRetrofit(orderID)

        call.enqueue(object : Callback<HostedPageOrderResponse> {
            override fun onResponse(call: Call<HostedPageOrderResponse>, response: Response<HostedPageOrderResponse>) {

                showLoader(false)
                navigator.goBack()
            }

            override fun onFailure(call: Call<HostedPageOrderResponse>, t: Throwable) {

                showLoader(false)
                navigator.goBack()
            }
        })
    }
}