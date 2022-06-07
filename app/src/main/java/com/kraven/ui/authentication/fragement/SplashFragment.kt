package com.kraven.ui.authentication.fragement

import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.kraven.R
import com.kraven.core.StatusCode
import com.kraven.data.pojo.Parameters
import com.kraven.di.component.FragmentComponent
import com.kraven.extensions.getViewModel
import com.kraven.ui.activity.HomeActivity
import com.kraven.ui.authentication.viewmodel.AuthViewModel
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.home.viewmodel.HomeViewModel
import com.kraven.utils.ConstantApp


class SplashFragment : BaseFragment() {
    private var handler: Handler? = null
    var runnable: Runnable? = null
    var isFirst = true
    private lateinit var viewModel: HomeViewModel
    private val authViewModel by getViewModel<AuthViewModel>()


    override fun createLayout(): Int = R.layout.splash_fragment

    override fun inject(fragmentComponent: FragmentComponent) {
        fragmentComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)[HomeViewModel::class.java]

        authViewModel.getUser.observe(this, {
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    if (it.data?.islandId != null && it.data.islandId?.isNotEmpty()!! && it.data.islandId != "0") {
                        navigator.loadActivity(HomeActivity::class.java).byFinishingAll().start()
                    } else {
                        navigator.load(SelectIslandFragment::class.java).setBundle(bundleOf(ConstantApp.PassValue.NEW_ISLAND to true)).replace(false)
                    }
                }
                else -> {
                    showMessage(it.message)
                }
            }
        })
    }

    override fun bindData() {

        toolbar.showToolbar(false)
        handler = Handler()
        runnable = Runnable {
            animateView()
            handler = null
        }
        handler!!.removeCallbacks(runnable!!)
        if (isFirst)
            handler!!.postDelayed(runnable!!, 500)
        else {
            animateView()
            handler = null
        }
    }

    private fun animateView() {

        if (!isAdded) return
        session.isProtoType = false
        if (session.user != null && session.setAddressCode != StatusCode.CODE_ADD_ADDRESS.toString()) {
            val imageView: ImageView = activity!!.findViewById(R.id.imageViewSplashLoading)
            Glide.with(this).load(R.drawable.splash_loading).into(imageView)

            handler = Handler()
            runnable = Runnable {
                authViewModel.getUser(Parameters(userId = session.user?.id.toString()))
            }
            handler!!.postDelayed(runnable!!, 3000)

            //authViewModel.getUser(Parameters(userId = session.user?.id.toString()))

            /*if (session.user!!.islandId != null && session.user!!.islandId?.isNotEmpty()!! && session.user!!.islandId != "0") {
                navigator.loadActivity(HomeActivity::class.java).byFinishingAll().start()
            } else {
                authViewModel.getUser(Parameters(userId = session.user?.id.toString()))
                //navigator.load(SelectIslandFragment::class.java).setBundle(bundleOf(ConstantApp.PassValue.NEW_ISLAND to true)).replace(false)
            }*/
        } else {
            isFirst = false
            navigator.load(LoginFragment::class.java).replace(false)
        }
    }

    override fun onResume() {
        super.onResume()
        if (handler != null && runnable != null) {
            handler!!.removeCallbacks(runnable!!)
            handler!!.postDelayed(runnable!!, 2000)
        }
    }


    override fun onPause() {
        if (handler != null && runnable != null)
            handler!!.removeCallbacks(runnable!!)
        super.onPause()
    }
}