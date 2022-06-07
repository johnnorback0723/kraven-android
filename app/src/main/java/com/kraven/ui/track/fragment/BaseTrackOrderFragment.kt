package com.kraven.ui.track.fragment

import android.util.Log
import androidx.lifecycle.Observer
import com.google.android.gms.maps.model.LatLng
import com.kraven.R
import com.kraven.core.AppExecutors
import com.kraven.data.repository.GoogleRepository
import com.kraven.extensions.isSelectedTrue
import com.kraven.extensions.locationPermission
import com.kraven.extensions.viewGone
import com.kraven.extensions.viewShow
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.track.model.OrderDetail
import com.kraven.utils.ConstantApp
import com.kraven.utils.Formatter
import com.kraven.utils.Formatter.YYYY_MM_DD_HH_MM_SS_NEW
import com.kraven.utils.Formatter.diffMinutes
import com.kraven.utils.LocationManager
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.track_order_fragment.*
import javax.inject.Inject
import kotlin.math.round
import kotlin.math.roundToInt


abstract class BaseTrackOrderFragment : BaseFragment() {


    @Inject
    lateinit var googleRepository: GoogleRepository
    var appExecutors = AppExecutors()


    @Inject
    lateinit var locationManager: LocationManager

    private var orderDetails: OrderDetail? = null

    private var disposableDistance: Disposable? = null
    private var disposableDuration: Disposable? = null


    fun getPickUpDate(orderDetails: OrderDetail) {
        this@BaseTrackOrderFragment.orderDetails = orderDetails
        textViewOrderIdLabel.text = getString(R.string.order_id, orderDetails.id.toString())
        groupFoodOnTheWay.viewGone()
        textViewOrderDelivered.text = "Order Completed"
        textViewLabel5.text = "You order has been completed!"
        when (orderDetails.status) {
            ConstantApp.OrderStatus.PENDING -> {
                getPendingData(orderDetails)
            }
            ConstantApp.OrderStatus.CONFIRMED -> {
                getConfirmedData(orderDetails)
            }
            ConstantApp.OrderStatus.PREPARING -> {
                getPreparingData(orderDetails, true)
            }
            ConstantApp.OrderStatus.DELIVERED -> {
                getDeliverData(orderDetails)
            }
        }
        showLoader(false)
    }

    fun getDeliveryData(orderDetails: OrderDetail) {
        this@BaseTrackOrderFragment.orderDetails = orderDetails
        textViewOrderIdLabel.text = getString(R.string.order_id, orderDetails.id.toString())
        groupFoodOnTheWay.viewShow()

        if (orderDetails.status == ConstantApp.OrderStatus.PENDING) {
            getPendingData(orderDetails)
        } else if (orderDetails.status == ConstantApp.OrderStatus.CONFIRMED) {
            getConfirmedData(orderDetails)
        } else if (orderDetails.status == ConstantApp.OrderStatus.PREPARING ||
                orderDetails.orderDriver?.status == ConstantApp.DriverStatus.ACCEPTED ||
                orderDetails.orderDriver?.status == ConstantApp.DriverStatus.STARTED) {
            getPreparingData(orderDetails, false)
        } else if (orderDetails.orderDriver?.status == ConstantApp.DriverStatus.PICKEDUP) {
            getOnTheWayData(orderDetails)
        } else if (orderDetails.status == ConstantApp.OrderStatus.DELIVERED) {
            getDeliverData(orderDetails)
        }

        showLoader(false)
    }

    private fun getConfirmedData(orderDetails: OrderDetail) {
        textViewEstimatedLabel.viewGone()
        textViewEstimatedMilesLabel.viewGone()
        isSelectedTrue(arrayListOf(textViewOrderPlaced, imageViewOrderPlaced, viewLineOne,
                imageViewOrderConfirm, textViewOrderConfirm, viewLineTwo))
        textViewPlacedTime.text = Formatter.utcToLocal(orderDetails.orderDatetime, YYYY_MM_DD_HH_MM_SS_NEW, Formatter.hh_mm_aa)
        textViewOrderProcess.text = getString(R.string.order_process, "0")
        textViewLabel4.text = getString(R.string._20_mins_estimated_delivery_time, "0")
        textViewConfirmTime.text = Formatter.utcToLocal(orderDetails.confirmedTime, YYYY_MM_DD_HH_MM_SS_NEW, Formatter.hh_mm_aa)
    }

    private fun getPendingData(orderDetails: OrderDetail) {
        textViewEstimatedLabel.viewGone()
        textViewEstimatedMilesLabel.viewGone()
        isSelectedTrue(arrayListOf(textViewOrderPlaced, imageViewOrderPlaced, viewLineOne))
        textViewPlacedTime.text = Formatter.utcToLocal(orderDetails.orderDatetime, YYYY_MM_DD_HH_MM_SS_NEW, Formatter.hh_mm_aa)
        textViewOrderProcess.text = getString(R.string.order_process, "0")
        textViewLabel4.text = getString(R.string._20_mins_estimated_delivery_time, "0")
    }

    private fun getPreparingData(orderDetails: OrderDetail, isPickUpDate: Boolean) {

        textViewEstimatedMilesLabel.viewGone()
        isSelectedTrue(arrayListOf(textViewOrderPlaced, imageViewOrderPlaced,
                viewLineOne, imageViewOrderConfirm,
                textViewOrderConfirm, viewLineTwo,
                imageViewOrderProcess, textViewOrderProcess))

        textViewPlacedTime.text = Formatter.utcToLocal(orderDetails.orderDatetime, YYYY_MM_DD_HH_MM_SS_NEW, Formatter.hh_mm_aa)
        textViewConfirmTime.text = Formatter.utcToLocal(orderDetails.confirmedTime, YYYY_MM_DD_HH_MM_SS_NEW, Formatter.hh_mm_aa)
        textViewProcess.text = Formatter.utcToLocal(orderDetails.preparingTime, YYYY_MM_DD_HH_MM_SS_NEW, Formatter.hh_mm_aa)

        if (orderDetails.driverDetails.latitude != null && orderDetails.driverDetails.latitude.isNotEmpty()) {
            if (isPickUpDate) {
                if (locationPermission(activity!!)) {
                    locationManager.locationUpdateLiveData.observe(this, Observer<LatLng> { latLng ->
                        getDistance(latLng.latitude.toString(), latLng.longitude.toString(),
                                orderDetails.pickupLatitude, orderDetails.pickupLongitude) {
                            if (orderDetails.preparationMinutes - diffMinutes(orderDetails.preparingTime) > 0) {
                                /*val duration = it.toIntOrNull()?.plus((orderDetails.preparationMinutes
                                        - diffMinutes(orderDetails.preparingTime)).toInt())
                                textViewEstimatedLabel.text = getString(R.string.estimated_time, duration.toString())
                                textViewOrderProcess.text =
                                        getString(R.string.order_process, ((orderDetails.preparationMinutes - diffMinutes(orderDetails.preparingTime)).toInt()).toString())*/


                                val duration = it.toIntOrNull()?.plus((orderDetails.preparationMinutes))
                                val hours = duration?.div(60)
                                val minutes = duration?.rem(60)
                                if (hours != null) {
                                    if (hours > 1) {
                                        textViewEstimatedLabel.text = if (hours == 1) "Estimated Time : $hours Hour $minutes mins" else "Estimated Time : $hours Hours : $minutes mins"
                                        textViewLabel4.text = if (hours == 1) "$hours Hour : $minutes mins" else "$hours Hours  $minutes mins estimated delivery time."
                                    } else {
                                        textViewEstimatedLabel.text = "Estimated Time : $minutes mins"
                                        textViewLabel4.text = "$minutes mins estimated delivery time."
                                    }
                                } else {
                                    textViewEstimatedLabel.text = getString(R.string.estimated_time, duration.toString())
                                    textViewLabel4.text = getString(R.string._20_mins_estimated_delivery_time, duration.toString())
                                }
                                textViewOrderProcess.text = getString(R.string.order_process, ((orderDetails.preparationMinutes)).toString())
                            } else {
                                val duration = it.toIntOrNull()
                                val hours = duration?.div(60)
                                val minutes = duration?.rem(60)
                                if (hours != null) {
                                    if (hours > 1) {
                                        textViewEstimatedLabel.text = if (hours == 1) "Estimated Time : $hours Hour $minutes mins" else "Estimated Time : $hours Hours $minutes mins"
                                        textViewLabel4.text = if (hours == 1) "$hours Hour $minutes mins" else "$hours Hours $minutes mins estimated delivery time."
                                    } else {
                                        textViewEstimatedLabel.text = "Estimated Time : $minutes mins"
                                        textViewLabel4.text = "$minutes mins estimated delivery time."
                                    }
                                } else {
                                    textViewEstimatedLabel.text = getString(R.string.estimated_time, duration.toString())
                                    textViewLabel4.text = getString(R.string._20_mins_estimated_delivery_time, duration.toString())
                                }

                                textViewOrderProcess.text = getString(R.string.order_process, "0")
                            }
                        }
                        locationManager.locationUpdateLiveData.removeObservers(this)
                    })
                }
            } else {
                getDistance(orderDetails.driverDetails.latitude, orderDetails.driverDetails.longitude!!,
                        orderDetails.pickupLatitude, orderDetails.pickupLongitude) {
                    if (diffMinutes(orderDetails.preparingTime) - orderDetails.preparationMinutes > 0) {
                        /*  val duration = it.toIntOrNull()?.plus((orderDetails.preparationMinutes
                                  - diffMinutes(orderDetails.preparingTime)).toInt())
                          textViewEstimatedLabel.text = getString(R.string.estimated_time, duration.toString())
                          textViewOrderProcess.text =
                                  getString(R.string.order_process, ((orderDetails.preparationMinutes - diffMinutes(orderDetails.preparingTime)).toInt()).toString())*/


                        val duration = it.toIntOrNull()?.plus((orderDetails.preparationMinutes))
                        val hours = duration?.div(60)
                        val minutes = duration?.rem(60)
                        if (hours != null) {
                            if (hours > 1) {
                                textViewEstimatedLabel.text = if (hours == 1) "Estimated Time : $hours Hour $minutes mins" else "Estimated Time : $hours Hours $minutes mins"
                                textViewLabel4.text = if (hours == 1) "$hours Hour $minutes mins" else "$hours Hours $minutes mins estimated delivery time."
                            } else {
                                textViewEstimatedLabel.text = "Estimated Time : $minutes mins"
                                textViewLabel4.text = "$minutes mins estimated delivery time."
                            }
                        } else {
                            textViewEstimatedLabel.text = getString(R.string.estimated_time, duration.toString())
                            textViewLabel4.text = getString(R.string._20_mins_estimated_delivery_time, duration.toString())
                        }
                        textViewOrderProcess.text = getString(R.string.order_process, ((orderDetails.preparationMinutes)).toString())
                    } else {
                        val duration = it.toIntOrNull()
                        val hours = duration?.div(60)
                        val minutes = duration?.rem(60)
                        if (hours != null) {
                            if (hours > 1) {
                                textViewEstimatedLabel.text = if (hours == 1) "Estimated Time : $hours Hour $minutes mins" else "Estimated Time : $hours Hours $minutes mins"
                                textViewLabel4.text = if (hours == 1) "$hours Hour $minutes mins" else "$hours Hours $minutes mins estimated delivery time."
                            } else {
                                textViewEstimatedLabel.text = "Estimated Time : $minutes mins"
                                textViewLabel4.text = "$minutes mins estimated delivery time."
                            }
                        } else {
                            textViewEstimatedLabel.text = getString(R.string.estimated_time, duration.toString())
                            textViewLabel4.text = getString(R.string._20_mins_estimated_delivery_time, duration.toString())
                        }

                        textViewOrderProcess.text = getString(R.string.order_process, "0")
                    }
                }
            }
        } else {
            textViewLabel4.text = getString(R.string._20_mins_estimated_delivery_time, "0")
            textViewOrderProcess.text = getString(R.string.order_process, orderDetails.preparationMinutes.toString())
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposableDistance?.dispose()
        disposableDuration?.dispose()
    }

    private fun getOnTheWayData(orderDetails: OrderDetail) {
        buttonTrack.viewShow()
        isSelectedTrue(arrayListOf(textViewOrderPlaced, imageViewOrderPlaced, viewLineOne, imageViewOrderConfirm,
                textViewOrderConfirm, viewLineTwo, imageViewOrderProcess, textViewOrderProcess, viewLineFour,
                textViewOrderOnTheWay, imageViewOrderOnTheWay))

        textViewPlacedTime.text = Formatter.utcToLocal(orderDetails.orderDatetime, YYYY_MM_DD_HH_MM_SS_NEW, Formatter.hh_mm_aa)
        textViewConfirmTime.text = Formatter.utcToLocal(orderDetails.confirmedTime, YYYY_MM_DD_HH_MM_SS_NEW, Formatter.hh_mm_aa)
        textViewProcess.text = Formatter.utcToLocal(orderDetails.preparingTime, YYYY_MM_DD_HH_MM_SS_NEW, Formatter.hh_mm_aa)
        textViewOnTheWay.text = Formatter.utcToLocal(orderDetails.pickedupTime, YYYY_MM_DD_HH_MM_SS_NEW, Formatter.hh_mm_aa)
        textViewOrderProcess.text = getString(R.string.order_process, "0")

        getDistanceDuration(orderDetails.driverDetails.latitude!!, orderDetails.driverDetails.longitude!!,
                orderDetails.deliveryLatitude, orderDetails.deliveryLongitude) { duration: String, distance: String ->

            textViewLabel4.text = getString(R.string._20_mins_estimated_delivery_time, duration)
            textViewEstimatedLabel.text = "Estimated Delivery Time : ".plus(duration).plus(" mins")
            //getString(R.string.estimated_time, duration)
            textViewEstimatedMilesLabel.text = getString(R.string.estimated_miles, round(distance.toFloat()).toString())
        }
    }

    private fun getDeliverData(orderDetails: OrderDetail) {
        buttonTrack.viewGone()
        textViewEstimatedLabel.viewGone()
        //  textViewEstimatedMilesLabel.viewGone()
        isSelectedTrue(arrayListOf(textViewOrderPlaced, imageViewOrderPlaced, viewLineOne, imageViewOrderConfirm,
                textViewOrderConfirm, viewLineTwo, imageViewOrderProcess, textViewOrderProcess, viewLineFour,
                textViewOrderOnTheWay, imageViewOrderOnTheWay, textViewOrderDelivered, imageViewOrderDelivered))

        textViewPlacedTime.text = Formatter.utcToLocal(orderDetails.orderDatetime, YYYY_MM_DD_HH_MM_SS_NEW, Formatter.hh_mm_aa)
        textViewConfirmTime.text = Formatter.utcToLocal(orderDetails.confirmedTime, YYYY_MM_DD_HH_MM_SS_NEW, Formatter.hh_mm_aa)
        textViewProcess.text = Formatter.utcToLocal(orderDetails.preparingTime, YYYY_MM_DD_HH_MM_SS_NEW, Formatter.hh_mm_aa)

        textViewDelivered.text = Formatter.utcToLocal(orderDetails.deliveredTime, YYYY_MM_DD_HH_MM_SS_NEW, Formatter.hh_mm_aa)
        textViewEstimatedLabel.text = getString(R.string.estimated_time, "0")
        textViewOrderProcess.text = getString(R.string.order_process, "0")
        textViewLabel4.text = getString(R.string._20_mins_estimated_delivery_time, "0")

    }

    private fun getDistance(userLatitude: String, userLongitude: String,
                            restaurantLatitude: String, restaurantLongitude: String, callback: (distance: String) -> Unit) {

        disposableDistance?.dispose()

        disposableDistance = googleRepository.getDistanceMatrix(userLatitude, userLongitude, restaurantLatitude, restaurantLongitude)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(appExecutors.mainThread()))
                .subscribe({ t ->
                    if (t.rows!![0].elements!![0].status == "ZERO_RESULTS") {
                        callback("0")
                    } else {
                        val getTime = t.rows!![0].elements!![0].duration?.value!!.toFloat().div(60).roundToInt()
                        callback(getTime.toString())
                    }
                }, {
                    it.printStackTrace()
                })
    }

    private fun getDistanceDuration(driverLatitude: String, driverLongitude: String,
                                    deliveryLatitude: String,
                                    deliveryLongitude: String, callback: (distance: String, duration: String) -> Unit) {

        disposableDuration?.dispose()
        disposableDuration = googleRepository.getDistanceMatrix(driverLatitude, driverLongitude, deliveryLatitude, deliveryLongitude)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(appExecutors.mainThread()))
                .subscribe({ t ->
                    if (t.rows!![0].elements!![0].status == "ZERO_RESULTS") {
                        callback("0", "0")
                    } else {
                        val getTime = t.rows!![0].elements!![0].duration?.value!!.toFloat().div(60).roundToInt()
                        val getDistance = Formatter.round(t.rows!![0].elements!![0].distance!!.value / 1609.344, 1).toFloat()

                        callback((getTime.toString()),
                                (getDistance.toString()))
                    }
                }, {
                    it.printStackTrace()
                })
    }


}