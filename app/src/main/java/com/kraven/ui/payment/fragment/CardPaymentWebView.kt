package com.kraven.ui.payment.fragment

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.JsonReader
import android.util.JsonToken
import android.util.Log
import android.view.View
import android.webkit.*
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProviders
import com.kraven.R
import com.kraven.core.StatusCode
import com.kraven.data.mvnsource.OrderCancelService
import com.kraven.data.pojo.Parameters
import com.kraven.di.component.FragmentComponent
import com.kraven.extensions.addFireBaseLogs
import com.kraven.extensions.extraNotNull
import com.kraven.ui.activity.HomeActivity
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.cart.fragment.CompleteOrderCartFragment
import com.kraven.ui.cart.viewModel.CartViewModel
import com.kraven.ui.order.beverage.SpecialBeverageViewModel
import com.kraven.ui.payment.viewmodel.PaymentViewModel
import com.kraven.utils.ConstantApp
import kotlinx.android.synthetic.main.card_payment_werbview.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.StringReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class CardPaymentWebView : BaseFragment(), BaseFragment.OnCompated {

    private var restaurantname: String? = null
    private var parameters: HashMap<String, Any>? = null
    private var orderPage: String? = null
    private var cardPaymentOrderId: Int = -1

    private var callActivity: CardPaymentWebView.CallAPI? = null
    private lateinit var viewModel: PaymentViewModel
    private lateinit var viewModelCart: CartViewModel
    private lateinit var specialBeverageViewModel: SpecialBeverageViewModel
    var progressDialog: ProgressDialog? = null
    private var callDeleteAPI = false
    private var errorMessage:String?=null
    private val transactionId by extraNotNull<String>("transaction_id")

    val card: String? by lazy {
        val args = arguments ?: throw IllegalArgumentException("Missing arguments")
        args.getString("card")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)[PaymentViewModel::class.java]
        viewModelCart = ViewModelProviders.of(this, viewModelFactory)[CartViewModel::class.java]
        specialBeverageViewModel = ViewModelProviders.of(this, viewModelFactory)[SpecialBeverageViewModel::class.java]
        if (arguments != null) {
            parameters = arguments!!.getSerializable("CART") as HashMap<String, Any>?
            restaurantname = arguments!!.getString("restaurantname")
            orderPage = arguments!!.getString(ConstantApp.PassValue.ORDER_FOOD)
            cardPaymentOrderId = arguments!!.getInt("cardPaymentOrderId", -1)
        }

        callActivity = CardPaymentWebView.CallAPI()
        callActivity?.onCompeted = this

        progressDialog = ProgressDialog(activity!!)
        //progressDialog!!.setMessage("Please wait. Your payment will take a minute and 30 seconds to process.\nPlease do not close the application as this may disrupt the payment process.")
        progressDialog!!.setMessage("Please wait...")
        progressDialog!!.setCancelable(false)
        progressDialog!!.setCanceledOnTouchOutside(false)

        registerObserver()
    }

    private fun registerObserver() {
        viewModelCart.deleteFacLogLiveData.observe(this, {
            callDeleteAPI = true
            if (progressDialog != null && progressDialog?.isShowing!!) {
                progressDialog?.dismiss()
            }
            showLoader(false)
            if(errorMessage!=null){
                showMessage(errorMessage!!)
                Handler().postDelayed({
                    navigator.goBack()
                }, 500)
            }else{
                navigator.goBack()
            }
        })

        viewModelCart.placeOrder.observe(this, {
            if (progressDialog != null && progressDialog?.isShowing!!) {
                progressDialog?.dismiss()
            }
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    session.cartModel = null
                    session.cartCount = "0"
                    navigator.load(CompleteOrderCartFragment::class.java).setBundle(Bundle().apply {
                        putString("name", restaurantname)
                    }).replace(false)
                }
                StatusCode.CODE_INVALID_REQUEST -> {
                    addFireBaseLogs("1013", "Card Web View Fragment Code 0 Restaurant or Beverage - $transactionId", it.message)
                    showMessage(it.message)
                    navigator.loadActivity(HomeActivity::class.java).byFinishingAll().start()
                }
                else -> {
                    addFireBaseLogs("1013", "Card Web View Restaurant or Beverage - $transactionId", it.message)
                    showMessage(it.message)
                }
            }
        }) { true }

        specialBeverageViewModel.paySpecialBeverageOrder.observe(this, {
            if (progressDialog != null && progressDialog?.isShowing!!) {
                progressDialog?.dismiss()
            }
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    navigator.load(CompleteOrderCartFragment::class.java).setBundle(Bundle().apply {
                        putString("name", restaurantname)
                    }).replace(false)
                }
                else -> {
                    showMessage(it.message)
                    addFireBaseLogs("1013", "Card Payment Web View SpecialBeverage", transactionId)
                }
            }
        })

        viewModel.getTransactionStatusLiveData.observe(this, {
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    Handler().postDelayed({
                        if (it.data?.data?.transactionStatusResult?.creditCardTransactionResults?.originalResponseCode == "00"
                                && it.data.data?.transactionStatusResult?.creditCardTransactionResults?.responseCode == "1") {
                            if (orderPage == ConstantApp.PassValue.ORDER_FOOD || orderPage == ConstantApp.PassValue.ORDER_FOOD_FUTURE) {
                                viewModelCart.placeOrder(parameters!!)
                            } else if (orderPage == ConstantApp.PassValue.ORDER_BEVERAGE_SPECIAL) {
                                specialBeverageViewModel.paySpecialBeverageOrder(parameters!!)
                            } else {
                                viewModelCart.placeOrderBeverage(parameters!!)
                            }
                        } else {
                            if (progressDialog != null && progressDialog?.isShowing!!) {
                                progressDialog?.dismiss()
                            }
                            showMessage("Transaction Failed. Please try again.")
                            Handler().postDelayed({
                                navigator.goBack()
                            }, 500)
                        }
                    }, 10000)

                }
                else -> {
                    if (progressDialog != null && progressDialog?.isShowing!!) {
                        progressDialog?.dismiss()
                    }
                    showMessage("Transaction Failed. Please try again.")
                    Handler().postDelayed({
                        navigator.goBack()
                    }, 500)
                }
            }
        })
    }

    private fun performCardPayment(orderID: Int, isFood: Boolean) {
        val retrofit = Retrofit.Builder()
            //.baseUrl("http://test-delivery.kravenbahamas.com/")
            .baseUrl("http://uat-delivery.kravenbahamas.com/")
            //.baseUrl("http://kravenbahamas.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(OrderCancelService::class.java)
        val call =
            if (isFood) service.performCardPaymentProcessedFood(orderID.toString())
            else service.performCardPaymentProcessedBeverage(orderID.toString())
        call?.enqueue(object : Callback<Any?> {
            override fun onResponse(call: Call<Any?>, response: Response<Any?>) {
                webView.post { card?.let { webView.loadData(it, "text/html", "UTF-8") } }
            }

            override fun onFailure(call: Call<Any?>, t: Throwable) {}
        })
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun createLayout(): Int = R.layout.web_view_fragment

    override fun inject(fragmentComponent: FragmentComponent) {
        fragmentComponent.inject(this)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun bindData() {
       /* if (progressDialog != null && progressDialog?.isShowing?.not()!!) {
            ThreadUtils.runOnUiThread {
                progressDialog?.show()
            }
        }*/
        toolbar.showToolbar(false)
        toolbar.setToolbarTitle("Payment")
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)

        WebView.setWebContentsDebuggingEnabled(true)

        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        webView.settings.displayZoomControls = false

        webView.webChromeClient = WebChromeClient()
        webView.settings.setSupportMultipleWindows(true)
        webView.settings.javaScriptEnabled = true
        webView.settings.defaultTextEncodingName = "utf-8"
        webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        //webView.loadData(card, "text/html", "UTF-8")

        webView.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {
                if (session.cartModel == null)
                    return

                //if (url == "https://kravenbahamas.com/fac/hostedpageresponse") {
                if (url?.contains("/fac/hostedpageresponse") == true) {
                    view?.evaluateJavascript("(function() { return (document.getElementsByTagName('body')[0].innerHTML); })();") {
                        val reader = JsonReader(StringReader(it))
                        reader.isLenient = true
                        try {
                            if (reader.peek() == JsonToken.STRING) {
                                val domStr = reader.nextString()
                                if (domStr != null) {
                                    try {
                                        val jsonObject = JSONObject(domStr)
                                        if (jsonObject.getString("OriginalResponseCode") == "00" &&
                                                jsonObject.getString("ResponseCode") == "1") {
                                                    Log.d("JSR", "Case  - 1")
                                           /* session.saveOrderPage = orderPage
                                            session.saveRestaurantName = restaurantname
                                            parameters!!["card_token"] = jsonObject.getString("TokenizedPAN")
                                            parameters!!["transaction_id"] = jsonObject.getString("OrderID")
                                            parameters!!["order_datetime"] = Formatter.format(Date().toString(), Formatter.YYYY_MM_DD_T_HH_MM_SSS_Z, Formatter.YYYY_MM_DD_HH_MM_SS, true).toString()

                                            if (orderPage == ConstantApp.PassValue.ORDER_FOOD || orderPage == ConstantApp.PassValue.ORDER_FOOD_FUTURE) {
                                                viewModelCart.placeOrder(parameters!!)
                                            } else if (orderPage == ConstantApp.PassValue.ORDER_BEVERAGE_SPECIAL) {
                                                specialBeverageViewModel.paySpecialBeverageOrder(parameters!!)
                                            } else {
                                                viewModelCart.placeOrderBeverage(parameters!!)
                                            }*/
                                            /*  session.saveTempParameters = parameters
                                              viewModel.getTransactionStatus(hashMapOf("transaction_id" to jsonObject.getString("OrderID")))*/
                                            if (progressDialog != null && progressDialog?.isShowing!!) {
                                                progressDialog?.dismiss()
                                            }
                                            session.cartModel = null
                                            session.cartCount = "0"
                                            navigator.load(CompleteOrderCartFragment::class.java).setBundle(Bundle().apply {
                                                putString("name", restaurantname)
                                            }).replace(false)
                                        } else {
                                            Log.d("JSR", "Case  - 2")
                                            addFireBaseLogs("1011", "Card Payment Web View", jsonObject.getString("ReasonCodeDesc"))
                                            if (progressDialog != null && progressDialog?.isShowing!!) {
                                                progressDialog?.dismiss()
                                            }
                                            session.cartModel = null
                                            session.cartCount = "0"

                                            val bundle = Bundle()
                                            bundle.putBoolean("isDeclined", true)
                                            bundle.putString("errorMessage", "Error...")
                                            bundle.putString("orderPage", orderPage)
                                            bundle.putInt("hostedPageOrderId", cardPaymentOrderId)
                                            bundle.putString("responseCode", "Received Error")
                                            setFragmentResult("paymentDeclined", bundle)
                                            navigator.goBack()
                                        }
                                    } catch (e: Exception) {
                                        Log.d("JSR", "Case  - 3")
                                        e.printStackTrace()
                                        if (progressDialog != null && progressDialog?.isShowing!!) {
                                            progressDialog?.dismiss()
                                        }
                                        session.cartModel = null
                                        session.cartCount = "0"

                                        val bundle = Bundle()
                                        bundle.putBoolean("isDeclined", true)
                                        bundle.putString("errorMessage", "Error...")
                                        bundle.putString("orderPage", orderPage)
                                        bundle.putInt("hostedPageOrderId", cardPaymentOrderId)
                                        bundle.putString("responseCode", "Received Error")
                                        setFragmentResult("paymentDeclined", bundle)
                                        navigator.goBack()
                                    }
                                }
                            }
                        } catch (e: IOException) {
                            Log.d("JSR", "Case  - 4")
                            e.printStackTrace()
                            if (progressDialog != null && progressDialog?.isShowing!!) {
                                progressDialog?.dismiss()
                            }
                            e.message?.let { it1 -> addFireBaseLogs("1012", "Card Payment Web View", it1) }
                            session.cartModel = null
                            session.cartCount = "0"

                            val bundle = Bundle()
                            bundle.putBoolean("isDeclined", true)
                            bundle.putString("errorMessage", "Error...")
                            bundle.putString("orderPage", orderPage)
                            bundle.putInt("hostedPageOrderId", cardPaymentOrderId)
                            bundle.putString("responseCode", "Received Error")
                            setFragmentResult("paymentDeclined", bundle)
                            navigator.goBack()
                        }
                    }
                } else if (url == "https://ecm.firstatlanticcommerce.com/SENTRY/PaymentGateway/Application/WFrmError.aspx") {
                    Log.d("JSR", "Case  - 5")
                    if (progressDialog != null && progressDialog?.isShowing!!) {
                        progressDialog?.dismiss()
                    }
                    session.cartModel = null
                    session.cartCount = "0"

                    addFireBaseLogs("1015", "CArd Payment Web View", "WFrmError.aspx")

                    val bundle = Bundle()
                    bundle.putBoolean("isDeclined", true)
                    bundle.putString("errorMessage", "Error...")
                    bundle.putString("orderPage", orderPage)
                    bundle.putInt("hostedPageOrderId", cardPaymentOrderId)
                    bundle.putString("responseCode", "Received Error")
                    setFragmentResult("paymentDeclined", bundle)
                    navigator.goBack()
                }
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                Log.d("JSR", "Case  - 6")
                addFireBaseLogs("1010", "CArd Payment Web View", "Display Error ${error.toString()} onReceivedError")
                session.cartModel = null
                session.cartCount = "0"

                val bundle = Bundle()
                bundle.putBoolean("isDeclined", true)
                bundle.putString("errorMessage", "Error...")
                bundle.putString("orderPage", orderPage)
                bundle.putInt("hostedPageOrderId", cardPaymentOrderId)
                bundle.putString("responseCode", "Received Error")
                setFragmentResult("paymentDeclined", bundle)
                navigator.goBack()
            }
        }

        if (cardPaymentOrderId != -1) {
            performCardPayment(cardPaymentOrderId, orderPage == ConstantApp.PassValue.ORDER_FOOD)
        } else {
            webView.post { card?.let { webView.loadData(it, "text/html", "UTF-8") } }
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
        Log.d("JSR", "finished : ${resutl}")
    }

    override fun onBackActionPerform(): Boolean {
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
    }

}