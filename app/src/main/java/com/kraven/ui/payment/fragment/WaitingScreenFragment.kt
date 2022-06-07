package com.kraven.ui.payment.fragment


import android.os.Bundle
import android.os.SystemClock
import com.bumptech.glide.Glide
import com.kraven.R
import com.kraven.di.component.FragmentComponent
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.track.fragment.TrackOrderFragment
import com.kraven.utils.ConstantApp

import kotlinx.android.synthetic.main.waiting_fragment.*


class WaitingScreenFragment : BaseFragment() {

    var beverageOnTheWay: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            beverageOnTheWay = arguments!!.getString(ConstantApp.PassValue.BEVERAGE_ON_THE_WAY)
        }
    }

    override fun createLayout(): Int = R.layout.waiting_fragment

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun bindData() {
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle("")
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)

        Glide.with(this)
                .load(R.raw.waiting)
                .into(imageViewGif)
        simpleChronometer.base = SystemClock.elapsedRealtime()
        simpleChronometer.start()
        simpleChronometer.setOnChronometerTickListener {
            val elapsedMillis = (SystemClock.elapsedRealtime() - it.base) / 1000
            if (elapsedMillis.toInt() == 30) {
                val bundle = Bundle()
                bundle.putString(ConstantApp.PassValue.BEVERAGE_ON_THE_WAY, beverageOnTheWay)
                navigator.load(TrackOrderFragment::class.java).setBundle(bundle).replace(true)
            }
        }

        textViewCancel.setOnClickListener {
            activity?.onBackPressed()
        }
    }
}