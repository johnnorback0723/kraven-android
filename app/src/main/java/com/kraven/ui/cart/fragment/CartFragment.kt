package com.kraven.ui.cart.fragment


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.TypefaceSpan
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager
import com.kraven.R
import com.kraven.application.KravenCustomer
import com.kraven.core.AppPreferences
import com.kraven.core.StatusCode
import com.kraven.data.pojo.Parameters
import com.kraven.di.component.FragmentComponent
import com.kraven.extensions.*
import com.kraven.ui.activity.IsolatedActivity
import com.kraven.ui.cart.CartSubTotalEvent
import com.kraven.ui.cart.adapter.BeverageCartAdapter
import com.kraven.ui.cart.adapter.CartAdapter
import com.kraven.ui.cart.adapter.TipAdapter
import com.kraven.ui.cart.viewModel.CartViewModel
import com.kraven.ui.home.fragment.ToppingBottomSheetBeverageEditFragment
import com.kraven.ui.home.fragment.ToppingBottomSheetEditFragment
import com.kraven.ui.home.model.DishesItem
import com.kraven.ui.home.model.SendItems
import com.kraven.ui.home.viewmodel.HomeViewModel
import com.kraven.ui.payment.fragment.PaymentFragment
import com.kraven.utils.ConstantApp
import com.kraven.utils.CustomTypefaceSpan
import com.kraven.utils.Formatter
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.cart_fragment.*
import kotlinx.android.synthetic.main.cart_fragment.view.*
import kotlinx.android.synthetic.main.common_cart_layout.*
import kotlinx.android.synthetic.main.layout_apply_code_tip.*
import org.greenrobot.eventbus.EventBus
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.abs


class CartFragment : BaseCartFragment(), View.OnClickListener,
        ToppingBottomSheetEditFragment.OnChildFragmentInteractionListener,
        ToppingBottomSheetBeverageEditFragment.OnChildFragmentInteractionListenersEdit {


    private var position = -1

    var bottomSheetTopping: ToppingBottomSheetEditFragment? = null

    @Inject
    lateinit var appPreferences: AppPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory)[CartViewModel::class.java]
        homeViewModel = ViewModelProviders.of(this, viewModelFactory)[HomeViewModel::class.java]
        registerObserver()

    }

    private fun registerObserver() {

        viewModel.getBeverageDetails.observe(this, {
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    it.data?.let { it1 -> radioButtonPickupVisibility(it1) }

                    val dish = cartModel?.dishes?.distinctBy { dishesItem -> dishesItem.id }?.joinToString(",") { item -> item.id.toString() }
                    viewModel.getBeverageListLast(it.data?.id.toString(), dish!!)
                }
                else -> showMessage(it.message)
            }
        })

        viewModel.getRestaurantDetails.observe(this, {
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    it.data?.let { it1 -> radioButtonPickupVisibility(it1) }
                    val dish = cartModel?.dishes?.distinctBy { dishesItem -> dishesItem.dishId }?.joinToString(",") { item -> item.dishId.toString() }
                    if (dish != null) {
                        viewModel.getMenuLists(it.data?.id.toString(), dish)
                    } else {
                        showLoader(false)
                        showMessage("Something went wrong")
                    }

                }
                else -> {
                    showLoader(false)
                    showMessage(it.message)
                }
            }
        })


        viewModel.getMenuList.observe(this, { it ->

            val dishList = ArrayList<DishesItem>()
            val list = it.data?.filter { item -> item.status == ConstantApp.RestaurantStatus.AVAILABLE }
            if (list?.any { it.status == ConstantApp.RestaurantStatus.AVAILABLE }!!) {
                list.forEach {
                    cartModel?.dishes?.forEach { dishItem ->
                        if (it.id == dishItem.dishId?.toInt()) {
                            val resultDishItem = generateDishItem(it, dishItem)
                            dishList.add(resultDishItem)
                        }
                    }
                }
                if (dishList.size != 0) {
                    setUpRecyclerView(dishList)
                    setFirstTimeData()
                } else {
                    showLoader(false)
                    layoutNoData.visibility = View.VISIBLE
                }
            } else {
                showLoader(false)
                layoutNoData.visibility = View.VISIBLE
            }
        })

        viewModel.getBeverageLists.observe(this, { it ->
            showLoader(false)
            val dishListBeverage = ArrayList<DishesItem>()
            val list = it.data?.filter { item -> item.status == ConstantApp.RestaurantStatus.AVAILABLE }
            if (list?.any { it.status == ConstantApp.RestaurantStatus.AVAILABLE }!!) {
                list.forEach {
                    cartModel?.dishes?.forEach { dishItem ->
                        if (it.id == dishItem.id) {
                            val beverageDish = generateBeverageDishItem(it, dishItem)
                            dishListBeverage.add(beverageDish)
                        }
                    }
                }
                if (dishListBeverage.size != 0) {
                    setUpRecyclerViewBeverage(dishListBeverage)
                    setFirstTimeData()
                } else {
                    showLoader(false)
                    layoutNoData.visibility = View.VISIBLE
                }

            } else {
                showLoader(false)
                layoutNoData.visibility = View.VISIBLE
            }
        })

        viewModel.placeOrder.observe(this, {
            showLoader(false)
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    cartModel = null
                    session.cartModel = null
                    session.cartCount = "0"
                    session.totalPrice = 0F
                    if (getTextReplace(textViewTotal) == 0.0F) {
                        navigator.load(CompleteOrderCartFragment::class.java).setBundle(Bundle().apply {
                            putString("name", restaurantDetails?.name)
                            putBoolean("isWallet", true)
                        }).replace(false)
                    } else {
                        navigator.load(CompleteOrderCartFragment::class.java).setBundle(Bundle().apply {
                            putString("name", restaurantDetails?.name)
                        }).replace(false)
                    }

                }
                else -> showMessage(it.message)
            }
        }) { true }

        viewModel.checkPromocode.observe(this, {
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

        viewModel.getUser.observe(this, {

            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    session.user = it.data
                    if (walletAmount != 0.00f) {
                        editTextWallet.isEnabled = false
                        imageViewCancelWallet.viewShow()
                        editTextWallet.setText(convertTwoDigit(walletAmount))
                        showLoader(false)
                        setCalculatedData()
                    }
                }

                else -> {
                }
            }
        })

        viewModel.addMoneyToWallet.observe(this,
                {
                    showLoader(false)
                    when (it.code) {
                        StatusCode.CODE_SUCCESS -> {
                            walletAmount = getTextReplaceString(editTextWallet).toFloat()
                            viewModel.getUser(session.user?.id.toString())

                        }
                        else -> showMessage(it.message)
                    }
                }) { true }

        homeViewModel.getSetting.observe(this, {
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    setCalculatedData()
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

        viewModel.beforeVerifyFoodOrderLiveData.observe(this, {
            showLoader(false)
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    placeOrder()
                }
                else -> {
                    commandDialog(getString(R.string.app_name), it.message, object : DialogInterface {
                        override fun onClickOk() {

                        }

                    })
                }
            }
        })

        viewModel.beforeVerifyBeverageOrderLiveData.observe(this, {
            showLoader(false)
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    placeOrder()
                }
                else -> {
                    commandDialog(getString(R.string.app_name), it.message, object : DialogInterface {
                        override fun onClickOk() {

                        }

                    })
                }
            }
        })
    }

    private fun setFirstTimeData() {
        layoutData.visibility = View.VISIBLE

        textViewVat.text = getString(R.string.VAT, if (restaurantDetails!!.vatOption == "VAT") session.userSettings?.vat?.toFloat() else 0.00f)
        textViewTip.text = getString(R.string.tip, "0.00%")

        if (session.userSettings != null && session.userSettings?.vat.isNullOrEmpty().not()) {
            setCalculatedData()
        } else {
            showLoader(true)
            homeViewModel.getSetting
        }

        if (cartModel?.orderDatetime != null && cartModel?.orderDatetime!!.isNotEmpty()) {
            inputLayoutDeliveryDate.viewShow()
            editTextDeliveryDate.setText(cartModel?.orderDatetime)
        }

        //editTextWallet.hint = getString(R.string.wallet_edit_text, session.user?.wallet)
        setWalletHint()

        if (cartModel?.orderPage == ConstantApp.PassValue.ORDER_BEVERAGE) {
            textViewYouMust.viewShow()
        } else {
            textViewYouMust.viewGone()
        }
        Handler().postDelayed({
            showLoader(false)
        }, 1000)
    }

    private fun setWalletHint() {
        val typefaceSpan: TypefaceSpan = CustomTypefaceSpan(ResourcesCompat.getFont(requireContext(), R.font.poppins_bold_0)!!)
        val spannableString = SpannableString(getString(R.string.wallet_edit_text, session.user?.wallet))
        spannableString.setSpan(typefaceSpan, 0, spannableString.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        editTextWallet.hint = spannableString
    }

    override fun createLayout(): Int = R.layout.cart_fragment

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun bindData() {
        viewModel.getUser(session.user?.id.toString())
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle(getString(R.string.your_cart))
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)

        if (session.cartModel != null) {

            showLoader(true)
            cartModel = session.cartModel!!

            requirePermission(activity!!, "To place order turn on your location services") {
                locationManager.checkLocationEnableAndStartUpdate(true)
                locationManager.locationUpdateLiveData.observe(this, androidx.lifecycle.Observer {
                    if (cartModel?.orderPage == ConstantApp.PassValue.ORDER_FOOD ||
                            cartModel?.orderPage == ConstantApp.PassValue.ORDER_FOOD_FUTURE) {
                        viewModel.getRestaurantDetails(cartModel?.restaurantId!!, it, (if (cartModel?.orderDatetime != null && cartModel?.orderDatetime!!.isNotEmpty()) cartModel?.orderDatetime else Formatter.format(Date().toString(), Formatter.YYYY_MM_DD_T_HH_MM_SSS_Z, "yyyy-MM-dd", false).toString())!!, if (cartModel?.orderPage == ConstantApp.PassValue.ORDER_FOOD) "Food" else "Future Food")
                    } else if (cartModel?.orderPage == ConstantApp.PassValue.ORDER_BEVERAGE) {
                        viewModel.getBeverageDetails(cartModel?.restaurantId!!, it)
                    }
                    locationManager.locationUpdateLiveData.removeObservers(this)
                })
            }
            address = ConstantApp.SaveValue.address
            /*  if (address != null) {
                  homeViewModel.checkToolFeeLocation(Parameters(latitude = address?.latitude.toString(), longitude = address?.longitude.toString()))
              }*/
            addressId = address?.id.toString()

            setUpLodingView()

            textViewAddressType.text = address?.addressType
            textViewAddress.text = address?.address

            imageViewDeliveryInfo.setOnClickListener(this)
            imageViewCashInfo.setOnClickListener(this)
            textViewChange.setOnClickListener(this)
            imageViewCancelPromocode.setOnClickListener(this)
            imageViewCancelWallet.setOnClickListener(this)
            llCashOn.setOnClickListener(this)
            buttonCheckout.setOnClickListener(this)

            llTip.setOnClickListener(this)
            imageViewCloseTip.setOnClickListener(this)
            buttonDoneTip.setOnClickListener(this)
            buttonTip.setOnClickListener(this)
            textViewHowToApply.setOnClickListener(this)

            recyclerViewTip.layoutManager = ChipsLayoutManager.newBuilder(context)
                    .setOrientation(ChipsLayoutManager.HORIZONTAL)
                    .setChildGravity(Gravity.CENTER)
                    .setRowStrategy(ChipsLayoutManager.STRATEGY_FILL_VIEW)
                    .build()

            tipAdapter = TipAdapter(tipList(), object : TipAdapter.OnSelectTip {
                override fun onSelectTip(selectTip: String, isEnter: Boolean) {
                    this@CartFragment.isEnter = isEnter
                    selectTips = selectTip
                    setCalculatedData()
                }
            })
            recyclerViewTip.adapter = tipAdapter

            editTextWallet.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyBoard()
                    editTextWallet.clearFocus()
                    if (editTextWallet.text.toString().isNotEmpty()) {
                        try {
                            if(session.user?.wallet!! > calculateTotal() && getTextReplace(editTextWallet) >= calculateTotal()){
                                walletAmount =calculateTotal()
                                editTextWallet.setText(convertTwoDigit(calculateTotal()))
                            }else{
                                walletAmount = getTextReplaceString(editTextWallet).toFloat()
                                editTextWallet.setText(convertTwoDigit(getTextReplaceString(editTextWallet).toFloat()))
                            }
                            editTextWallet.isEnabled = false
                            imageViewCancelWallet.viewShow()
                            showLoader(false)
                            setCalculatedData()
                            /*val userEnteredWalletAmount = getTextReplaceString(editTextWallet).toFloat()
                            val currentWalletAmount = session.user?.wallet
                            walletAmount = userEnteredWalletAmount*/
                           /* val differenceAmount = twoDigit(userEnteredWalletAmount - currentWalletAmount!!)
                            if (differenceAmount.toFloat() > 0) {
                                editTextWallet.setText(convertTwoDigit(walletAmount))
                                *//*commanDialogYesNoNew("You don't have enough in your wallet", "Continue to add $$differenceAmount to wallet",
                                        "Add", "Cancel", object : DialogInterfaceYesNo {
                                    override fun onClickYes() {
                                        navigator.loadActivity(IsolatedActivity::class.java).setPage(PaymentCartAddFragment::class.java).addBundle(
                                                Bundle().apply {
                                                    putFloat("amount", differenceAmount.toFloat())
                                                }
                                        ).forResult(103).start()

                                        navigator.loadActivity(IsolatedActivity::class.java).setPage(MyWalletFragment::class.java).byFinishingAll().start()
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
                                            setWalletHint()
                                            //editTextWallet.hint = getString(R.string.wallet_edit_text, session.user?.wallet)
                                        }
                                    }
                                })*//*
                            } else {
                                editTextWallet.setText(convertTwoDigit(userEnteredWalletAmount))
                                *//*walletAmount = userEnteredWalletAmount
                                editTextWallet.setText(convertTwoDigit(walletAmount))
                                editTextWallet.isEnabled = false
                                imageViewCancelWallet.viewShow()
                                editTextWallet.setText(convertTwoDigit(walletAmount))
                                showLoader(false)
                                setCalculatedData()*//*
                            }
                            editTextWallet.isEnabled = false
                            imageViewCancelWallet.viewShow()
                            showLoader(false)
                            setCalculatedData()*/
                        } catch (e: NumberFormatException) {
                            editTextWallet.clearText()
                            setWalletHint()
                            //editTextWallet.hint = getString(R.string.wallet_edit_text, session.user?.wallet)
                            showMessage("Please enter valid amount.")
                        }
                    }
                    true
                } else {
                    false
                }
            }

            editTextPromoCode.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyBoard()
                    editTextPromoCode.clearFocus()
                    if (editTextPromoCode.text.toString().isNotEmpty()) {
                        showLoader(true)
                        //editTextWallet.isEnabled = false
                        viewModel.checkPromocode(getTextReplace(textViewSubTotalValue).toString(), editTextPromoCode.text.toString(), "Food", restaurantDetails!!.id, cartModel?.orderPage)
                    }
                    true
                } else {
                    false
                }
            }

            radioGroupDelivery.setOnCheckedChangeListener { rGroup, _ ->
                val radioBtnID = rGroup.checkedRadioButtonId
                val radioB = rGroup.findViewById<AppCompatRadioButton>(radioBtnID)
                if (radioB.text == ConstantApp.DELIVERY) {
                    textViewAddressLabel.visibility = View.VISIBLE
                    textViewChange.visibility = View.VISIBLE
                    textViewAddressType.visibility = View.VISIBLE
                    textViewAddress.visibility = View.VISIBLE

                    radioButtonCashPickup.viewGone()
                    imageViewCashInfo.visibility = View.VISIBLE
                    if (address != null) {

                        if (dialog != null && dialog?.isShowing!!) {
                            dialog?.show()
                        }
                        radioB.isChecked = true
                        if (address!!.isCay == "1") restaurantDetails!!.delivery_base_fare = address!!.cayFee.toFloat()
                        calculateCharges()
                                .observeOn(Schedulers.from(appExecutors.mainThread()))
                                .subscribe(object : Observer<Float> {
                                    override fun onComplete() {
                                        if (address!!.isCay == "1") {
                                            textViewDeliveryChargeValue.text =
                                                convertTwoDigit(restaurantDetails!!.delivery_base_fare)
                                            setCalculatedData()
                                        }
                                    }

                                    override fun onSubscribe(d: Disposable) {
                                        if (address!!.isCay == "1") {
                                            textViewDeliveryChargeValue.text =
                                                convertTwoDigit(restaurantDetails!!.delivery_base_fare)
                                            setCalculatedData()
                                        }
                                    }

                                    override fun onNext(it: Float) {
                                        dialog?.dismiss()
                                        distance = Formatter.round(it.toDouble()).toFloat()
                                        if (it != 0.0F) {
                                            if (distance > restaurantDetails?.delivery_minimum_miles!!) {
                                                val extraDistance = restaurantDetails?.delivery_minimum_miles!! - distance
                                                val resultOfExtraDistance = abs(extraDistance) * restaurantDetails?.delivery_per_miles_rate!!
                                                val finalResult = restaurantDetails!!.delivery_base_fare + Formatter.round(resultOfExtraDistance.toDouble()).toFloat()
                                                if (textViewDeliveryChargeValue != null)
                                                    textViewDeliveryChargeValue.text = convertTwoDigit(finalResult)
                                            } else {
                                                if (textViewDeliveryChargeValue != null)
                                                    textViewDeliveryChargeValue.text = convertTwoDigit(restaurantDetails!!.delivery_base_fare)
                                            }
                                        } else {
                                            if (address!!.isCay == "1")
                                                textViewDeliveryChargeValue.text = convertTwoDigit(restaurantDetails!!.delivery_base_fare)
                                            else
                                                textViewDeliveryChargeValue.text = convertTwoDigit(it)
                                        }
                                        setCalculatedData()
                                    }

                                    override fun onError(e: Throwable) {
                                        e.printStackTrace()
                                        dialog?.dismiss()
                                    }

                                })

                    }
                } else {
                    textViewAddressLabel.visibility = View.GONE
                    textViewChange.visibility = View.GONE
                    textViewAddressType.visibility = View.GONE
                    textViewAddress.visibility = View.GONE

                    // radioButtonCashPickup.viewShow()
                    textViewCashOn.text = getString(R.string.cashOnDelivery)
                    textViewCashOn.text = getString(R.string.cashOnPickup)
                    imageViewCashInfo.visibility = View.GONE
                    setCalculatedData()
                    groupTollFee.viewGone()
                }
            }

            if (ConstantApp.SaveValue.isPickUpOrder) {
                radioButtonDelivery.viewGone()
            }

        } else {
            layoutNoData.visibility = View.VISIBLE
        }
    }

    private fun setUpRecyclerView(t: ArrayList<DishesItem>) {

        recyclerViewCartList.layoutManager = LinearLayoutManager(activity)
        cartAdapter = CartAdapter(object : CartAdapter.ItemClickListener {

            override fun onSpecialNote(specialNote: String, id: String) {
            }

            override fun onDelete(item: DishesItem?, position: Int) {
                commandDialogYesNo(getString(R.string.app_name), "Are you sure you want to delete?", object : DialogInterfaceYesNo {
                    override fun onClickYes() {
                        cartAdapter?.items?.removeAt(position)
                        cartAdapter?.notifyDataSetChanged()
                        if (cartAdapter?.items?.size == 0) {
                            layoutData.visibility = View.GONE
                            layoutNoData.visibility = View.VISIBLE
                            cartModel = null
                            session.cartModel = null
                            session.cartCount = "0"
                            session.totalPrice = 0F

                            EventBus.getDefault().post("")
                        } else {
                            session.cartCount = cartAdapter?.items?.size.toString()
                            session.totalPrice = getFinalPrice()
                            setCalculatedData()
                            EventBus.getDefault().post("")
                        }
                    }

                    override fun onClickNo() {

                    }

                })
            }

            override fun onAdd(position: Int, selectedCartList: DishesItem?, cartSubTotalEvent: CartSubTotalEvent) {
                setCalculatedData()
            }

            override fun onSubtract(position: Int, selectedCartList: DishesItem?, cartSubTotalEvent: CartSubTotalEvent) {

                setCalculatedData()
            }


            override fun onClickTopping(position: Int) {

            }

            override fun onItemClick(position: Int) {

            }

            override fun onMenusClick(position: Int, item: DishesItem?) {
                this@CartFragment.position = position

                bottomSheetTopping = ToppingBottomSheetEditFragment()
                val args = Bundle()
                args.putParcelable(ConstantApp.PassValue.RESTAURANT_MENU, item?.restaurantMenu)
                args.putParcelable("NewDishList", item)
                args.putString(ConstantApp.PassValue.TYPE, item?.menu)
                bottomSheetTopping!!.arguments = args
                bottomSheetTopping!!.show(childFragmentManager, bottomSheetTopping!!.tag)
            }

        })
        recyclerViewCartList.adapter = cartAdapter
        cartAdapter?.items = t

    }

    private fun setUpRecyclerViewBeverage(t: ArrayList<DishesItem>) {

        recyclerViewCartList.layoutManager = LinearLayoutManager(activity)
        beverageCartAdapter = BeverageCartAdapter(object : BeverageCartAdapter.ItemClickListener {

            override fun onDelete(item: DishesItem?, position: Int) {
                commandDialogYesNo(getString(R.string.app_name), "Are you sure you want to delete?", object : DialogInterfaceYesNo {
                    override fun onClickYes() {
                        //beverageCartAdapter?.removeItem(position)
                        beverageCartAdapter?.items?.removeAt(position)
                        beverageCartAdapter?.notifyDataSetChanged()

                        if (beverageCartAdapter?.items?.size == 0) {
                            layoutData.visibility = View.GONE
                            layoutNoData.visibility = View.VISIBLE
                            //cartModel?.dishes = null
                            cartModel = null

                            session.cartModel = null
                            session.cartCount = "0"
                            session.totalPrice = 0F

                            EventBus.getDefault().post("")
                        } else {
                            session.cartCount = beverageCartAdapter?.items?.size.toString()
                            session.totalPrice = getFinalPriceBeverage()
                            setCalculatedData()
                            EventBus.getDefault().post("")
                        }
                    }

                    override fun onClickNo() {

                    }

                })
            }

            override fun onAdd(position: Int, selectedCartList: DishesItem?, cartSubTotalEvent: CartSubTotalEvent) {
                setCalculatedData()
            }

            override fun onSubtract(position: Int, selectedCartList: DishesItem?, cartSubTotalEvent: CartSubTotalEvent) {
                setCalculatedData()
            }

            override fun onMenusClick(position: Int, item: DishesItem?) {
                this@CartFragment.position = position

                val bottomSheetTopping = ToppingBottomSheetBeverageEditFragment()
                val args = Bundle()
                args.putParcelable("NewDishList", item)
                args.putString(ConstantApp.PassValue.TYPE, item?.menu)
                bottomSheetTopping.arguments = args
                bottomSheetTopping.show(childFragmentManager, bottomSheetTopping.tag)
            }

        })
        recyclerViewCartList.adapter = beverageCartAdapter
        beverageCartAdapter?.items = t

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.textViewHowToApply -> {
                commandDialog("Wallet Balance Info ",
                        getString(R.string.how_to_apply), object : DialogInterface {
                    override fun onClickOk() {

                    }
                })
            }
            R.id.imageViewDeliveryInfo -> {
                commandDialog(getString(R.string.delivery_charge_info),
                        if (cartModel!!.orderPage == ConstantApp.PassValue.ORDER_FOOD)
                            session.userSettings?.deliveryChargeBtnInfo!! else session.userSettings?.bevDeliveryChargeBtnInfo!!, object : DialogInterface {
                    override fun onClickOk() {

                    }
                })
            }

            R.id.imageViewCashInfo -> {
                commandDialog(getString(R.string.cash_on_delivery_info), getString(R.string.cash_delivery_text), object : DialogInterface {
                    override fun onClickOk() {

                    }
                })
            }

            R.id.buttonCheckout -> {
                if (cartModel?.orderPage == ConstantApp.PassValue.ORDER_FOOD || cartModel?.orderPage == ConstantApp.PassValue.ORDER_FOOD_FUTURE) {

                    if (cartAdapter?.items?.firstOrNull()?.toppings?.size != 0) {
                        val firstCheck = cartAdapter?.items?.firstOrNull {
                            it.toppings!!.firstOrNull()?.minCount == 0 && it.toppings?.firstOrNull()?.minCount!! <= it.toppings?.firstOrNull()?.minQty!! && it.toppings?.firstOrNull()?.minQty!! != 0
                        }
                        if (firstCheck != null) {
                            showMessage("Please choose ${firstCheck.toppings!!.firstOrNull { it.type != "" }!!.type} up to ${firstCheck.toppings!!.firstOrNull { it.minQty != 0 }!!.minQty}")
                        } else {
                            checkOut()
                        }
                    } else {
                        checkOut()
                    }

                } else if (cartModel?.orderPage == ConstantApp.PassValue.ORDER_BEVERAGE) {

                    if (beverageCartAdapter?.items?.firstOrNull()?.toppings?.firstOrNull()?.toppingList?.size != 0) {

                        val firstCheck = beverageCartAdapter?.items?.firstOrNull {
                            it.toppings?.firstOrNull()?.minCount != null && it.toppings?.firstOrNull()?.minQty != null &&
                                    it.toppings?.firstOrNull()?.minCount == 0 &&
                                    it.toppings?.firstOrNull()?.minCount!! <= it.toppings?.firstOrNull()?.minQty!!
                                    && it.toppings?.firstOrNull()?.minQty!! != 0
                        }

                        if (firstCheck != null) {
                            showMessage("Please choose ${firstCheck.toppings!!.firstOrNull { it.type != "" }!!.type} up to ${firstCheck.toppings!!.firstOrNull { it.minQty != 0 }!!.minQty}")
                        } else {
                            checkOut()
                        }
                    } else {
                        checkOut()
                    }
                }
            }

            R.id.textViewChange -> {
                val b = Bundle()
                b.putString(ConstantApp.PassValue.RESTAURANT_ID, restaurantDetails?.id.toString())
                b.putString(ConstantApp.PassValue.ORDER_FOOD, cartModel?.orderPage)
                navigator.loadActivity(IsolatedActivity::class.java)
                        .setPage(SelectAddressFragment::class.java).forResult(102).addBundle(b).start()
            }

            R.id.imageViewCancelWallet -> {
                editTextWallet.isEnabled = true
                walletAmount = 0.00F
                imageViewCancelWallet.visibility = View.GONE
                editTextWallet.clearText()
                //editTextWallet.hint = getString(R.string.wallet_edit_text, session.user?.wallet)
                setWalletHint()
                setCalculatedData()
            }

            R.id.imageViewCancelPromocode -> {
                editTextWallet.isEnabled = true
                editTextPromoCode.clearText()
                imageViewCancelPromocode.visibility = View.GONE
                promoCode = null
                KravenCustomer.tempPromoCode = null
                setCalculatedData()
            }

            R.id.llCashOn -> {
                radioButtonCashOn.isSelected = !radioButtonCashOn.isSelected
                radioButtonDebitCard.isChecked = false
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
                selectTips = ""
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

    private fun checkOut() {
        if (restaurantDetails?.availabilityStatus == ConstantApp.RestaurantStatus.AVAILABLE) {
            when {
                radioGroupDelivery.checkedRadioButtonId == -1 -> showMessage("Please select order type")
                address == null && !radioButtonPickup.isChecked -> showMessage("Please select address")
                radioGroupDelivery.radioButtonDelivery.isChecked && getTextReplace(textViewDeliveryChargeValue) == 0.00f -> showMessage("Please select a valid address")
                //radioGroupPaymentMethod.checkedRadioButtonId == -1 -> showMessage("Please select payment method")
                //radioButtonCashPickup.isChecked -> placeOrder(isCash = true)
                //radioButtonDebitCard.isChecked -> placeOrder(isCash = false)
                else -> {
                    showLoader(true)
                    val parameters = Parameters()
                    parameters.subTotal = getTextReplace(textViewSubTotalValue).toString()
                    if (promoCode != null) {
                        parameters.promocode = promoCode?.promocode
                    }
                    if (cartModel?.orderPage == ConstantApp.PassValue.ORDER_FOOD || cartModel?.orderPage == ConstantApp.PassValue.ORDER_FOOD_FUTURE) {
                        parameters.restaurantId = restaurantDetails?.id.toString()
                        viewModel.beforeVerifyFoodOrder(parameters)
                    } else {
                        parameters.beverageId = beverageCartAdapter?.items?.get(0)?.beverageId.toString()
                        viewModel.beforeVerifyBeverageOrder(parameters)
                    }

                }

                //placeOrder(isCash = false)
            }

        } else {
            /*if (cartModel?.orderDatetime != null && cartModel!!.orderDatetime!!.isNotEmpty()) {
                if (setRestaurantTimeSelectedDateTime(cartModel?.orderDatetime!!)) {
                    if (radioButtonPickup.isChecked) {
                        placeOrder(isCash = true)
                    } else if (radioButtonDelivery.isChecked) {
                        placeOrder(isCash = false)
                    }
                } else {
                    commandDialogYesNo(if (cartModel?.orderPage == ConstantApp.PassValue.ORDER_FOOD || cartModel?.orderPage == ConstantApp.PassValue.ORDER_FOOD_FUTURE) "Restaurant is currently closed" else "Beverage is currently closed", "Do you want to order for the future?", object : DialogInterfaceYesNo {
                        override fun onClickYes() {
                            navigator
                                    .loadActivity(IsolatedActivity::class.java, FutureFoodOrderFragment::class.java)
                                    .addBundle(Bundle().apply {
                                        putBoolean("isRestaurantDetails", true)
                                        putParcelable(ConstantApp.PassValue.RESTAURANT_MENU, restaurantDetails)
                                    }).forResult(101).start()
                        }

                        override fun onClickNo() {

                        }
                    })
                }
            }else{*/
            showMessage("Sorry! " + restaurantDetails?.name + " is " + restaurantDetails?.availabilityStatus!!)
            //}
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 102) {
                if (data?.getParcelableExtra<com.kraven.ui.address.model.Address>("address") != null) {
                    address = data.getParcelableExtra("address")
                    addressId = address?.id.toString()

                    if (address != null) {
                        homeViewModel.run {
                            checkToolFeeLocation(Parameters(
                                    pickupLatitude = restaurantDetails?.latitude.toString(),
                                    pickupLongitude = restaurantDetails?.longitude,
                                    latitude = address?.latitude.toString(),
                                    longitude = address?.longitude.toString()))
                        }
                    }
                    cartModel?.addressId = addressId

                    textViewAddressType.text = address?.addressType
                    textViewAddress.text = address?.address

                    radioButtonDelivery.viewShow()

                    if (radioButtonDelivery.isChecked && address != null) {
                        if (address!!.isCay == "1") restaurantDetails!!.delivery_base_fare = address!!.cayFee.toFloat()
                        calculateCharges()
                                .observeOn(Schedulers.from(appExecutors.mainThread()))
                                .subscribe(object : Observer<Float> {
                                    override fun onComplete() {
                                        if (address!!.isCay == "1") {
                                            textViewDeliveryChargeValue.text =
                                                convertTwoDigit(restaurantDetails!!.delivery_base_fare)
                                            setCalculatedData()
                                        }
                                    }

                                    override fun onSubscribe(d: Disposable) {
                                        if (address!!.isCay == "1") {
                                            textViewDeliveryChargeValue.text =
                                                convertTwoDigit(restaurantDetails!!.delivery_base_fare)
                                            setCalculatedData()
                                        }
                                    }

                                    override fun onNext(it: Float) {
                                        distance = Formatter.round(it.toDouble()).toFloat()
                                        dialog?.dismiss()
                                        if (it != 0.0F) {
                                            if (distance > restaurantDetails?.delivery_minimum_miles!!) {
                                                val extraDistance = restaurantDetails?.delivery_minimum_miles!! - distance
                                                val resultOfExtraDistance = abs(extraDistance) * restaurantDetails?.delivery_per_miles_rate!!
                                                val finalResult = restaurantDetails!!.delivery_base_fare + Formatter.round(resultOfExtraDistance.toDouble()).toFloat()
                                                textViewDeliveryChargeValue.text = convertTwoDigit(finalResult)
                                            } else {
                                                textViewDeliveryChargeValue.text = convertTwoDigit(restaurantDetails!!.delivery_base_fare)
                                            }
                                        } else {
                                            if (address!!.isCay == "1")
                                                textViewDeliveryChargeValue.text = convertTwoDigit(restaurantDetails!!.delivery_base_fare)
                                            else
                                                textViewDeliveryChargeValue.text = convertTwoDigit(it)
                                        }
                                        setCalculatedData()

                                    }

                                    override fun onError(e: Throwable) {
                                        e.printStackTrace()
                                        dialog?.dismiss()
                                    }

                                })
                    }

                } else {
                    address = null
                    radioButtonDelivery.viewGone()
                    setCalculatedData()
                }

            } else if (requestCode == 101) {
                if (cartModel != null) {
                    cartModel?.orderDatetime = data!!.getStringExtra(ConstantApp.PassValue.FUTUREDATE)
                    editTextDeliveryDate.setText(cartModel?.orderDatetime)
                }
            } else if (requestCode == 103) {
                val isCard = data?.getBooleanExtra("directcard", false)
                if (isCard!!) {
                    showLoader(true)
                    val parameters = HashMap<String, Any>()
                    parameters["card_token"] = data.getStringExtra("cardtoken")!!
                    parameters["amount"] = data.getStringExtra("amount")!!
                    viewModel.addMoneyToWallet(parameters)
                } else {
                    walletAmount = getTextReplaceString(editTextWallet).toFloat()
                    viewModel.getUser(session.user?.id.toString())
                }
            }
        }
    }

    private fun placeOrder() {
        if (restaurantDetails!!.minOrderAmount > getTextReplace(textViewSubTotalValue)) {
            showMessage(if (cartModel?.orderPage == ConstantApp.PassValue.ORDER_FOOD || cartModel?.orderPage == ConstantApp.PassValue.ORDER_FOOD_FUTURE)
                "You need to make a minimum order of  " + convertTwoDigit(restaurantDetails!!.minOrderAmount)
            else "You need to make a minimum order of  " + convertTwoDigit(restaurantDetails!!.minOrderAmount))
        } else {

            val parameters = HashMap<String, Any>()
            if (tollFee?.tollfee != null && radioButtonDelivery.isChecked) {
                parameters["tollfee"] = tollFee?.tollfee.toString()
            }
            parameters["distance"] = distance.toString()
            parameters["delivery_type"] = if (radioButtonPickup.isChecked) ConstantApp.PICKUP else ConstantApp.DELIVERY

            parameters["total_qty"] = getTotalQuantity().toString()
            parameters["total"] = getTextReplace(textViewTotal).toString()

            /*var vatPercent = 0.00F

            if (cartAdapter != null) {
                cartAdapter?.items?.forEach { dishes ->
                    vatPercent += dishes.vat?.toFloat()!!
                }
            } else {
                beverageCartAdapter?.items?.forEach { dishes ->
                    vatPercent += dishes.vat?.toFloat()!!
                }
            }*/
            /*parameters["vat_percent"] = session.userSettings?.vat.toString()

            var vatPrice = 0.00F

            if (cartAdapter != null) {
                cartAdapter?.items?.forEach { dishes ->
                    vatPrice += dishes.calculateVatPrice?.toFloat()!!
                }
            } else {
                beverageCartAdapter?.items?.forEach { dishes ->
                    vatPrice += dishes.calculateVatPrice?.toFloat()!!
                }
            }
            parameters["total_vat"] = vatPrice*/

            parameters["vat_percent"] = if (restaurantDetails!!.vatOption == "VAT") session.userSettings?.vat.toString() else "0.00"
            parameters["total_vat"] = getTextReplace(textViewVatValue).toString()
//            parameters["total_vat"] = getTextReplace(textViewVatValue).toString()

            parameters["sub_total"] = getTextReplace(textViewSubTotalValue).toString()
            parameters["delivery_charge"] = getTextReplace(textViewDeliveryChargeValue).toString()
            parameters["tip"] = getTextReplace(textViewTipValue).toString()


            //parameters["order_type"] = if (cartModel?.orderDatetime == null || cartModel?.orderDatetime?.isEmpty()!!) ConstantApp.NOW else ConstantApp.FUTURE
            parameters["order_type"] = ConstantApp.NOW

            //if (cartModel?.orderDatetime == null || cartModel?.orderDatetime?.isEmpty()!!) {
            //parameters["order_datetime"] = Formatter.format(Date().toString(), Formatter.YYYY_MM_DD_T_HH_MM_SSS_Z, Formatter.YYYY_MM_DD_HH_MM_SS, true).toString()
            /*} else {
                parameters["order_datetime"] = Formatter.format(cartModel?.orderDatetime!!, Formatter.YYYY_MM_DD_hh_mm_aa, Formatter.YYYY_MM_DD_HH_MM_SS, true).toString()
            }*/

            if (cartModel?.orderPage == ConstantApp.PassValue.ORDER_FOOD || cartModel?.orderPage == ConstantApp.PassValue.ORDER_FOOD_FUTURE) {
                parameters["dishes"] = sendDishItem(cartAdapter?.items) as List<SendItems>
                parameters["restaurant_id"] = restaurantDetails?.id.toString()
            } else if (cartModel?.orderPage == ConstantApp.PassValue.ORDER_BEVERAGE) {
                parameters["items"] = sendBeverageCart(beverageCartAdapter?.items)
                parameters["beverage_id"] = beverageCartAdapter?.items?.get(0)?.beverageId.toString()
            }

            if (radioButtonDelivery.isChecked) {
                parameters["address_id"] = addressId
                /* if (address?.buildingName == "" && address?.landmark == "") {
                     parameters["address_landmark"] = address?.house!!
                 } else if (address?.buildingName == "" && address?.landmark != "") {
                     parameters["address_landmark"] = address?.house!! + "," + address?.landmark
                 } else if (address?.buildingName != "" && address?.landmark == "") {
                     parameters["address_landmark"] = address?.house!! + "," + address?.buildingName
                 } else {
                     parameters["address_landmark"] = address?.house!! + "," + address?.buildingName + "," + address?.landmark
                 }*/
            }

            if (isEnter != null) {
                parameters["tip_percent"] = if (isEnter!!) "0" else selectTips ?: "0.00"
            }

            if (promoCode != null && promoCode?.restaurantId != null && promoCode?.restaurantId == 0 && promoCode?.beverageId == 0) {
                parameters["discount"] = getTextReplace(textViewDiscountValue).toString()
                parameters["promocode"] = editTextPromoCode.text.toString().trim()
            } else {
                parameters["discount"] = "0"
                if (cartModel?.orderPage == ConstantApp.PassValue.ORDER_FOOD || cartModel?.orderPage == ConstantApp.PassValue.ORDER_FOOD_FUTURE) {
                    parameters["restaurant_discount"] = getTextReplace(textViewDiscountValue).toString()
                } else {
                    parameters["beverage_discount"] = getTextReplace(textViewDiscountValue).toString()
                }
            }
            /*if (isCash) {
                showLoader(true)
                parameters["payment_method"] = if (isCash) "Cash" else "Card"
                if (cartModel?.orderPage == ConstantApp.PassValue.ORDER_FOOD || cartModel?.orderPage == ConstantApp.PassValue.ORDER_FOOD_FUTURE) {
                    viewModel.placeOrder(parameters)
                } else {
                    viewModel.placeOrderBeverage(parameters)
                }
            } else {*/
            if (getTextReplace(textViewTotal) == 0.0F && session.user?.wallet!! >= calculateTotal()) {
                parameters["wallet"] = getTextReplace(textViewWalletValue).toString()
                parameters["payment_method"] = "Wallet"
                parameters["actual_wallet"] = getTextReplace(textViewWalletValue).toString()
                parameters["order_datetime"] = Formatter.format(Date().toString(), Formatter.YYYY_MM_DD_T_HH_MM_SSS_Z, Formatter.YYYY_MM_DD_HH_MM_SS, true).toString()
                if (cartModel?.orderPage == ConstantApp.PassValue.ORDER_FOOD || cartModel?.orderPage == ConstantApp.PassValue.ORDER_FOOD_FUTURE) {
                    viewModel.placeOrder(parameters)
                } else {
                    viewModel.placeOrderBeverage(parameters)
                }
            } else {
                val userEnteredWalletAmount = if (getTextReplaceString(editTextWallet).isNotEmpty()) getTextReplaceString(editTextWallet).toFloat() else 0.0f
                val currentWalletAmount = session.user?.wallet
                val differenceAmount = twoDigit(userEnteredWalletAmount - currentWalletAmount!!)
                when {
                    getTextReplace(textViewTotal) == 0.0F -> {
                        parameters["wallet"] = twoDigit(calculateTotal())
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
                bundle.putString(ConstantApp.PassValue.ORDER_FOOD, cartModel?.orderPage)
                bundle.putString("restaurantname", restaurantDetails?.name)
                navigator.load(PaymentFragment::class.java).setBundle(bundle).replace(true)
            }
            // }
        }
    }

    override fun addFood(dishPriceTopping: Float, finalCount: Int, items: DishesItem, id: Int, itemPrice: Float, toppingPrice: Float) {
        cartAdapter?.updateItem(items, position)
        setCalculatedData()
    }

    override fun addBeverage(finalPrice: Float, items: DishesItem, orderPage: String) {
        beverageCartAdapter?.updateItem(items, position)
        setCalculatedData()
    }

    override fun onDestroy() {
        super.onDestroy()
        KravenCustomer.tempPromoCode = null
        if (cartModel != null && cartModel?.dishes != null) {
            if (cartModel?.isDetails!!) {
                cartModel = null
                session.cartModel = null
                session.cartCount = "0"
            }
        }
    }

    override fun onPause() {
        super.onPause()

        if (cartModel != null && cartModel?.dishes != null) {
            session.totalPrice = getFinalPrice()
            if (cartModel?.isDetails!!.not()) {
                if (cartAdapter != null) {
                    if (cartAdapter?.items != null && cartAdapter!!.items.size != 0) {
                        cartModel?.dishes = cartAdapter?.items!! as ArrayList<DishesItem>
                    }
                } else {
                    if (beverageCartAdapter?.items != null && beverageCartAdapter?.items!!.size != 0) {
                        cartModel?.dishes = beverageCartAdapter?.items as ArrayList<DishesItem>
                    }
                }
                session.cartModel = cartModel
            }
        } else {
            session.cartModel = null
            session.cartCount = "0"
            session.totalPrice = 0F
        }
    }
}