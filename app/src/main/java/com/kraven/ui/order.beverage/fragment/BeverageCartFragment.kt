package com.kraven.ui.order.beverage.fragment


import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.CompoundButton
import androidx.recyclerview.widget.LinearLayoutManager
import com.kraven.R
import com.kraven.core.AppPreferences
import com.kraven.di.component.FragmentComponent
import com.kraven.extensions.viewGone
import com.kraven.extensions.viewShow
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.cart.CartSubTotalEvent
import com.kraven.ui.cart.adapter.BeverageCartAdapter
import com.kraven.ui.cart.fragment.BaseFragmentVisibility
import com.kraven.ui.cart.fragment.SelectAddressFragment
import com.kraven.ui.home.model.DishesItem

import com.kraven.ui.order.beverage.model.Beverage
import com.kraven.ui.payment.fragment.PaymentFragment

import com.kraven.utils.ConstantApp
import kotlinx.android.synthetic.main.beverage_cart_fragment.*
import kotlinx.android.synthetic.main.common_cart_layout.*
import kotlinx.android.synthetic.main.layout_apply_code_tip.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class BeverageCartFragment : BaseFragmentVisibility(), View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private var cartList: ArrayList<Beverage>? = null
    private var beverageCartAdapter: BeverageCartAdapter? = null
    lateinit var textWatcher: TextWatcher

    @Inject
    lateinit var appPreferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            cartList = arguments!!.getParcelableArrayList(ConstantApp.PassValue.passItems)
        }
    }

    override fun createLayout(): Int = R.layout.beverage_cart_fragment


    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun bindData() {
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle("")
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)

        editTextBeverageDate.setOnClickListener(this)
        imageViewDeliveryInfo.setOnClickListener(this)
        imageViewCashInfo.setOnClickListener(this)
        textViewChange.setOnClickListener(this)
        setUpRecyclerView()
        loadLayout()
        initView()
    }

    private fun initView() {

        textViewAddressType.text = if (appPreferences.getString(ConstantApp.SaveValue.addressType).isEmpty()) "Home" else appPreferences.getString(ConstantApp.SaveValue.addressType)
        textViewAddress.text = if (appPreferences.getString(ConstantApp.SaveValue.displayAddress).isEmpty()) "51 Butternut Lane Harrisburg, IL 629546" else appPreferences.getString(ConstantApp.SaveValue.displayAddress)

        radioButtonDebitCard.setOnCheckedChangeListener(this)

        //radioButtonCashOn.setOnCheckedChangeListener(this)

        textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                if (!editTextWallet.text.toString().trim().startsWith("$") &&
                        !editTextWallet.text.toString().contains("$")) {
                    editTextWallet.setText(String.format(Locale.US,"$%s", editTextWallet.text))
                    editTextWallet.setSelection(editTextWallet.text!!.length)
                } else {
                    if (editTextWallet.text.toString().contains("$")) {
                        if (!editTextWallet.text.toString().trim().startsWith("$")) {
                            editTextWallet.setText(String.format(Locale.US,"$%s", editTextWallet.text.toString().replace("$", "")))
                        }
                    }
                }

                if (editTextWallet.text.toString().trim().length == 1) {
                    editTextWallet.removeTextChangedListener(textWatcher)
                    editTextWallet.setText("")
                    editTextWallet.addTextChangedListener(textWatcher)
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        }

        editTextWallet.addTextChangedListener(textWatcher)

    }


    private fun setUpRecyclerView() {
        recyclerViewCartList.layoutManager = LinearLayoutManager(activity)
        beverageCartAdapter = BeverageCartAdapter( object : BeverageCartAdapter.ItemClickListener {
            override fun onAdd(position: Int, selectedCartList: DishesItem?, cartSubTotalEvent: CartSubTotalEvent) {

            }

            override fun onSubtract(position: Int, selectedCartList: DishesItem?, cartSubTotalEvent: CartSubTotalEvent) {

            }

            override fun onDelete(item: DishesItem?, position: Int) {

            }

            override fun onMenusClick(position: Int, item: DishesItem?) {

            }



        })
        recyclerViewCartList.adapter = beverageCartAdapter

       // beverageCartAdapter!!.items = cartList
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView!!.id) {
            R.id.radioButtonCashOn -> {

                radioButtonDebitCard.isChecked = false
                //radioButtonCashOn.isChecked = isChecked

                linearLayoutTip.viewGone()
            }

            R.id.radioButtonDebitCard -> {
                //radioButtonCashOn.isChecked = false

                radioButtonDebitCard.isChecked = isChecked
                linearLayoutTip.viewShow()
            }
        }
    }


    override fun onClick(v: View?) {
        super.onClick(v!!)
        when (v?.id) {
            R.id.editTextBeverageDate -> {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)

                val dpd = DatePickerDialog(activity!!, DatePickerDialog.OnDateSetListener { _, year, _, _ ->
                    val calendar = Calendar.getInstance()
                    calendar.set(year, month, day)
                    val format = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
                    editTextBeverageDate.setText(format.format(calendar.time))
                }, year, month, day)
                dpd.datePicker.minDate = System.currentTimeMillis() - 1000
                dpd.show()
            }
            R.id.imageViewDeliveryInfo -> {
                session.userSettings?.bevDeliveryChargeBtnInfo?.let {
                    commandDialog(getString(R.string.delivery_charge_info), it, object : BaseFragment.DialogInterface {
                        override fun onClickOk() {

                        }
                    })
                }
            }
            R.id.imageViewCashInfo -> {
                commandDialog(getString(R.string.cash_on_delivery_info), getString(R.string.cash_delivery_text), object : BaseFragment.DialogInterface {
                    override fun onClickOk() {

                    }
                })
            }
            R.id.buttonCheckout -> {
                if (radioButtonDebitCard.isChecked ) {
                    val bundle = Bundle()
                    bundle.putString(ConstantApp.PassValue.BEVERAGE_ON_THE_WAY,"BEVERAGE_ON_THE_WAY")
                    navigator.load(PaymentFragment::class.java).setBundle(bundle).replace(true)
                } else {
                    showMessage(getString(R.string.please_select_payment_method))
                }
            }
            R.id.textViewChange -> {
                navigator.load(SelectAddressFragment::class.java).replace(true)
            }


        }

    }
}