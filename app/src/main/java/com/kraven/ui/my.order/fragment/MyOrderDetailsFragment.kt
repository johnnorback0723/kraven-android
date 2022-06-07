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
import com.kraven.fcm.AppFirebaseNotification
import com.kraven.ui.activity.IsolatedActivity
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.cart.fragment.CartFragment
import com.kraven.ui.home.model.CartModel
import com.kraven.ui.home.model.DishesItem
import com.kraven.ui.my.order.adapter.OrderDetailsAdapter
import com.kraven.ui.my.order.model.OrderList
import com.kraven.ui.my.order.viewModel.OrderViewModel
import com.kraven.ui.rating.RatingOrderHistoryFragment
import com.kraven.ui.review.fragment.ReviewFragment
import com.kraven.ui.track.fragment.TrackOrderFragment
import com.kraven.ui.track.model.OrderDetail
import com.kraven.ui.track.viewmodel.TrackViewModel
import com.kraven.utils.ConstantApp
import com.kraven.utils.Formatter
import kotlinx.android.synthetic.main.my_order_details_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MyOrderDetailsFragment : BaseFragment(), View.OnClickListener {

    private var orderDetailsAdapter: OrderDetailsAdapter? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var orderList: OrderList? = null
    private var orderId: String? = null
    private var fragmentPosition: Int = 0

    private lateinit var viewModel: OrderViewModel
    private lateinit var trackViewModel: TrackViewModel
    private var textViewStatus: AppCompatTextView? = null
    private var orderDetails: OrderDetail? = null

    override fun createLayout(): Int = R.layout.my_order_details_fragment

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)[OrderViewModel::class.java]
        trackViewModel = ViewModelProviders.of(this, viewModelFactory)[TrackViewModel::class.java]
        registerObserver()
    }

    private fun registerObserver() {
        viewModel.cancelOrder.observe(this, {
            showLoader(false)
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    navigator.goBack()
                }
                else -> showMessage(it.message)
            }
        })

        trackViewModel.getOrderDetails.observe(this, {
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

    private fun setOrderData(orderDetails: OrderDetail) {
        this@MyOrderDetailsFragment.orderDetails = orderDetails

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
        textViewOrderDateTime.text = getString(R.string.date_time, Formatter.utcToLocal(orderDetails.orderDatetime,
                "yyyy-MM-dd HH-mm-ss", Formatter.DD_MMM_YYYY_hh_mm_aa))


        Glide.with(activity!!)
                .load(orderDetails.restaurant.banner)
                /*.apply(RequestOptions().placeholder(R.drawable.ic_user))*/
                .apply(RequestOptions.bitmapTransform(MultiTransformation(CenterCrop(), RoundedCorners(15))))
                .into(imageViewRestaurant)

        textViewRestaurantName.text = orderDetails.restaurant.name
        textViewRestaurantItem.text = orderDetails.restaurant.cuisines?.joinToString(", ", transform = { it.cuisine })
        ratingBarRestaurant.count = orderDetails.restaurant.rating
        textViewReviewCount.text = getString(R.string.review_restaurant, orderDetails.restaurant.reviews.toString())
        textViewAddressType.text = orderDetails.deliveryType
        textViewAddress.text = if(orderDetails.deliveryAddress.isNotEmpty())orderDetails.deliveryAddress else orderDetails.pickupAddress

        textViewVat.text = getString(R.string.vat, twoDigit(orderDetails.vatPercent) + "%")

        textViewSubTotalValue.text = convertTwoDigit(orderDetails.subTotal)
        textViewDeliveryChargeValue.text = convertTwoDigit(orderDetails.deliveryCharge.plus(orderDetails.deliveryCommission))
        textViewVatValue.text = convertTwoDigit(orderDetails.totalVat)
        textViewTip.text = getString(R.string.tip, twoDigit(orderDetails.tipPercent) + "%")
        textViewTipValue.text = convertTwoDigit(orderDetails.tip)
        textViewDiscountValue.text = convertTwoDigit(orderDetails.discount)
        textViewTotal.text = convertTwoDigit(orderDetails.total)

        orderDetailsAdapter?.items = orderDetails.orderDishes

        textViewPaymentModeValue.text = getString(R.string.payment_mode_value,
                orderDetails.total.toString(), convertTwoDigit(orderDetails.wallet))

        if (orderDetails.status == ConstantApp.OrderStatus.PENDING) {
                //|| orderDetails.status == ConstantApp.OrderStatus.ACCEPTED) {
            buttonCancel.text = getString(R.string.cancel_order)
            buttonTrack.viewShow()
        } else if (orderDetails.status == ConstantApp.OrderStatus.CONFIRMED) {
            buttonTrack.viewShow()
            buttonCancel.viewGone()
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
                if (orderDetails.restaurantReview == 0) {
                    buttonCancel.viewShow()
                    buttonCancel.text = getString(R.string.rate_and_review)
                } else {
                    buttonCancel.viewGone()
                }
            } else if (orderDetails.deliveryType == ConstantApp.DELIVERY) {
                if (orderDetails.restaurantReview == 0 || orderDetails.driverReview == 0) {
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

        if (orderDetails.status == ConstantApp.OrderStatus.REJECTED || orderDetails.status == ConstantApp.OrderStatus.CANCELLED
                || orderDetails.status == "Payment Failed") {
            textViewCancelBy.viewShow()
            textViewCancelReason.viewShow()

            textViewCancelBy.text = getString(R.string.cancel_by, orderDetails.cancelBy)
            textViewCancelReason.text = getString(R.string.cancel_reason, orderDetails.cancelReason)
        }

        if (orderDetails.paymentMethod == "Card" && orderDetails.cardToken != null && orderDetails.cardToken?.isNotEmpty()!!) {
            //groupPaymentVisible.viewShow()
            val lastFour = orderDetails.cardToken.substring(orderDetails.cardToken.length - 4, orderDetails.cardToken.length)
            textViewCardNumberValue.text = "xxxxx xxxx xxxx ".plus(lastFour)
            textViewTransactionValue.text = orderDetails.transactionId
        } else {
            //groupPaymentVisible.viewGone()
            textViewCardNumberValue.text = "-"
            textViewTransactionValue.text = orderDetails.transactionId
            //textViewCardNumberValue.text = "-"
            //textViewTransactionValue.text = "-"
        }

        /*if (orderDetails.tollFee != null && orderDetails.tollFee != "0") {
            groupTollFee.viewShow()*/
        textViewTollFeeValue.text = convertTwoDigit(orderDetails.tollFee?.toFloat()!!)
        //}
    }

    override fun bindData() {
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle(getString(R.string.my_order_details))
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)

        setHasOptionsMenu(true)
        if (arguments != null) {
            if (arguments!!.getParcelable<OrderList>(ConstantApp.PassValue.ORDER_DETAIL) != null) {
                orderList = arguments!!.getParcelable(ConstantApp.PassValue.ORDER_DETAIL)
                trackViewModel.getOrderDetails(orderList!!.id.toString())
            } else if (arguments!!.getString("orderId") != null) {
                orderId = arguments!!.getString("orderId")
                trackViewModel.getOrderDetails(orderId.toString())
            }
            showLoader(true)
            fragmentPosition = arguments!!.getInt(ConstantApp.PassValue.ORDER_FRAGMENT_POSITION)
        }


        buttonTrack.setOnClickListener(this)
        buttonCancel.setOnClickListener(this)
        imageViewDeliveryInfo.setOnClickListener(this)
        textViewReviewCount.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        if (orderList != null) {
            trackViewModel.getOrderDetails(orderList!!.id.toString())
        } else if (orderId != null) {
            trackViewModel.getOrderDetails(orderId.toString())
        }
    }

    private fun setUpRecyclerView() {
        linearLayoutManager = LinearLayoutManager(activity)
        recyclerViewOrderList.layoutManager = linearLayoutManager
        orderDetailsAdapter = OrderDetailsAdapter(object : OrderDetailsAdapter.ItemClickListenerChild {
            override fun onItemChildClick() {

            }
        })
        recyclerViewOrderList.adapter = orderDetailsAdapter
    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.textViewReviewCount -> {
                val b = Bundle()
                b.putString(Common.ID, orderDetails?.restaurantId.toString())
                b.putString(Common.VENDORTYPE, ConstantApp.PassValue.ORDER_FOOD)
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
                when {
                    getText(buttonTrack) == getString(R.string.reorder) -> {
                        addItemToCarts()
                    }
                    else -> navigator.load(TrackOrderFragment::class.java).setBundle(Bundle().apply {
                        //putString(ConstantApp.PassValue.ORDER_ID, orderList?.id.toString())
                        putString(ConstantApp.PassValue.ORDER_ID, orderDetails?.id.toString())
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
                            //viewModel.cancelOrder(orderList?.id.toString())
                            viewModel.cancelOrder(orderDetails?.id.toString())
                        }
                    })

                } else if (getText(buttonCancel) == getString(R.string.rate_and_review)) {
                    navigator.loadActivity(IsolatedActivity::class.java).setPage(RatingOrderHistoryFragment::class.java)
                            .addBundle(Bundle().apply {
                                putParcelable("OrderDetails", orderDetails)
                            }).start()
                    /*navigator.load(RatingOrderHistoryFragment::class.java).setBundle(Bundle().apply {
                        putParcelable("OrderDetails", orderDetails)
                    }).replace(true)*/
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

                        cartModel.restaurantId = orderDetails?.restaurantId.toString()
                        if (cartModel.dishes == null) {
                            cartModel.dishes = ArrayList()
                        }
                        cartModel.dishes = orderDetails?.orderDishes as ArrayList<DishesItem>
                        cartModel.orderPage = ConstantApp.PassValue.ORDER_FOOD
                        cartModel.isDetails = true
                        Log.e(this::class.java.simpleName, "ERROR NEW ASSIGN =")
                        session.cartModel = cartModel

                        navigator.loadActivity(IsolatedActivity::class.java).addBundle(Bundle().apply {
                            putString(ConstantApp.PassValue.RESTAURANT_ID, orderDetails?.restaurantId.toString())
                        }).setPage(CartFragment::class.java).start()
                    }

                    override fun onClickNo() {
                    }

                })
            } else {
                val cartModel = CartModel()

                cartModel.restaurantId = orderDetails?.restaurantId.toString()
                if (cartModel.dishes == null) {
                    cartModel.dishes = ArrayList()
                }
                cartModel.dishes = orderDetails?.orderDishes as ArrayList<DishesItem>
                cartModel.orderPage = ConstantApp.PassValue.ORDER_FOOD
                cartModel.isDetails = true
                Log.e(this::class.java.simpleName, "ERROR NEW ASSIGN =")
                session.cartModel = cartModel

                navigator.loadActivity(IsolatedActivity::class.java).addBundle(Bundle().apply {
                    putString(ConstantApp.PassValue.ORDER_FOOD, cartModel.orderPage)
                    putString(ConstantApp.PassValue.RESTAURANT_ID, orderDetails?.restaurantId.toString())
                }).setPage(CartFragment::class.java).start()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(appFireBaseNotification: AppFirebaseNotification) {
        clearNotification(activity!!)
        if(appFireBaseNotification.tag =="accept_foodorder"){
            buttonCancel.viewGone()
        }
    }
}