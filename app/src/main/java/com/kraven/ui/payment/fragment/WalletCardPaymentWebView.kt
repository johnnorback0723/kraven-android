package com.kraven.ui.payment.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.JsonReader
import android.util.JsonToken
import android.util.Log
import android.view.View
import android.webkit.*
import androidx.lifecycle.ViewModelProviders
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.kraven.R
import com.kraven.core.StatusCode
import com.kraven.di.component.FragmentComponent
import com.kraven.extensions.addFireBaseLogs
import com.kraven.extensions.extraNotNull
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.home.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.card_payment_werbview.*
import org.json.JSONObject
import java.io.IOException
import java.io.StringReader

class WalletCardPaymentWebView : BaseFragment() {

    private lateinit var homeViewModel: HomeViewModel
    var progressDialog: ProgressDialog? = null
    private var errorMessage: String? = null
    private val transactionId by extraNotNull<String>("transaction_id")

    val card: String? by lazy {
        val args = arguments ?: throw IllegalArgumentException("Missing arguments")
        args.getString("card")
    }
    val transaction_id: String? by lazy {
        val args = arguments ?: throw IllegalArgumentException("Missing arguments")
        args.getString("transaction_id")
    }
    val amount: String? by lazy {
        val args = arguments ?: throw IllegalArgumentException("Missing arguments")
        args.getString("amount")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel = ViewModelProviders.of(this, viewModelFactory)[HomeViewModel::class.java]
        registerObserver()

        progressDialog = ProgressDialog(activity!!)
        //progressDialog!!.setMessage("Please wait. Your payment will take a minute and 30 seconds to process.\nPlease do not close the application as this may disrupt the payment process.")
        progressDialog!!.setMessage("Please wait...")
        progressDialog!!.setCancelable(false)
        progressDialog!!.setCanceledOnTouchOutside(false)
    }

    private fun registerObserver() {
        homeViewModel.addMoneyToWallet.observe(this, {
            //showLoader(false)
            progressDialog?.dismiss()

            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    session.saveTempWalletParameters = null
                    val resultIntent = Intent()
                    activity!!.setResult(Activity.RESULT_OK, resultIntent)
                    navigator.finish()
                }
                else -> {
                    showMessage(it.message)
                }
            }
        }) { true }
    }

    override fun createLayout(): Int = R.layout.card_payment_werbview

    override fun inject(fragmentComponent: FragmentComponent) {
        fragmentComponent.inject(this)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun bindData() {
        if (progressDialog != null && progressDialog?.isShowing?.not()!!) {
            ThreadUtils.runOnUiThread {
                progressDialog?.show()
            }
        }
        toolbar.showToolbar(true)
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
                                            jsonObject.getString("ResponseCode") == "1"
                                        ) {

                                            val resultIntent = Intent()
                                            activity!!.setResult(Activity.RESULT_OK, resultIntent)
                                            navigator.finish()
                                            //homeViewModel.addMoneyToWallet(hashMapOf(Pair("amount", amount), Pair("transaction_id", transaction_id)))
                                        } else {

                                            addFireBaseLogs(
                                                "1011",
                                                "Card Payment Web View",
                                                jsonObject.getString("ReasonCodeDesc")
                                            )
                                            if (progressDialog != null && progressDialog?.isShowing!!) {
                                                progressDialog?.dismiss()
                                            }
                                            showMessage(jsonObject.getString("ReasonCodeDesc"))
                                            Handler().postDelayed({
                                                navigator.goBack()
                                            }, 500)
                                        }
                                    } catch (e: Exception) {

                                        e.printStackTrace()
                                        val resultIntent = Intent()
                                        activity!!.setResult(Activity.RESULT_OK, resultIntent)
                                        navigator.finish()
                                    }
                                } else {

                                    val resultIntent = Intent()
                                    activity!!.setResult(Activity.RESULT_OK, resultIntent)
                                    navigator.finish()
                                }
                            }
                        } catch (e: IOException) {

                            e.printStackTrace()
                            val resultIntent = Intent()
                            activity!!.setResult(Activity.RESULT_OK, resultIntent)
                            navigator.finish()
                        }
                    }
                } else if (url == "https://ecm.firstatlanticcommerce.com/SENTRY/PaymentGateway/Application/WFrmError.aspx") {

                    if (progressDialog != null && progressDialog?.isShowing!!) {
                        progressDialog?.dismiss()
                    }
                    showMessage("Something went wrong")
                    Handler().postDelayed({
                        navigator.goBack()
                    }, 500)
                    addFireBaseLogs("1015", "CArd Payment Wallet Web View", "WFrmError.aspx")
                }

            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {

                super.onReceivedError(view, request, error)
                addFireBaseLogs(
                    "1010",
                    "CArd Payment Web View",
                    "Display Error ${error.toString()} onReceivedError"
                )
                showMessage("Something went wrong")
                Handler().postDelayed({
                    navigator.goBack()
                }, 500)
            }
        }

        webView.post {
            card?.let {
                webView.loadData(it, "text/html", "UTF-8")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (progressDialog != null && progressDialog?.isShowing!!) {
            progressDialog!!.dismiss()
        }
    }
}