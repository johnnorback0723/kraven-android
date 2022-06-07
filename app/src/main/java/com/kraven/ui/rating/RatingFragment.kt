package com.kraven.ui.rating

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.kraven.R
import com.kraven.core.StatusCode
import com.kraven.di.component.FragmentComponent
import com.kraven.extensions.clearText
import com.kraven.extensions.getText
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.review.viewmodel.RatingViewModel
import com.kraven.ui.track.model.OrderDetail
import com.kraven.ui.track.viewmodel.TrackViewModel
import com.kraven.utils.ConstantApp
import kotlinx.android.synthetic.main.rating_fragment.*


class RatingFragment : BaseFragment() {

    private lateinit var viewModel: TrackViewModel

    private lateinit var ratingViewModel: RatingViewModel
    private var vendorId: String? = null
    private var vendorType: String? = null
    private var orderDetails: OrderDetail? = null
    private var isRestaurantRating = false


    private val orderId: String? by lazy {
        val args = arguments ?: throw IllegalStateException("Missing arguments")
        args.getString("OrderId")
    }

    private val tags: String? by lazy {
        val args = arguments ?: throw IllegalStateException("Missing arguments")
        args.getString("tag")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)[TrackViewModel::class.java]
        ratingViewModel = ViewModelProviders.of(this, viewModelFactory)[RatingViewModel::class.java]
        registerObserver()
    }

    private fun registerObserver() {
        viewModel.getOrderDetails.observe(this, {
            showLoader(false)
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    setData(it.data!!)
                }
                else -> {
                    showMessage(it.message)
                }
            }
        }, { true })

        ratingViewModel.giveRating.observe(this, {
            showLoader(false)
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    if (tags == ConstantApp.Firebase.DELIVERED_FOOD_ORDER_BY_DRIVER) {
                        isRestaurantRating = true
                        editTextComment.clearText()
                        ratingBar.count = 5
                        vendorId = orderDetails!!.restaurantId.toString()
                        vendorType = "Restaurant"
                        textViewName.text = orderDetails!!.restaurant.name
                        Glide.with(activity!!)
                                .load(orderDetails!!.restaurant.banner)
                                .apply(RequestOptions().placeholder(R.drawable.ic_user))
                                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                                .into(imageViewDriver)
                    }
                }
                else -> showMessage(it.message)
            }
        })

        ratingViewModel.giveRatingRestaurant.observe(this, {
            showLoader(false)
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    navigator.finish()
                }
                else -> showMessage(it.message)
            }
        })
    }

    override fun createLayout(): Int = R.layout.rating_fragment

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun bindData() {
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle(getString(R.string.rating))
        toolbar.setToolbarTextColorWhiteClose(true)
        toolbar.setButtonTextVisibility(false)

        orderId?.let { viewModel.getOrderDetails(it) }

        buttonSubmit.setOnClickListener {
            showLoader(true)
            if (isRestaurantRating.not()) {
                orderId?.let { it1 -> ratingViewModel.giveRating(it1, vendorId, vendorType, ratingBar.count, getText(editTextComment)) }
            } else {
                orderId?.let { it1 -> ratingViewModel.giveRatingRestaurant(it1, vendorId, vendorType, ratingBar.count, getText(editTextComment)) }
            }
        }
    }

    private fun setData(orderDetail: OrderDetail) {
        this@RatingFragment.orderDetails = orderDetail

        if (tags == ConstantApp.Firebase.DELIVERED_FOOD_ORDER_BY_DRIVER) {
            vendorId = orderDetail.driverId.toString()
            vendorType = "Driver"

            textViewName.text = orderDetail.driverDetails.name
            Glide.with(activity!!)
                    .load(orderDetail.driverDetails.profileImage)
                    .apply(RequestOptions().placeholder(R.drawable.ic_user))
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .into(imageViewDriver)
        } else {
            isRestaurantRating = true
            vendorId = orderDetail.restaurantId.toString()
            vendorType = "Restaurant"
            textViewName.text = orderDetail.restaurant.name
            Glide.with(activity!!)
                    .load(orderDetail.restaurant.banner)
                    .apply(RequestOptions().placeholder(R.drawable.ic_user))
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .into(imageViewDriver)
        }
    }
}