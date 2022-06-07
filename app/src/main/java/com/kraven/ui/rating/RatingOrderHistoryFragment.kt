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


class RatingOrderHistoryFragment : BaseFragment() {

    private lateinit var viewModel: TrackViewModel

    private lateinit var ratingViewModel: RatingViewModel
    private var isRestaurantRating = false

    private var vendorId: String? = null
    private var vendorType: String? = null

    private val orderDetails: OrderDetail by lazy {
        val args = arguments ?: throw IllegalStateException("Missing Arguments")
        args.getParcelable("OrderDetails")!!

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)[TrackViewModel::class.java]
        ratingViewModel = ViewModelProviders.of(this, viewModelFactory)[RatingViewModel::class.java]
        registerObserver()
    }

    private fun registerObserver() {
      /*  viewModel.getOrderDetails.observe(this, {
            showLoader(false)
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    setData(it.data!!)
                }
                else -> {
                    showMessage(it.message)
                }
            }
        }, { true })*/

        ratingViewModel.giveRating.observe(this, {
            showLoader(false)
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    if (orderDetails.deliveryType == ConstantApp.DELIVERY) {

                        isRestaurantRating = true
                        editTextComment.clearText()
                        ratingBar.count = 5
                        vendorId = orderDetails!!.restaurantId.toString()
                        vendorType = "Restaurant"
                        textViewName.text = orderDetails!!.restaurant.name
                        Glide.with(activity!!)
                                .load(orderDetails!!.restaurant.banner)
                                /*.apply(RequestOptions().placeholder(R.drawable.ic_user))*/
                                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                                .into(imageViewDriver)
                      /*  editTextComment.clearText()
                        ratingBar.count = 1
                        textViewName.text = orderDetails.restaurant.name
                        Glide.with(activity!!)
                                .load(orderDetails.restaurant.banner)
                                .apply(RequestOptions().placeholder(R.drawable.ic_user))
                                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                                .into(imageViewDriver)*/
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

        setData(orderDetails)

        buttonSubmit.setOnClickListener {
            showLoader(true)
            if (isRestaurantRating.not()) {
                ratingViewModel.giveRating(orderDetails.id.toString(), vendorId, vendorType, ratingBar.count, getText(editTextComment))
            } else {
                ratingViewModel.giveRatingRestaurant(orderDetails.id.toString(), vendorId, vendorType, ratingBar.count, getText(editTextComment))
            }
            //if (orderDetails.deliveryType == ConstantApp.PICKUP ) {
              //  ratingViewModel.giveRatingRestaurant(orderDetails.id.toString(), orderDetails.restaurantId.toString(), "Restaurant", ratingBar.count, getText(editTextComment))
            //} else if (orderDetails.deliveryType == ConstantApp.DELIVERY) {
                //ratingViewModel.giveRating(orderDetails.id.toString(), orderDetails.driverId.toString(), "Driver", ratingBar.count, getText(editTextComment))
//            }
        }
    }

    private fun setData(orderDetail: OrderDetail) {
        if (orderDetails.deliveryType == ConstantApp.DELIVERY) {
            vendorId = orderDetail.driverId.toString()
            vendorType = "Driver"

            textViewName.text = orderDetail.driverDetails.name
            Glide.with(activity!!)
                    .load(orderDetail.driverDetails.profileImage)
                    /*.apply(RequestOptions().placeholder(R.drawable.ic_user))*/
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .into(imageViewDriver)
        } else {
            isRestaurantRating = true
            vendorId = orderDetail.restaurantId.toString()
            vendorType = "Restaurant"
            textViewName.text = orderDetail.restaurant.name
            Glide.with(activity!!)
                    .load(orderDetail.restaurant.banner)
                    /*.apply(RequestOptions().placeholder(R.drawable.ic_user))*/
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .into(imageViewDriver)
        }
    }
}