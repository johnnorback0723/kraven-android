package com.kraven.ui.my.order.fragment

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.kraven.R
import com.kraven.core.Common
import com.kraven.core.StatusCode
import com.kraven.di.component.FragmentComponent
import com.kraven.extensions.*
import com.kraven.ui.activity.IsolatedActivity
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.cart.fragment.CartFragment
import com.kraven.ui.home.model.CartModel
import com.kraven.ui.home.model.DishesItem
import com.kraven.ui.home.model.ToppingsItems
import com.kraven.ui.my.order.adapter.BeverageDetailsAdapter
import com.kraven.ui.my.order.model.BeverageHistory
import com.kraven.ui.my.order.model.BeverageHistoryDetails
import com.kraven.ui.my.order.viewModel.OrderViewModel
import com.kraven.ui.rating.RatingOrderBeverageFragment
import com.kraven.ui.review.fragment.ReviewFragment
import com.kraven.ui.track.fragment.TrackOrderBeverageFragment
import com.kraven.ui.track.viewmodel.TrackViewModel
import com.kraven.utils.ConstantApp
import com.kraven.utils.Formatter
import kotlinx.android.synthetic.main.my_order_details_fragment.*


class OrderDetailsBeverageFragment : BaseFragment(), View.OnClickListener {

    private var beverageAdapter: BeverageDetailsAdapter? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var beverageHistory: BeverageHistory? = null
    private var fragmentPosition: Int = 0
    private var orderId: String? = null
    private var textViewStatus: AppCompatTextView? = null
    private lateinit var viewModel: OrderViewModel
    private lateinit var trackViewModel: TrackViewModel
    private var orderDetails: BeverageHistoryDetails? = null

    override fun createLayout(): Int = R.layout.my_order_details_fragment

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory)[OrderViewModel::class.java]
        trackViewModel = ViewModelProviders.of(this, viewModelFactory)[TrackViewModel::class.java]
        registerObserver()
    }

    private fun registerObserver() {
        viewModel.cancelOrderBeverage.observe(this, {
            showLoader(false)
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    navigator.goBack()
                }
                else -> showMessage(it.message)
            }
        })

        trackViewModel.getOrderBeverageDetails.observe(this, {
            showLoader(false)
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    setOrderData(it.data!!)
                }
                else -> showMessage(it.message)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_status, menu)
        val textViewDone = menu.findItem(R.id.notification).actionView.findViewById(R.id.linearLayoutStatus) as LinearLayout
        textViewStatus = menu.findItem(R.id.notification).actionView.findViewById(R.id.textViewDetailsStatus) as AppCompatTextView
        if (orderDetails != null) {
            textViewStatus?.text = orderDetails!!.status
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setOrderData(orderDetails: BeverageHistoryDetails) {
        this@OrderDetailsBeverageFragment.orderDetails = orderDetails
        when (orderDetails.status) {
            "Payment Processing" -> {
                textViewStatus?.text = orderDetails.status
                textViewStatus?.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen._9ssp))
            }
            "Payment Failed" -> {
                textViewStatus?.text = orderDetails.status
                textViewStatus?.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen._10ssp))
            }
            else -> {
                textViewStatus?.text = orderDetails.status
            }
        }
        setUpRecyclerView()
        textViewOrderId.text = getString(R.string.order_id_small, orderDetails.id.toString())
        textViewOrderDateTime.text = getString(R.string.date_time, Formatter.utcToLocal(orderDetails.orderDatetime!!,
                "yyyy-MM-dd HH-mm-ss", Formatter.DD_MMM_YYYY_hh_mm_aa))

        Glide.with(activity!!)
                .load(orderDetails.beverage?.banner)
                /*.apply(RequestOptions().placeholder(R.drawable.ic_user))*/
                .apply(RequestOptions.bitmapTransform(MultiTransformation(CenterCrop(), RoundedCorners(15))))
                .into(imageViewRestaurant)

        imageViewRestaurant.layoutParams.height = resources.getDimension(R.dimen._70sdp).toInt()
        imageViewRestaurant.layoutParams.width = resources.getDimension(R.dimen._70sdp).toInt()


        textViewRestaurantName.text = orderDetails.beverage?.name
        textViewRestaurantItem.viewGone()
        textViewRestaurant.text = "Beverage"
        //textViewRestaurantItem.text = orderDetails.restaurant.cuisines?.joinToString(",", transform = { it.cuisine })
        ratingBarRestaurant.count = if (orderDetails.beverage?.rating != null) orderDetails.beverage.rating else 0
        textViewReviewCount.text = getString(R.string.review_restaurant, if (orderDetails.beverage?.reviews != null) orderDetails.beverage.reviews.toString() else "")
        textViewAddressType.text = orderDetails.deliveryType
        textViewAddress.text = if(orderDetails.deliveryAddress?.isNotEmpty()!!)orderDetails.deliveryAddress else orderDetails.pickupAddress

        textViewVat.text = getString(R.string.vat, twoDigit(orderDetails.vatPercent!!) + "%")
        textViewSubTotalValue.text = convertTwoDigit(orderDetails.subTotal!!)
        textViewDeliveryChargeValue.text = convertTwoDigit(orderDetails.deliveryCharge!!.plus(orderDetails.deliveryCommission!!))
        textViewVatValue.text = convertTwoDigit(orderDetails.totalVat!!)
        textViewTip.text = getString(R.string.tip, twoDigit(orderDetails.tipPercent!!) + "%")
        textViewTipValue.text = convertTwoDigit(orderDetails.tip!!)
        textViewDiscountValue.text = convertTwoDigit(orderDetails.discount)
        textViewTotal.text = convertTwoDigit(orderDetails.total!!)


        val passOrderList = ArrayList<BeverageHistoryDetails.PassOrderItem>()
        orderDetails.orderItems?.forEach {
            val passOrderItems = BeverageHistoryDetails.PassOrderItem()
            passOrderItems.id = it.itemId?.toInt()
            passOrderItems.image = it.image
            passOrderItems.item = it.item
            passOrderItems.menu = it.menu
            passOrderItems.milliliter = it.milliliter
            passOrderItems.price = it.price
            passOrderItems.status = it.status
            passOrderItems.qty = it.qty
            passOrderItems.itemId = it.itemId
            passOrderItems.specialNote = it.specialNote
            passOrderItems.toppings = ArrayList()
            val toppingsItems = ToppingsItems()
            toppingsItems.id = it.itemId?.toInt()
            toppingsItems.toppingList = ArrayList()
            toppingsItems.toppingList!!.addAll(it.toppings!!)
            passOrderItems.toppings!!.add(toppingsItems)
            passOrderList.add(passOrderItems)
        }

        beverageAdapter?.items = passOrderList

        textViewPaymentModeValue.text = getString(R.string.payment_mode_value,
                orderDetails.total.toString(), convertTwoDigit(orderDetails.wallet!!))


        if (orderDetails.status == ConstantApp.OrderStatus.PENDING
                || orderDetails.status == ConstantApp.OrderStatus.ACCEPTED) {
            buttonCancel.text = getString(R.string.cancel_order)
            buttonTrack.viewShow()
        } else if (orderDetails.status == ConstantApp.OrderStatus.CANCELLED
                || orderDetails.status == ConstantApp.OrderStatus.REJECTED) {
            buttonTrack.text = getString(R.string.reorder)
            buttonCancel.viewGone()
        } else if (orderDetails.status == ConstantApp.OrderStatus.PREPARING
                || orderDetails.status == ConstantApp.OrderStatus.ONTHEWAY) {
            buttonCancel.viewGone()
            buttonTrack.viewShow()
        } else if (orderDetails.status == ConstantApp.OrderStatus.DELIVERED ||
                orderDetails.status == ConstantApp.OrderStatus.COMPLETED) {

            if (orderDetails.deliveryType == ConstantApp.PICKUP) {
                if (orderDetails.beverageReview == 0) {
                    buttonCancel.viewShow()
                    buttonCancel.text = getString(R.string.rate_and_review)
                } else {
                    buttonCancel.viewGone()
                }
            } else if (orderDetails.deliveryType == ConstantApp.DELIVERY) {
                if (orderDetails.beverageReview == 0 || orderDetails.driverReview == 0) {
                    buttonCancel.viewShow()
                    buttonCancel.text = getString(R.string.rate_and_review)
                } else {
                    buttonCancel.viewGone()
                }
            }
            buttonTrack.text = getString(R.string.reorder)
        } else if (orderDetails.status == "Payment Processing") {
            buttonCancel.viewGone()
            buttonTrack.viewGone()
        } else if (orderDetails.status == "Payment Failed") {
            buttonCancel.viewGone()
            buttonTrack.viewShow()
            buttonTrack.text = getString(R.string.reorder)
        }

        if (orderDetails.status == ConstantApp.OrderStatus.REJECTED ||
                orderDetails.status == ConstantApp.OrderStatus.CANCELLED ||
                orderDetails.status == "Payment Failed") {
            textViewCancelBy.viewShow()
            textViewCancelReason.viewShow()

            textViewCancelBy.text = getString(R.string.cancel_by, orderDetails.cancelBy)
            textViewCancelReason.text = getString(R.string.cancel_reason, orderDetails.cancelReason)
        }

        if (orderDetails.paymentMethod == "Card" && orderDetails.cardToken != null && orderDetails.cardToken.isNotEmpty()) {
            //groupPaymentVisible.viewShow()
            val lastFour = orderDetails.cardToken.substring(orderDetails.cardToken.length - 4, orderDetails.cardToken.length)
            textViewCardNumberValue.text = "xxxxx xxxx xxxx " + lastFour
            textViewTransactionValue.text = orderDetails.transactionId
        } else {
            //groupPaymentVisible.viewGone()
            /* textViewCardNumberValue.visibility = View.GONE
             textViewTransactionValue.visibility = View.GONE*/

            /* textViewCardNumberValue.text = "-"
             textViewTransactionValue.text = "-"*/
            textViewCardNumberValue.text = "-"
            textViewTransactionValue.text = orderDetails.transactionId
        }

        /* if (orderDetails.tollFee != null && orderDetails.tollFee != "0") {
             groupTollFee.viewShow()*/
        textViewTollFeeValue.text = orderDetails.tollFee?.toFloat()?.let { convertTwoDigit(it) }
        // }
    }

    override fun bindData() {
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle(getString(R.string.my_order_details))
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)

        setHasOptionsMenu(true)
        buttonTrack.setOnClickListener(this)
        buttonCancel.setOnClickListener(this)
        imageViewDeliveryInfo.setOnClickListener(this)
        textViewReviewCount.setOnClickListener(this)
    }

    private fun setUpRecyclerView() {
        linearLayoutManager = LinearLayoutManager(activity)
        recyclerViewOrderList.layoutManager = linearLayoutManager
        beverageAdapter = BeverageDetailsAdapter()
        recyclerViewOrderList.adapter = beverageAdapter
    }

    override fun onResume() {
        super.onResume()
        if (arguments != null) {
            if (arguments!!.getParcelable<BeverageHistory>(ConstantApp.PassValue.ORDER_DETAIL) != null) {
                beverageHistory = arguments!!.getParcelable(ConstantApp.PassValue.ORDER_DETAIL)
                trackViewModel.getOrderBeverageDetails(beverageHistory!!.id.toString())
            } else if (arguments!!.getString("orderId") != null) {
                orderId = arguments!!.getString("orderId")
                trackViewModel.getOrderBeverageDetails(orderId.toString())
            }
            showLoader(true)
            fragmentPosition = arguments!!.getInt(ConstantApp.PassValue.ORDER_FRAGMENT_POSITION)
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.textViewReviewCount -> {
                val b = Bundle()
                b.putString(Common.ID, orderDetails?.beverageId.toString())
                b.putString(Common.VENDORTYPE, ConstantApp.PassValue.ORDER_BEVERAGE)
                navigator.load(ReviewFragment::class.java).setBundle(b).replace(true)

            }
            R.id.imageViewDeliveryInfo -> {
                session.userSettings?.bevDeliveryChargeBtnInfo?.let {
                    commandDialog(getString(R.string.delivery_charge_info), it, object : BaseFragment.DialogInterface {
                        override fun onClickOk() {

                        }
                    })
                }
            }
            R.id.buttonTrack -> {
                if (getText(buttonTrack) == getString(R.string.reorder)) {
                    addItemToCarts()
                } else {
                    navigator.load(TrackOrderBeverageFragment::class.java).setBundle(Bundle().apply {
                        putString(ConstantApp.PassValue.ORDER_ID, beverageHistory?.id.toString())
                    }).replace(true)
                }
            }
            R.id.buttonCancel -> {
                if (getText(buttonCancel) == getString(R.string.cancel_order)) {
                    commandDialogYesNo(getString(R.string.app_name), "Are you sure you want to cancel this order?", object : DialogInterfaceYesNo {
                        override fun onClickNo() {

                        }

                        override fun onClickYes() {
                            showLoader(true)
                            viewModel.cancelOrderBeverage(beverageHistory?.id.toString())
                        }
                    })
                } else if (getText(buttonCancel) == getString(R.string.rate_and_review)) {
                    navigator.loadActivity(IsolatedActivity::class.java).setPage(RatingOrderBeverageFragment::class.java)
                            .addBundle(Bundle().apply {
                                putParcelable("OrderDetails", orderDetails)
                            }).start()
                }
            }
        }
    }

    private fun addItemToCarts() {
        if (orderDetails != null) {
            if (session.cartModel != null) {
                commanDialogYesNoNew(getString(R.string.message_restaurant_already_exist_title),
                        getString(R.string.message_restaurant_already_exist), "Yes", "No", object : DialogInterfaceYesNo {
                    override fun onClickYes() {
                        Log.e(this::class.java.simpleName, "ERROR NEW ASSIGN =")
                        session.cartModel = null
                        session.cartCount = "0"

                        val cartModel = CartModel()

                        cartModel.restaurantId = orderDetails?.beverageId.toString()
                        if (cartModel.dishes == null) {
                            cartModel.dishes = ArrayList()
                        }


                        cartModel.dishes = beverageAdapter!!.items as ArrayList<DishesItem>
                        cartModel.orderPage = ConstantApp.PassValue.ORDER_BEVERAGE
                        cartModel.isDetails = true
                        Log.e(this::class.java.simpleName, "ERROR NEW ASSIGN =")
                        session.cartModel = cartModel



                        navigator.loadActivity(IsolatedActivity::class.java).addBundle(Bundle().apply {
                            putString(ConstantApp.PassValue.RESTAURANT_ID, orderDetails?.beverageId.toString())
                        }).setPage(CartFragment::class.java).start()
                    }

                    override fun onClickNo() {
                    }

                })
            } else {
                val cartModel = CartModel()

                cartModel.restaurantId = orderDetails?.beverageId.toString()
                if (cartModel.dishes == null) {
                    cartModel.dishes = ArrayList()
                }
                cartModel.dishes = beverageAdapter!!.items as ArrayList<DishesItem>
                cartModel.orderPage = ConstantApp.PassValue.ORDER_BEVERAGE
                cartModel.isDetails = true
                Log.e(this::class.java.simpleName, "ERROR NEW ASSIGN =")
                session.cartModel = cartModel

                navigator.loadActivity(IsolatedActivity::class.java).addBundle(Bundle().apply {
                    putString(ConstantApp.PassValue.ORDER_FOOD, cartModel.orderPage)
                    putString(ConstantApp.PassValue.RESTAURANT_ID, orderDetails?.beverageId.toString())
                }).setPage(CartFragment::class.java).start()

            }

        }
    }
}