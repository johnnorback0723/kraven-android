package com.kraven.ui.cart.fragment

import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.Toast
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager
import com.kraven.R
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.cart.adapter.TipAdapter
import kotlinx.android.synthetic.main.common_cart_layout.*
import java.util.*
import kotlin.collections.ArrayList


abstract class BaseFragmentVisibility : BaseFragment(), View.OnClickListener {
    var selectTips: String? = null
    var tipAdapter: TipAdapter? = null
    var isEnter: Boolean? = null

    fun loadLayout() {
        llTip.setOnClickListener(this)
        imageViewCloseTip.setOnClickListener(this)
        buttonDoneTip.setOnClickListener(this)
        buttonTip.setOnClickListener(this)


        recyclerViewTip.layoutManager = ChipsLayoutManager.newBuilder(context)
                .setOrientation(ChipsLayoutManager.HORIZONTAL)
                .setChildGravity(Gravity.CENTER)
                .setRowStrategy(ChipsLayoutManager.STRATEGY_FILL_VIEW)
                .build()

        tipAdapter = TipAdapter(tipList(), object : TipAdapter.OnSelectTip {
            override fun onSelectTip(selectTip: String, isEnter: Boolean) {
                this@BaseFragmentVisibility.isEnter = isEnter
                selectTips = selectTip
            }

        })
        recyclerViewTip.adapter = tipAdapter
    }

    private fun tipList(): ArrayList<String> {

        val menuLists = ArrayList<String>()
        menuLists.add("5")
        menuLists.add("10")
        menuLists.add("15")
        menuLists.add("20")
        menuLists.add("30")

        return menuLists

    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.llTip -> {
                tipAdapter!!.setPosition()
                selectTips = ""
                llTip.visibility = View.GONE
                llEnterTip.visibility = View.VISIBLE


            }
            R.id.buttonTip -> {
                tipAdapter!!.setPosition()
                selectTips = ""
                llTip.visibility = View.GONE
                llEnterTip.visibility = View.VISIBLE
            }
            R.id.imageViewCloseTip -> {
                selectTips = ""
                buttonTip.isEnabled = true
                llTip.isEnabled = true
                buttonTip.text = getString(R.string.add_tip)
                imageViewCloseTip.visibility = View.GONE
            }
            R.id.buttonDoneTip -> {
                hideKeyBoard()
                if (selectTips != null && !TextUtils.isEmpty(selectTips)) {
                    llEnterTip.visibility = View.GONE
                    llTip.visibility = View.VISIBLE
                    if (isEnter!!) {
                        buttonTip.text = "$" + selectTips
                    } else {
                        buttonTip.text = String.format(Locale.US,"%s%%", selectTips)
                    }

                    imageViewCloseTip.visibility = View.VISIBLE
                    buttonTip.isEnabled = false
                    llTip.isEnabled = false
                } else {
                    Toast.makeText(activity, getString(R.string.select_tip), Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

}