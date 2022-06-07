package com.kraven.ui.bartender.fragment


import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.kraven.R
import com.kraven.di.component.FragmentComponent
import com.kraven.ui.base.BaseFragment
import kotlinx.android.synthetic.main.bartender_order_details_fragment.*



class BartenderOrderDetailPendingFragment : BaseFragment(), View.OnClickListener {

    var textViewStatus: AppCompatTextView? = null

    override fun createLayout(): Int = R.layout.bartender_order_details_fragment

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun bindData() {
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle(getString(R.string.my_order_details))
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)

        setHasOptionsMenu(true)
        buttonCancel.setOnClickListener(this)



    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater!!.inflate(R.menu.menu_status, menu)

        textViewStatus = menu!!.findItem(R.id.notification).actionView.findViewById(R.id.textViewDetailsStatus) as AppCompatTextView
        textViewStatus!!.text = getString(R.string.status_pending)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.buttonCancel -> {
                activity!!.onBackPressed()
            }
        }
    }
}