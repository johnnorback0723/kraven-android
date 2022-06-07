package com.kraven.ui.portable.bar.rental.fragment

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.kraven.R
import com.kraven.di.component.FragmentComponent
import com.kraven.ui.base.BaseFragment

import com.kraven.ui.portable.bar.rental.model.PortableOrderList
import com.kraven.ui.track.fragment.TrackOrderFragment
import com.kraven.utils.ConstantApp
import kotlinx.android.synthetic.main.portable_order_details.*


class PortableOrderDetailsFragment : BaseFragment(), View.OnClickListener {

    var textViewStatus: AppCompatTextView? = null
    var portableOrderList: PortableOrderList? = null
    var orderStaus: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            portableOrderList = arguments!!.getParcelable(ConstantApp.PassValue.ORDER_DETAIL)
            orderStaus = arguments!!.getInt(ConstantApp.PassValue.STATUS)
        }
    }

    override fun createLayout(): Int = R.layout.portable_order_details

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun bindData() {
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle(getString(R.string.my_order))
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)

        buttonTrack.setOnClickListener(this)
        buttonCancel.setOnClickListener(this)
        setHasOptionsMenu(true)

        if (orderStaus == 0) {
            buttonTrack.visibility = View.GONE
            buttonCancel.text = getString(R.string.cancel)
        } else {
            buttonTrack.visibility = View.VISIBLE
            buttonCancel.text = getString(R.string.reorder)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_status, menu)
        textViewStatus = menu.findItem(R.id.notification).actionView.findViewById(R.id.textViewDetailsStatus) as AppCompatTextView
        if (portableOrderList!!.orderStatus.equals(getString(R.string.status_pending))) {
            textViewStatus!!.text = getString(R.string.status_pending)
            buttonTrack.visibility = View.GONE
        } else if (portableOrderList!!.orderStatus.equals(getString(R.string.status_confirmed))) {
            textViewStatus!!.text = getString(R.string.status_confirmed)
            buttonTrack.visibility = View.VISIBLE
        }
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