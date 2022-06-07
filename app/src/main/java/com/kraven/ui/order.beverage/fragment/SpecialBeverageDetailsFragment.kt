package com.kraven.ui.order.beverage.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.ViewModelProviders
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager
import com.kraven.R
import com.kraven.core.StatusCode
import com.kraven.data.pojo.Parameters
import com.kraven.di.component.FragmentComponent
import com.kraven.extensions.*
import com.kraven.ui.activity.HomeActivity
import com.kraven.ui.activity.IsolatedActivity
import com.kraven.ui.cart.adapter.TipAdapter
import com.kraven.ui.cart.fragment.CompleteOrderCartFragment
import com.kraven.ui.cart.fragment.SelectAddressFragment
import com.kraven.ui.cart.viewModel.CartViewModel
import com.kraven.ui.home.viewmodel.HomeViewModel
import com.kraven.ui.order.beverage.SpecialBeverageViewModel
import com.kraven.ui.order.beverage.adapter.AdapterSpecialBeverageDetails
import com.kraven.ui.order.beverage.model.SpecialBeverageDetails
import com.kraven.ui.payment.fragment.PaymentFragment
import com.kraven.utils.ConstantApp
import com.kraven.utils.Formatter
import kotlinx.android.synthetic.main.common_cart_layout.*
import kotlinx.android.synthetic.main.common_payment_method.*
import kotlinx.android.synthetic.main.layout_apply_code_tip.*
import kotlinx.android.synthetic.main.special_beverage_order_details.*

class SpecialBeverageDetailsFragment : BaseSpecialBeverageDetailsFragment(), View.OnClickListener {


    private val homeViewModel by lazy { ViewModelProviders.of(this, viewModelFactory)[HomeViewModel::class.java] }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        specialBeverageViewModel = ViewModelProviders.of(this, viewModelFactory)[SpecialBeverageViewModel::class.java]
        cartViewModel = ViewModelProviders.of(this, viewModelFactory)[CartViewModel::class.java]
        registerObserver()
    }

    private fun registerObserver() {
        specialBeverageViewModel.getSpecialBeverageOrderDetails.observe(this, {
            showLoader(false)
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    it.data?.let { it1 -> setData(it1) }
                }
                else -> showMessage(it.message)
            }
        })


        cartViewModel.addMoneyToWallet.observe(this,
                {
                    showLoader(false)
                    when (it.code) {
                        StatusCode.CODE_SUCCESS -> {
                            walletAmount = getTextReplaceString(editTextWallet).toFloat()
                            cartViewModel.getUser(session.user?.id.toString())

                        }
                        else -> showMessage(it.message)
                    }
                }) { true }

        cartViewModel.checkPromocode.observe(this, {
            showLoader(false)
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    //if(it.data?.type=="Amount")
                    imageViewCancelPromocode.visibility = View.VISIBLE
                    editTextPromoCode.setText(it.data!!.promocode)
                    editTextPromoCode.clearFocus()
                    promoCode = it.data
                    setCalculatedData()
                    editTextPromoCode.clearFocus()
                }
                else -> {
                    imageViewCancelPromocode.visibility = View.GONE
                    editTextPromoCode.setText("")
                    editTextPromoCode.clearFocus()
                    showMessage(it.message)
                }
            }
        })


        cartViewModel.getUser.observe(this, {
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    session.user = it.data
                    if (walletAmount != 0.00f) {
                        editTextWallet.isEnabled = false
                        imageViewCancelWallet.viewShow()
                        editTextWallet.setText(convertTwoDigit(calculateTotal()))
                        editTextWallet.clearFocus()
                        editTextPromoCode.clearFocus()
                        showLoader(false)
                        setCalculatedData()
                    }
                }

                else -> {
                }
            }
        })

        specialBeverageViewModel.cancelSpecialBeverageOrder.observe(this, {
            showLoader(false)
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    navigator.loadActivity(HomeActivity::class.java).byFinishingAll().start()
                }
                else -> showMessage(it.message)
            }

        })

        homeViewModel.checkToolFeeLocationLiveData.observe(this, {
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    tollFee = it.data
                    setCalculatedData()

                }
                else -> showMessage(it.message)
            }
        })

        specialBeverageViewModel.paySpecialBeverageOrder.observe(this, {
            showLoader(false)
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    if (getTextReplace(textViewTotal) == 0.0F) {
                        navigator.load(CompleteOrderCartFragment::class.java).setBundle(Bundle().apply {
                            putString("name", getString(R.string.app_name))
                            putBoolean("isWallet", true)
                        }).replace(false)
                    } else {
                        navigator.load(CompleteOrderCartFragment::class.java).setBundle(Bundle().apply {
                            putString("name", getString(R.string.app_name))
                        }).replace(false)
                    }
                }
                else -> showMessage(it.message)
            }
        })

    }

    override fun createLayout(): Int = R.layout.special_beverage_order_details

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_status, menu)
        val textViewDone = menu.findItem(R.id.notification).actionView.findViewById(R.id.linearLayoutStatus) as LinearLayout
        textViewStatus = menu.findItem(R.id.notification).actionView.findViewById(R.id.textViewDetailsStatus) as AppCompatTextView
        if (details != null) {

            when (details!!.status) {
                "Payment Processing" -> {
                    textViewStatus?.text = details!!.status
                    textViewStatus?.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen._9ssp))
                }
                "Payment Failed" -> {
                    textViewStatus?.text = details!!.status
                    textViewStatus?.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen._10ssp))
                }
                else -> {
                    textViewStatus?.text = details!!.status
                }
            }
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun bindData() {
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle(getString(R.string.my_order_details))
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)

        setHasOptionsMenu(true)
        showLoader(true)
        buttonCheckout.viewGone()
        setUpRecyclerView()
        specialBeverageViewModel.getSpecialBeverageOrderDetails(Parameters(orderId = orderId))

        setOnClickListener()
    }

    private fun setOnClickListener() {
        textViewChange.setOnClickListener(this)
        imageViewCancelPromocode.setOnClickListener(this)
        imageViewCancelWallet.setOnClickListener(this)
        llCashOn.setOnClickListener(this)
        llTip.setOnClickListener(this)
        imageViewCloseTip.setOnClickListener(this)
        buttonDoneTip.setOnClickListener(this)
        buttonTip.setOnClickListener(this)
        imageViewDeliveryInfo.setOnClickListener(this)

        buttonCancel.setOnClickListener(this)
        buttonPay.setOnClickListener(this)
    }

    private fun setUpRecyclerView() {
        adapterSpecialBeverageDetails = AdapterSpecialBeverageDetails()
        recyclerViewSpecialBeverage.adapter = adapterSpecialBeverageDetails

        recyclerViewTip.layoutManager = ChipsLayoutManager.newBuilder(context)
                .setOrientation(ChipsLayoutManager.HORIZONTAL)
                .setChildGravity(Gravity.CENTER)
                .setRowStrategy(ChipsLayoutManager.STRATEGY_FILL_VIEW)
                .build()

        tipAdapter = TipAdapter(tipList() as ArrayList<String>, object : TipAdapter.OnSelectTip {
            override fun onSelectTip(selectTip: String, isEnter: Boolean) {
                this@SpecialBeverageDetailsFragment.isEnter = isEnter
                selectTips = selectTip
            }

        })
        recyclerViewTip.adapter = tipAdapter
    }

    private fun setData(details: SpecialBeverageDetails) {
        this@SpecialBeverageDetailsFragment.details = details

        cartViewModel.getUser(session.user?.id.toString())
        textViewOrderId.text = getString(R.string.order_id, details.id)
        textViewDateTime.text = getString(R.string.date_time, Formatter.utcToLocal(details.deliveryDatetime!!,
                "yyyy-MM-dd HH-mm-ss", Formatter.DD_MMM_YYYY_hh_mm_aa))

        //Set RecyclerView Items
        adapterSpecialBeverageDetails?.items = details.items

        textViewAdminCommentText.text = details.adminNotes
        editTextWallet.hint = getString(R.string.wallet_edit_text, session.user?.wallet)
        textViewVat.text = getString(R.string.VAT, details.vatPercent?.toFloat())
        textViewTip.text = getString(R.string.tip, "0.00%")

        if (address != null) {
            textViewAddress.text = address?.address
            textViewAddressType.text = address?.addressType
            homeViewModel.checkToolFeeLocation(Parameters(
                    pickupLatitude = details.deliveryLatitude.toString(),
                    pickupLongitude = details.deliveryLongitude,
                    latitude = address?.latitude.toString(),
                    longitude = address?.longitude.toString()))
        } else {
            textViewAddress.text = details.deliveryAddress
            textViewAddressType.text = details.addressType
        }

        groupTollFee.viewGone()
        /*if (details.tollFee != null && details.tollFee != "0") {
            groupTollFee.viewShow()
            textViewTollFeeValue.text = convertTwoDigit(tollFee?.tollfee?.toFloat() ?: 0.0f)
        } else {
            groupTollFee.viewGone()
        }*/


        radioButtonDebitCard.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                linearLayoutTip.visibility = View.VISIBLE
            } else {
                linearLayoutTip.visibility = View.GONE
            }
        }

        editTextPromoCode.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyBoard()
                editTextPromoCode.clearFocus()
                if (editTextPromoCode.text.toString().isNotEmpty()) {
                    //editTextWallet.isEnabled = false
                    showLoader(true)
                    details.id?.toInt()?.let { cartViewModel.checkPromocode(getTextReplace(textViewSubTotalValue).toString(), editTextPromoCode.text.toString(), "Food", it, ConstantApp.PassValue.ORDER_BEVERAGE) }
                }
                true
            } else {
                false
            }
        }

        editTextWallet.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyBoard()
                editTextWallet.clearFocus()
                //walletAmount = editTextWallet.text.toString().replace("$", "").toFloat()

                if (editTextWallet.text.toString().isNotEmpty()) {
                    val userEnteredWalletAmount = getTextReplaceString(editTextWallet).toFloat()
                    val currentWalletAmount = session.user?.wallet
                    walletAmount = userEnteredWalletAmount
                    editTextWallet.setText(convertTwoDigit(walletAmount))
                    editTextWallet.isEnabled = false
                    imageViewCancelWallet.viewShow()
                    showLoader(false)
                    setCalculatedData()
                    /*if (differenceAmount.toFloat() > 0) {
                        commanDialogYesNoNew("You don't have enough in your wallet", "Continue to add $$differenceAmount to wallet",
                                "Add", "Cancel", object : DialogInterfaceYesNo {
                            override fun onClickYes() {
                                navigator.loadActivity(IsolatedActivity::class.java).setPage(PaymentCartAddFragment::class.java).addBundle(
                                        Bundle().apply {
                                            putFloat("amount", differenceAmount.toFloat())
                                        }
                                ).forResult(103).start()
                            }

                            override fun onClickNo() {
                                if (currentWalletAmount != 0F) {
                                    if (currentWalletAmount > userEnteredWalletAmount) {
                                        walletAmount = calculateTotal()
                                        editTextWallet.setText(convertTwoDigit(walletAmount))
                                    } else {
                                        walletAmount = currentWalletAmount
                                        editTextWallet.setText(convertTwoDigit(walletAmount))
                                    }

                                    editTextWallet.isEnabled = false
                                    imageViewCancelWallet.viewShow()
                                    showLoader(false)
                                    setCalculatedData()
                                } else {
                                    editTextWallet.clearText()
                                    editTextWallet.hint = getString(R.string.wallet_edit_text, session.user?.wallet)

                                }
                            }
                        })
                    } else {
                        walletAmount = userEnteredWalletAmount
                        editTextWallet.setText(convertTwoDigit(walletAmount))
                        editTextWallet.isEnabled = false
                        imageViewCancelWallet.viewShow()
                        showLoader(false)
                        setCalculatedData()
                    }*/
                }
                true
            } else {
                false
            }
        }

        when {
            details.status == ConstantApp.OrderStatus.PENDING -> {
                textViewStatus?.text = details.status
                includeCode.viewGone()
                commonCartLayout.viewGone()
                buttonPay.viewGone()
                groupAddressVisibiliity.viewGone()
                textViewChange.viewGone()
            }
            details.status == ConstantApp.OrderStatus.ACCEPTED -> {
                textViewStatus?.text = details.status
                includeCode.viewShow()
                groupPaymentVisible.viewShow()
                commonPaymentMethod.viewGone()
                buttonCancel.viewShow()
                buttonPay.viewShow()
                setCalculatedData()
            }
            details.status == ConstantApp.OrderStatus.REJECTED ||
                    details.status == ConstantApp.OrderStatus.CANCELLED ||
                    details.status == "Payment Failed"
            -> {

                when (details.status) {
                    "Payment Failed" -> {
                        textViewStatus?.text = details.status
                        textViewStatus?.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen._10ssp))
                    }
                    else -> {
                        textViewStatus?.text = details.status
                    }
                }
                includeCode.viewGone()
                commonCartLayout.viewGone()
                buttonPay.viewGone()
                buttonCancel.viewGone()
                textViewChange.viewGone()

                textViewCancelBy.viewShow()
                textViewCancelReason.viewShow()

                textViewCancelBy.text = getString(R.string.cancel_by, details.cancelBy)
                textViewCancelReason.text = getString(R.string.cancel_reason, details.cancelReason)
            }
            /*details.status == ConstantApp.OrderStatus.CANCELLED -> {
                textViewCancelBy.viewShow()
                textViewCancelReason.viewShow()
                textViewCancelBy.text = getString(R.string.cancelled_by, details.cancelBy)
                textViewCancelReason.text = getString(R.string.cancel_reason, details.cancelReason)
                textViewStatus?.text = details.status
                setPaymentMethodData(details)
                setData()
            }*/
            details.status == ConstantApp.OrderStatus.COMPLETED -> {
                textViewStatus?.text = details.status
                setPaymentMethodData(details)
                setData()
                linearLayoutTip.viewGone()
            }
            details.status == ConstantApp.OrderStatus.CONFIRMED ||
                    details.status == "Payment Processing" -> {
                when (details.status) {
                    "Payment Processing" -> {
                        textViewStatus?.text = details.status
                        textViewStatus?.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen._9ssp))
                    }
                    else -> {
                        textViewStatus?.text = details.status
                    }
                }
                textViewStatus?.text = details.status
                setPaymentMethodData(details)
                setData()
                linearLayoutTip.viewGone()
            }
        }
    }

    private fun setData() {
        textViewTip.text = getString(R.string.tip, details?.tipPercent?.toFloat()?.let { twoDigit(it) } + "%")
        textViewSubTotalValue.text = details?.subTotal?.toFloat()?.let { convertTwoDigit(it) }
        textViewVatValue.text = details?.totalVat?.toFloat()?.let { convertTwoDigit(it) }
        textViewDeliveryChargeValue.text = details?.deliveryCharge?.toFloat()?.let { details?.deliveryCommission?.toFloat()?.let { it1 -> it.plus(it1) }?.let { it2 -> convertTwoDigit(it2) } }

        textViewDiscountValue.text = details?.discount?.toFloat()?.let { convertTwoDigit(it) }
        textViewWalletValue.text = details?.wallet?.toFloat()?.let { convertTwoDigit(it) }
        textViewTipValue.text = details?.tip?.toFloat()?.let { convertTwoDigit(it) }
        textViewTotal.text = details?.total?.toFloat()?.let { convertTwoDigit(it) }

        /*  if (details?.tollFee != null && details?.tollFee != "0") {
              groupTollFee.viewShow()*/
        textViewTollFeeValue.text = details?.tollFee?.toFloat()?.let { convertTwoDigit(it) }
        //}
    }

    private fun setPaymentMethodData(details: SpecialBeverageDetails) {
        groupPaymentVisible.viewGone()
        commonPaymentMethod.viewShow()
        includeCode.viewGone()
        buttonCancel.viewGone()
        buttonPay.viewGone()
        textViewChange.viewGone()


        textViewPaymentModeValue.text = getString(R.string.payment_mode_value,
                details.total.toString(), convertTwoDigit(details.wallet!!.toFloat()))

        if (details.paymentMethod == "Card" && details.cardToken != null && details.cardToken?.isNotEmpty()!!) {
            //groupPaymentVisible.viewShow()
            val lastFour = details.cardToken?.substring(details.cardToken?.length!! - 4, details.cardToken?.length!!)
            textViewCardNumberValue.text = "xxxxx xxxx xxxx ".plus(lastFour)
            textViewTransactionValue.text = details.transactionId
        } else {
            //groupPaymentVisible.viewGone()
            /* textViewCardNumberValue.text = "-"
             textViewTransactionValue.text = "-"*/

            textViewCardNumberValue.text = "-"
            textViewTransactionValue.text = details.transactionId
        }

        //textViewCardNumberValue.text = if (details.cardToken!!.isNotEmpty()) getLastFourNumber(details.cardToken!!) else "-"
        //textViewTransactionValue.text = if (details.transactionId!!.isNotEmpty()) details.transactionId else "-"
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.imageViewDeliveryInfo -> {
                commandDialog(getString(R.string.delivery_charge_info), session.userSettings?.bevDeliveryChargeBtnInfo!!, object : DialogInterface {
                    override fun onClickOk() {

                    }
                })
            }

            R.id.buttonCancel -> {
                commanDialogYesNoNew(getString(R.string.app_name),
                        "Are you sure you want to cancel this order?", "Yes", "No", object : DialogInterfaceYesNo {
                    override fun onClickYes() {
                        showLoader(true)
                        specialBeverageViewModel.cancelSpecialBeverageOrder(Parameters(orderId = details?.id))
                    }

                    override fun onClickNo() {
                    }

                })
            }
            R.id.buttonPay -> {
                when (address) {
                    null -> showMessage("Please select address")

                    //radioGroupPaymentMethod.checkedRadioButtonId == -1 -> showMessage("Please select payment method")
                    else -> placeOrder()
                }
            }

            R.id.textViewChange -> {
                val b = Bundle()
                b.putString(ConstantApp.PassValue.RESTAURANT_ID, details?.id.toString())
                b.putString(ConstantApp.PassValue.ORDER_FOOD, ConstantApp.PassValue.ORDER_BEVERAGE_SPECIAL)
                navigator.loadActivity(IsolatedActivity::class.java)
                        .setPage(SelectAddressFragment::class.java).forResult(102).addBundle(b).start()
            }
            R.id.imageViewCancelWallet -> {
                editTextWallet.isEnabled = true
                walletAmount = 0.00F
                imageViewCancelWallet.visibility = View.GONE
                editTextWallet.clearText()
                editTextWallet.hint = getString(R.string.wallet_edit_text, session.user?.wallet)
                setCalculatedData()
            }

            R.id.imageViewCancelPromocode -> {
                editTextWallet.isEnabled = true
                editTextPromoCode.clearText()
                imageViewCancelPromocode.visibility = View.GONE
                promoCode = null
                setCalculatedData()
            }
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
                selectTips = null
                buttonTip.isEnabled = true
                llTip.isEnabled = true
                buttonTip.text = getString(R.string.add_tip)
                imageViewCloseTip.visibility = View.GONE
                textViewTip.text = getString(R.string.tip, "0.00%")
                setCalculatedData()
            }

            R.id.buttonDoneTip -> {
                hideKeyBoard()
                if (selectTips != null && !TextUtils.isEmpty(selectTips)) {
                    llEnterTip.visibility = View.GONE
                    llTip.visibility = View.VISIBLE
                    setCalculatedData()
                } else {
                    Toast.makeText(activity, getString(R.string.select_tip), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun placeOrder() {
        val parameters = HashMap<String, Any>()
        if (details != null) {
            parameters["order_id"] = details?.id.toString()

            parameters["address_id"] = address?.id!!
            /*if (address?.buildingName == "" && address?.landmark == "") {
                parameters["address_landmark"] = address?.house!!
            } else if (address?.buildingName == "" && address?.landmark != "") {
                parameters["address_landmark"] = address?.house!! + "," + address?.landmark
            } else if (address?.buildingName != "" && address?.landmark == "") {
                parameters["address_landmark"] = address?.house!! + "," + address?.buildingName
            } else {
                parameters["address_landmark"] = address?.house!! + "," + address?.buildingName + "," + address?.landmark
            }
*/
            if (tollFee?.tollfee != null) {
                parameters["tollfee"] = tollFee?.tollfee.toString()
            }


            parameters["sub_total"] = details?.subTotal.toString()
            parameters["discount"] = getTextReplace(textViewDiscountValue).toString()
            parameters["total"] = getTextReplace(textViewTotal).toString()
            // parameters["wallet"] = getTextReplace(textViewWalletValue).toString()
            parameters["tip"] = getTextReplace(textViewTipValue).toString()
            if (isEnter != null) {
                parameters["tip_percent"] = if (isEnter!!) "0" else selectTips!!
            }

            if (promoCode != null) {
                parameters["promocode"] = editTextPromoCode.text.toString().trim()
            }

            if (getTextReplace(textViewTotal) == 0.0F && session.user?.wallet!! >= calculateTotal()) {
                parameters["wallet"] = getTextReplace(textViewWalletValue).toString()
                parameters["payment_method"] = "Wallet"
                parameters["actual_wallet"] = getTextReplace(textViewWalletValue).toString()
                showLoader(true)
                specialBeverageViewModel.paySpecialBeverageOrder(parameters)
            } else {
                val userEnteredWalletAmount = if (getTextReplaceString(editTextWallet).isNotEmpty()) getTextReplaceString(editTextWallet).toFloat() else 0.0f
                val currentWalletAmount = session.user?.wallet
                val differenceAmount = twoDigit(userEnteredWalletAmount - currentWalletAmount!!)
                when {
                    getTextReplace(textViewTotal) == 0.0F -> {
                        parameters["wallet"] = calculateTotal()
                        parameters["actual_wallet"] = walletAmount
                    }
                    getTextReplaceString(editTextWallet).isNotEmpty() && differenceAmount.toFloat() > 0 -> {
                        parameters["wallet"] = walletAmount
                        parameters["actual_wallet"] = walletAmount
                    }
                    else -> {
                        parameters["wallet"] = getTextReplace(textViewWalletValue).toString()
                    }
                }
                parameters["payment_method"] = "Card"

                val bundle = Bundle()
                bundle.putSerializable("CART", parameters)
                bundle.putString(ConstantApp.PassValue.ORDER_FOOD, ConstantApp.PassValue.ORDER_BEVERAGE_SPECIAL)
                bundle.putString("restaurantname", getString(R.string.app_name))
                navigator.load(PaymentFragment::class.java).setBundle(bundle).replace(true)
            }
        } else {
            showMessage("Something went wrong!")
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 102) {
                Log.d("JSR", "onAcitivityResult - 1")
                address = data?.getParcelableExtra("address")
                Log.d("JSR", "onAcitivityResult - 2")
                if (address != null) {
                    Log.d("JSR", "onAcitivityResult - 3")
                    homeViewModel.checkToolFeeLocation(Parameters(
                            pickupLatitude = details?.deliveryLatitude.toString(),
                            pickupLongitude = details?.deliveryLongitude,
                            latitude = address?.latitude.toString(),
                            longitude = address?.longitude.toString()))
                    Log.d("JSR", "onAcitivityResult - 4")
                    Log.d("JSR", "onAcitivityResult - 5")
                    textViewAddressType.text = address?.addressType
                    Log.d("JSR", "onAcitivityResult - 6")
                    textViewAddress.text = address?.address
                    Log.d("JSR", "onAcitivityResult - 7")
                }
            } else if (requestCode == 103) {
                val isCard = data?.getBooleanExtra("directcard", false)
                if (isCard!!) {
                    showLoader(true)
                    val parameters = HashMap<String, Any>()
                    parameters["card_token"] = data.getStringExtra("cardtoken")!!
                    parameters["amount"] = data.getStringExtra("amount")!!
                    cartViewModel.addMoneyToWallet(parameters)
                } else {
                    showLoader(true)
                    val parameters = HashMap<String, Any>()
                    parameters["amount"] = data.getStringExtra("amount")!!
                    parameters["transaction_id"] = data.getStringExtra("transactionid")!!
                    cartViewModel.addMoneyToWallet(parameters)
                }
            }
        }
    }


}