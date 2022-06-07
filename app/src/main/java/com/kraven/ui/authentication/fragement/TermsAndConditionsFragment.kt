package com.kraven.ui.authentication.fragement

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.kraven.R
import com.kraven.di.component.FragmentComponent
import com.kraven.ui.base.BaseFragment
import com.kraven.utils.ConstantApp
import kotlinx.android.synthetic.main.web_view_fragment.*


class TermsAndConditionsFragment : BaseFragment() {

    var header: String? = null
    var link: String? = null

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    @SuppressLint("SetJavaScriptEnabled")
    override fun createLayout(): Int = R.layout.web_view_fragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            header = arguments?.getString(ConstantApp.PassValue.TermsHeder)
            link = arguments?.getString(ConstantApp.PassValue.TermsLink)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun bindData() {
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle(header!!)
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)


        webView.webViewClient = MyBrowser()
        webView.settings.loadsImagesAutomatically = true
        webView.settings.javaScriptEnabled = true
        webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        webView.post {
            link?.let { webView.loadUrl(it) }
        }
    }

    private inner class MyBrowser : WebViewClient() {
        /*override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }*/
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            view?.loadUrl(request?.url.toString())
            return true
        }
    }
}