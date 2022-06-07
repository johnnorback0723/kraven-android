package com.kraven.ui.payment.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.kraven.R
import com.kraven.core.StatusCode
import com.kraven.core.Validator
import com.kraven.data.mvnsource.OrderCancelService
import com.kraven.data.mvnsource.UpdateNotifyStatus
import com.kraven.data.pojo.Parameters
import com.kraven.di.component.FragmentComponent
import com.kraven.exception.ApplicationException
import com.kraven.extensions.*
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.cart.viewModel.CartViewModel
import com.kraven.ui.order.beverage.SpecialBeverageViewModel
import com.kraven.ui.payment.adapter.CardListAdapter
import com.kraven.ui.payment.setting.model.Card
import com.kraven.ui.payment.viewmodel.PaymentViewModel
import com.kraven.utils.ConstantApp
import com.kraven.utils.Formatter
import kotlinx.android.synthetic.main.fragment_payment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import javax.inject.Inject


class PaymentFragment : BaseFragment() {

    var cardListAdapter: CardListAdapter? = null
    var transactionId: String? = null
    private lateinit var viewModel: PaymentViewModel
    private lateinit var specialBeverageViewModel: SpecialBeverageViewModel
    private val cartViewModel by lazy { ViewModelProviders.of(this, viewModelFactory)[CartViewModel::class.java] }

    private var lastPosition: Int = -1
    private var menuAdd: MenuItem? = null
    private var restaurantname: String? = null
    private var parameters: HashMap<String, Any>? = null
    private var hostedPageOrderId: Int = -1
    private var cardPageOrderId: Int = -1

    private lateinit var viewModelCart: CartViewModel

    override fun createLayout(): Int = R.layout.fragment_payment
    var orderPage: String? = null
    private var isCard = false

    //CVV Feature
    private var cardCVV: String? = null
    private var dialog: Dialog? = null

    @Inject
    lateinit var validator: Validator
    private var editTextCVV: TextInputEditText? = null

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            parameters = arguments!!.getSerializable("CART") as HashMap<String, Any>?
            restaurantname = arguments!!.getString("restaurantname")
            orderPage = arguments!!.getString(ConstantApp.PassValue.ORDER_FOOD)
        }

        viewModel = ViewModelProviders.of(this, viewModelFactory)[PaymentViewModel::class.java]
        viewModelCart = ViewModelProviders.of(this, viewModelFactory)[CartViewModel::class.java]
        specialBeverageViewModel = ViewModelProviders.of(this, viewModelFactory)[SpecialBeverageViewModel::class.java]

        viewModel.getCardList.observe(this, { responseBody ->
            showLoader(false)
            when (responseBody.code) {
                StatusCode.CODE_SUCCESS -> {

                    cardListAdapter!!.items = responseBody.data
                    if (responseBody.data?.size == 0) {
                        if (menuAdd != null) {
                            menuAdd?.isVisible = true
                        }
                    }
                }
                StatusCode.CODE_NO_DATA -> {
                    cardListAdapter!!.clearAllItem()
                    cardListAdapter!!.errorMessage = responseBody.message
                    if (menuAdd != null) {
                        menuAdd?.isVisible = true
                    }
                }
                else -> showMessage(responseBody.message)
            }
        }) { true }

        /*  viewModelCart.placeOrder.observe(this, {
              showLoader(false)
              when (it.code) {
                  StatusCode.CODE_SUCCESS -> {
                      session.cartModel = null
                      session.cartCount = "0"
                      navigator.load(CompleteOrderCartFragment::class.java).setBundle(Bundle().apply {
                          putString("name", restaurantname)
                      }).replace(false)
                  }
                  else -> showMessage(it.message)
              }
          }) { true }*/

        viewModel.hostedPagePayment.observe(this, {
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    showLoader(false)
                    Log.d("LZX", "before webpayment : $transactionId : ${it.data} : $parameters : $restaurantname : $orderPage : $hostedPageOrderId")
                    navigator.load(PaymentWebPageFragment::class.java).setBundle(Bundle().apply {
                                putString("transaction_id", transactionId)
                                putBoolean("isWallet", false)
                                putParcelable("card", it.data)
                                putSerializable("CART", parameters)
                                putString("restaurantname", restaurantname)
                                putString(ConstantApp.PassValue.ORDER_FOOD, orderPage)
                                putInt("hostedPageOrderId", hostedPageOrderId)
                            }).replace(true)
                }
                else -> {
                    showLoader(false)
                    showMessage(it.message)
                }
            }
        })

        viewModel.cardPayment.observe(this, {
            showLoader(false)
            if (dialog != null)
                dialog!!.dismiss()
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    navigator.load(CardPaymentWebView::class.java)
                            .setBundle(Bundle().apply {
                                putString("transaction_id", transactionId)
                                putString("card", it.data?.htmlPage)
                                putSerializable("CART", parameters)
                                putString("restaurantname", restaurantname)
                                putString(ConstantApp.PassValue.ORDER_FOOD, orderPage)
                                putInt("cardPaymentOrderId", cardPageOrderId)
                            }).replace(true)
                }
                else -> {
                    showMessage(it.message)
                }
            }
        })

        cartViewModel.beforeVerifyFoodOrderLiveData.observe(this, {
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    transactionId = transactionId(session.user?.id!!)
                    //transactionId = session.transactionIdCard
                    if (isCard) {
                        /* viewModel.cardPayment(hashMapOf(
                                 Pair("transaction_id", transactionId!!),
                                 Pair("card_token", cardListAdapter?.items?.get(lastPosition)?.cardToken.toString()),
                                 Pair("order_id", System.currentTimeMillis().toString().takeLast(4)),
                                 Pair("amount", twoDigit(parameters!!["total"].toString().toFloat()))

                         ))*/
                        parameters?.set("order_datetime", Formatter.format(Date().toString(), Formatter.YYYY_MM_DD_T_HH_MM_SSS_Z, Formatter.YYYY_MM_DD_HH_MM_SS, true).toString())
                        parameters!!["transaction_id"] = transactionId.toString()
                        if (orderPage == ConstantApp.PassValue.ORDER_FOOD || orderPage == ConstantApp.PassValue.ORDER_FOOD_FUTURE) {
                            viewModelCart.placeOrder(parameters!!)
                        } else if (orderPage == ConstantApp.PassValue.ORDER_BEVERAGE_SPECIAL) {
                            specialBeverageViewModel.paySpecialBeverageOrder(parameters!!)
                        } else {
                            viewModelCart.placeOrderBeverage(parameters!!)
                        }
                    } else {
                        /* viewModel.hostedPagePayment(hashMapOf(
                                 Pair("transaction_id", transactionId!!),
                                 Pair("amount", parameters!!["total"].toString()),
                                 Pair("capture", "0")
                         ))*/
                        parameters?.set("order_datetime", Formatter.format(Date().toString(), Formatter.YYYY_MM_DD_T_HH_MM_SSS_Z, Formatter.YYYY_MM_DD_HH_MM_SS, true).toString())
                        parameters!!["transaction_id"] = transactionId.toString()

                        if (orderPage == ConstantApp.PassValue.ORDER_FOOD || orderPage == ConstantApp.PassValue.ORDER_FOOD_FUTURE) {
                            viewModelCart.placeOrder(parameters!!)
                        } else if (orderPage == ConstantApp.PassValue.ORDER_BEVERAGE_SPECIAL) {
                            specialBeverageViewModel.paySpecialBeverageOrder(parameters!!)
                        } else {
                            viewModelCart.placeOrderBeverage(parameters!!)
                        }
                    }
                }
                else -> {
                    showLoader(false)
                    commandDialog(getString(R.string.app_name), it.message, object : DialogInterface {
                        override fun onClickOk() {

                        }
                    })
                }
            }
        })

        cartViewModel.beforeVerifyBeverageOrderLiveData.observe(this, {
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    transactionId = transactionId(session.user?.id!!)
                    //transactionId = session.transactionIdCard
                    if (isCard) {
                        /* viewModel.cardPayment(hashMapOf(
                                 Pair("transaction_id", transactionId!!),
                                 Pair("card_token", cardListAdapter?.items?.get(lastPosition)?.cardToken.toString()),
                                 Pair("order_id", System.currentTimeMillis().toString().takeLast(4)),
                                 Pair("amount", twoDigit(parameters!!["total"].toString().toFloat()))

                         ))*/
                        parameters?.set("order_datetime", Formatter.format(Date().toString(), Formatter.YYYY_MM_DD_T_HH_MM_SSS_Z, Formatter.YYYY_MM_DD_HH_MM_SS, true).toString())
                        parameters!!["transaction_id"] = transactionId.toString()
                        if (orderPage == ConstantApp.PassValue.ORDER_FOOD || orderPage == ConstantApp.PassValue.ORDER_FOOD_FUTURE) {
                            viewModelCart.placeOrder(parameters!!)
                        } else if (orderPage == ConstantApp.PassValue.ORDER_BEVERAGE_SPECIAL) {
                            specialBeverageViewModel.paySpecialBeverageOrder(parameters!!)
                        } else {
                            viewModelCart.placeOrderBeverage(parameters!!)
                        }
                    } else {
                        /*viewModel.hostedPagePayment(hashMapOf(
                                Pair("transaction_id", transactionId!!),
                                Pair("amount", parameters!!["total"].toString()),
                                Pair("capture", "0")
                        ))*/
                        parameters?.set("order_datetime", Formatter.format(Date().toString(), Formatter.YYYY_MM_DD_T_HH_MM_SSS_Z, Formatter.YYYY_MM_DD_HH_MM_SS, true).toString())
                        parameters!!["transaction_id"] = transactionId.toString()
                        if (orderPage == ConstantApp.PassValue.ORDER_FOOD || orderPage == ConstantApp.PassValue.ORDER_FOOD_FUTURE) {
                            viewModelCart.placeOrder(parameters!!)
                        } else if (orderPage == ConstantApp.PassValue.ORDER_BEVERAGE_SPECIAL) {
                            specialBeverageViewModel.paySpecialBeverageOrder(parameters!!)
                        } else {
                            viewModelCart.placeOrderBeverage(parameters!!)
                        }
                    }
                }
                else -> {
                    showLoader(false)
                    commandDialog(getString(R.string.app_name), it.message, object : DialogInterface {
                        override fun onClickOk() {

                        }
                    })
                }
            }
        })

        viewModelCart.placeOrder.observe(this, {
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    /* session.cartModel = null
                     session.cartCount = "0"*/
                    /* navigator.load(CompleteOrderCartFragment::class.java).setBundle(Bundle().apply {
                         putString("name", restaurantname)
                     }).replace(false)*/
                    if (isCard) {
                        var total = ""
                        cardPageOrderId = if (it.data != null) it.data.orderId!! else -1
                        total = if (parameters!!["actual_wallet"] != null && parameters!!["actual_wallet"].toString().toFloat() != 0F) {
                            if (parameters!!["total"].toString().toFloat() != 0F) {
                                val finalAmount = parameters!!["actual_wallet"].toString().toFloat() + parameters!!["total"].toString().toFloat()
                                twoDigit(finalAmount.minus(session.user?.wallet!!))
                            } else {
                                twoDigit(parameters!!["actual_wallet"].toString().toFloat().minus(session.user?.wallet!!))
                            }
                        } else {
                            twoDigit(parameters!!["total"].toString().toFloat())
                        }
                        viewModel.cardPayment(hashMapOf(
                                Pair("transaction_id", transactionId!!),
                                Pair("card_token", cardListAdapter?.items?.get(lastPosition)?.cardToken.toString()),
                                Pair("order_id", System.currentTimeMillis().toString().takeLast(4)),
                                Pair("amount", total),
                                Pair("cvv", cardCVV!!)
                        ))
                    } else {
                        hostedPageOrderId = if (it.data != null) it.data.orderId!! else -1
                        var total = ""
                        total = if (parameters!!["actual_wallet"] != null && parameters!!["actual_wallet"].toString().toFloat() != 0F) {
                            if (parameters!!["total"].toString().toFloat() != 0F) {
                                val finalAmount = parameters!!["actual_wallet"].toString().toFloat() + parameters!!["total"].toString().toFloat()
                                twoDigit(finalAmount.minus(session.user?.wallet!!))
                            } else {
                                twoDigit(parameters!!["actual_wallet"].toString().toFloat().minus(session.user?.wallet!!))
                            }
                        } else {
                            twoDigit(parameters!!["total"].toString().toFloat())
                        }
                        viewModel.hostedPagePayment(hashMapOf(
                                Pair("transaction_id", transactionId!!),
                                Pair("amount", total),
                                Pair("capture", "0")
                        ))
                    }
                }
                StatusCode.CODE_INVALID_REQUEST -> {
                    showMessage(it.message)
                    //navigator.loadActivity(HomeActivity::class.java).byFinishingAll().start()
                }
                else -> {
                    //    addFireBaseLogs("1013", "Payment Web View Fragment  Restaurant or Beverage - $transactionId", it.message)
                    showMessage(it.message)
                }
            }
        }) { true }

        specialBeverageViewModel.paySpecialBeverageOrder.observe(this, {
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    /* navigator.load(CompleteOrderCartFragment::class.java).setBundle(Bundle().apply {
                         putString("name", restaurantname)
                     }).replace(false)*/
                    if (isCard) {
                        var total = ""
                        total = if (parameters!!["actual_wallet"] != null && parameters!!["actual_wallet"].toString().toFloat() != 0F) {
                            if (parameters!!["total"].toString().toFloat() != 0F) {
                                val finalAmount = parameters!!["actual_wallet"].toString().toFloat() + parameters!!["total"].toString().toFloat()
                                twoDigit(finalAmount.minus(session.user?.wallet!!))
                            } else {
                                twoDigit(parameters!!["actual_wallet"].toString().toFloat().minus(session.user?.wallet!!))
                            }
                        } else {
                            twoDigit(parameters!!["total"].toString().toFloat())
                        }
                        viewModel.cardPayment(hashMapOf(
                                Pair("transaction_id", transactionId!!),
                                Pair("card_token", cardListAdapter?.items?.get(lastPosition)?.cardToken.toString()),
                                Pair("order_id", System.currentTimeMillis().toString().takeLast(4)),
                                Pair("amount", total),
                                Pair("cvv", cardCVV!!)))
                    } else {
                        val total: String = if (parameters!!["actual_wallet"] != null && parameters!!["actual_wallet"].toString().toFloat() != 0F) {
                            if (parameters!!["total"].toString().toFloat() != 0F) {
                                val finalAmount = parameters!!["actual_wallet"].toString().toFloat() + parameters!!["total"].toString().toFloat()
                                twoDigit(finalAmount.minus(session.user?.wallet!!))
                            } else {
                                twoDigit(parameters!!["actual_wallet"].toString().toFloat().minus(session.user?.wallet!!))
                            }
                        } else {
                            twoDigit(parameters!!["total"].toString().toFloat())
                        }
                        viewModel.hostedPagePayment(hashMapOf(
                                Pair("transaction_id", transactionId!!),
                                Pair("amount", total),
                                Pair("capture", "0")
                        ))
                    }

                }
                StatusCode.CODE_INVALID_REQUEST -> {
                    // addFireBaseLogs("1013", "Payment Web View code 0 Fragment  SpecialBeverage - $transactionId", it.message)
                    showLoader(false)
                    showMessage(it.message)
                    //navigator.loadActivity(HomeActivity::class.java).byFinishingAll().start()
                }
                else -> {
                    showLoader(false)
                    //addFireBaseLogs("1013", "Payment Web View Fragment  SpecialBeverage - $transactionId", it.message)
                    showMessage(it.message)
                }
            }
        })

        // payment declined prcess
        setFragmentResultListener("paymentDeclined") {
                _, result ->
            val isDeclined = result.getBoolean("isDeclined", true)
            val errorMessage = result.getString("errorMessage", "")
            val orderPageLocal = result.getString("orderPage", null)
            val hostedPageOrderIdLocal = result.getInt("hostedPageOrderId", -1)
            val responseCode = result.getString("responseCode", "")
            Log.d("LZX", "showPaymentInfo : $responseCode")
            if (isDeclined) {
                if (orderPageLocal != null && hostedPageOrderIdLocal != -1 &&
                    (orderPageLocal == ConstantApp.PassValue.ORDER_FOOD || orderPageLocal == ConstantApp.PassValue.ORDER_BEVERAGE)) {
                    showPaymentInfo(hostedPageOrderIdLocal, errorMessage, orderPageLocal, responseCode)
                }
            }
        }

        viewModel.deleteSaveCardsLiveData.observe(this, {
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    viewModel.getCardList()
                }
                else -> {
                    showLoader(false)
                    showMessage(it.message)
                }
            }
        })
    }

    private fun showPaymentInfo(orderID: Int, message: String, orderPage: String?, responseCode : String) {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.dialog_payment_status)
        val body = dialog.findViewById(R.id.textViewInfo) as TextView
        body.text = getString(R.string.str_your_transaction_declined, orderID)
        val yesBtn = dialog.findViewById(R.id.buttonOk) as Button
        yesBtn.setOnClickListener {
            updateOrderStatus(orderID, orderPage == ConstantApp.PassValue.ORDER_FOOD)
            dialog.dismiss()
        }
        if (!dialog.isShowing)
            dialog.show()
    }

    private fun updateOrderStatus(orderID: Int, isFood: Boolean) {
        val retrofit = Retrofit.Builder()
            //.baseUrl("http://test-delivery.kravenbahamas.com/")
            .baseUrl("http://uat-delivery.kravenbahamas.com/")
            //.baseUrl("http://kravenbahamas.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(OrderCancelService::class.java)
        val call: Call<Any?>? =
            if (isFood) service.cancelDeclinedFoodOrderRetrofit(orderID.toString())
            else service.cancelDeclinedBeverageOrderRetrofit(orderID.toString())

        showLoader(true)
        call?.enqueue(object : Callback<Any?> {
            override fun onResponse(call: Call<Any?>?, response: Response<Any?>?) {
                updateNotifyStatus(orderID)
            }

            override fun onFailure(call: Call<Any?>?, t: Throwable) {
                updateNotifyStatus(orderID)
            }
        })
    }

    private fun updateNotifyStatus(orderID: Int) {
        val retrofit = Retrofit.Builder()
            //.baseUrl("http://test-delivery.kravenbahamas.com:7171")
            .baseUrl("http://uat-delivery.kravenbahamas.com:7171")
            //.baseUrl("http://kravenbahamas.com:7171")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(OrderCancelService::class.java)
        val call = service.updateNotifyStatus(orderID)
        call.enqueue(object : Callback<UpdateNotifyStatus> {
            override fun onResponse(call: Call<UpdateNotifyStatus>, response: Response<UpdateNotifyStatus>) {
                showLoader(false)
            }

            override fun onFailure(call: Call<UpdateNotifyStatus>, t: Throwable) {
                showLoader(false)
            }
        })
    }

    override fun bindData() {

        toolbar.showToolbar(true)
        toolbar.setToolbarTitle(getString(R.string.payment))
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)

        setUpRecyclerView()
        if (parameters!!["actual_wallet"] != null && parameters!!["actual_wallet"].toString().toFloat() != 0F) {
            if (parameters!!["total"].toString().toFloat() != 0F) {
                val finalAmount = parameters!!["actual_wallet"].toString().toFloat() + parameters!!["total"].toString().toFloat()
                textViewTotalAmount.text = convertTwoDigit(finalAmount.minus(session.user?.wallet!!))
            } else {
                textViewTotalAmount.text = convertTwoDigit(parameters!!["actual_wallet"].toString().toFloat().minus(session.user?.wallet!!))
            }
        } else {
            textViewTotalAmount.text = convertTwoDigit(parameters!!["total"].toString().toFloat())
        }
        viewModel.getCardList()
        buttonPay.clickWithDebounce {
            if (lastPosition != -1) {
                if (cardListAdapter?.items?.get(lastPosition)?.isExpired != null &&
                    cardListAdapter?.items?.get(lastPosition)?.isExpired == "1")
                {
                    //showMessage("The card is expired please use alternate payment method")
                    commandDialog("The selected card is expired!", "The card is expired please use alternate payment method", object : DialogInterface {
                        override fun onClickOk() {
                        }
                    })
                } else
                {
                    dialog = Dialog(activity!!, android.R.style.Theme_DeviceDefault_Dialog_MinWidth)
                    dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    dialog!!.setContentView(R.layout.dialog_input_cvv)
                    dialog!!.setCanceledOnTouchOutside(true)

                    val buttonOk = dialog!!.findViewById(R.id.btnPay) as MaterialButton
                    editTextCVV = dialog!!.findViewById(R.id.tieCvv) as TextInputEditText
                    val inputLayoutCVV = dialog!!.findViewById(R.id.inputLayoutCVV) as TextInputLayout
                    buttonOk.setOnClickListener {
                        try {
                            validator.submit(editTextCVV!!, inputLayoutCVV)
                                .checkEmpty().errorMessage(getString(R.string.please_enter_cvv))
                                .check()

                            validator.submit(editTextCVV!!, inputLayoutCVV)
                                .checkMinDigits(3).errorMessage(getString(R.string.please_enter_valid_cvv))
                                .check()

                            val imm = activity!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
                            imm!!.hideSoftInputFromWindow(editTextCVV!!.windowToken, 0)

                            transactionId = transactionIdWallet(session.user?.id!!)
                            cardCVV = editTextCVV!!.text.toString()
                            if(!cardCVV.isNullOrEmpty()) {
                                showLoader(true)
                                isCard = true
                                val parameters = Parameters()
                                parameters.subTotal = this@PaymentFragment.parameters!!["sub_total"].toString()
                                val promoCode = this@PaymentFragment.parameters?.get("promocode")
                                if (promoCode != null) {
                                    parameters.promocode = this@PaymentFragment.parameters?.get("promocode").toString()
                                }
                                if (orderPage == ConstantApp.PassValue.ORDER_FOOD || orderPage == ConstantApp.PassValue.ORDER_FOOD_FUTURE) {
                                    parameters.restaurantId = this@PaymentFragment.parameters!!["restaurant_id"].toString()
                                    cartViewModel.beforeVerifyFoodOrder(parameters)
                                } else if (orderPage == ConstantApp.PassValue.ORDER_BEVERAGE) {
                                    parameters.beverageId = this@PaymentFragment.parameters!!["beverage_id"].toString()
                                    cartViewModel.beforeVerifyBeverageOrder(parameters)
                                } else {
                                    transactionId=transactionId(session.user?.id!!)
                                    //transactionId = session.transactionIdCard
                                    /* viewModel.cardPayment(hashMapOf(
                                             Pair("transaction_id", transactionId!!),
                                             Pair("card_token", cardListAdapter?.items?.get(lastPosition)?.cardToken.toString()),
                                             Pair("order_id", System.currentTimeMillis().toString().takeLast(4)),
                                             Pair("amount", twoDigit(this@PaymentFragment.parameters!!["total"].toString().toFloat()))

                                     ))*/
                                    this@PaymentFragment.parameters!!["order_datetime"] = Formatter.format(Date().toString(), Formatter.YYYY_MM_DD_T_HH_MM_SSS_Z, Formatter.YYYY_MM_DD_HH_MM_SS, true).toString()
                                    //transactionId=transactionId(session.user?.id!!)
                                    // transactionId = session.transactionIdCard
                                    this@PaymentFragment.parameters!!["transaction_id"] = transactionId.toString()
                                    specialBeverageViewModel.paySpecialBeverageOrder(this@PaymentFragment.parameters!!)
                                }
                            }
                        } catch (a: ApplicationException) {
                            a.message
                        }
                    }

                    if (dialog != null)
                        dialog!!.show()
                }
            } else if (cardListAdapter != null && cardListAdapter!!.items.isEmpty()) {
                showMessage("Please add card")
            } else {
                showMessage(getString(R.string.please_select_card))
            }
        }

        buttonPayWithNewCard.clickWithDebounce {

            showLoader(true)
            isCard = false
            val parameters = Parameters()
            parameters.subTotal = this@PaymentFragment.parameters!!["sub_total"].toString()
            val promoCode = this@PaymentFragment.parameters?.get("promocode")
            if (promoCode != null) {
                parameters.promocode = this@PaymentFragment.parameters?.get("promocode").toString()
            }
            if (orderPage == ConstantApp.PassValue.ORDER_FOOD || orderPage == ConstantApp.PassValue.ORDER_FOOD_FUTURE) {
                parameters.restaurantId = this@PaymentFragment.parameters!!["restaurant_id"].toString()
                cartViewModel.beforeVerifyFoodOrder(parameters)
            } else if (orderPage == ConstantApp.PassValue.ORDER_BEVERAGE) {
                parameters.beverageId = this@PaymentFragment.parameters!!["beverage_id"].toString()
                cartViewModel.beforeVerifyBeverageOrder(parameters)
            } else {
                /* transactionId=transactionId(session.user?.id!!)
                 viewModel.hostedPagePayment(hashMapOf(
                         Pair("transaction_id", transactionId!!),
                         Pair("amount", this@PaymentFragment.parameters!!["total"].toString()),
                         Pair("capture", "0")
                 ))*/
                this@PaymentFragment.parameters!!["order_datetime"] = Formatter.format(Date().toString(), Formatter.YYYY_MM_DD_T_HH_MM_SSS_Z, Formatter.YYYY_MM_DD_HH_MM_SS, true).toString()
                transactionId=transactionId(session.user?.id!!)
                //transactionId = session.transactionIdCard
                this@PaymentFragment.parameters!!["transaction_id"] = transactionId.toString()
                specialBeverageViewModel.paySpecialBeverageOrder(this@PaymentFragment.parameters!!)
            }
        }
    }

    private fun setUpRecyclerView() {
        recyclerViewCardList.layoutManager = LinearLayoutManager(activity)
        cardListAdapter = CardListAdapter(
            object : CardListAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    lastPosition = position
                }},
            object : CardListAdapter.OnItemDeleteClickListener {
                override fun onItemDeleteClick(card: Card) {
                    commanDialogYesNoNew(getString(R.string.app_name),"Are you sure want to delete card?","Yes","No",object :DialogInterfaceYesNo{
                        override fun onClickYes() {
                            showLoader(true)
                            viewModel.deleteSaveCards(Parameters(userType = "User", cardToken = card.cardToken))
                        }
                        override fun onClickNo() {
                        }
                    })
                }}
        )
        recyclerViewCardList.adapter = cardListAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        if (dialog != null)
            dialog!!.dismiss()
    }
}