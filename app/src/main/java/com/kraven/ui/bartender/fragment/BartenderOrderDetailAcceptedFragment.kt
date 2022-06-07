package com.kraven.ui.bartender.fragment

import android.view.Menu
import android.view.MenuInflater
import android.view.View

import androidx.appcompat.widget.AppCompatTextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.kraven.R
import com.kraven.di.component.FragmentComponent
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.track.fragment.TrackOrderFragment
import kotlinx.android.synthetic.main.bartender_order_details_accepted_fragment.*


class BartenderOrderDetailAcceptedFragment : BaseFragment(), View.OnClickListener {

    var textViewStatus: AppCompatTextView? = null

    override fun createLayout(): Int = R.layout.bartender_order_details_accepted_fragment

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun bindData() {
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle(getString(R.string.my_order_details))
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)

        setHasOptionsMenu(true)
        buttonTrack.setOnClickListener(this)
        buttonCancel.setOnClickListener(this)
        Glide.with(this.activity!!)
                .load("https://pixel.nymag.com/imgs/daily/vulture/2017/06/14/14-tom-cruise.w700.h700.jpg")
                /*.apply(RequestOptions().placeholder(R.drawable.ic_user))*/
                .apply(RequestOptions.bitmapTransform(MultiTransformation(CenterCrop(), RoundedCorners(15))))
                .into(imageViewDriver)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater!!.inflate(R.menu.menu_status, menu)

        textViewStatus = menu!!.findItem(R.id.notification).actionView.findViewById(R.id.textViewDetailsStatus) as AppCompatTextView
        textViewStatus!!.text = getString(R.string.status_accepted)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.buttonTrack -> {
                navigator.load(TrackOrderFragment::class.java).replace(true)
            }
            R.id.buttonCancel -> {
                activity!!.onBackPressed()
            }
        }
    }
}