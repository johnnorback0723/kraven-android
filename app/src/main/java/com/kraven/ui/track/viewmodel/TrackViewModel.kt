package com.kraven.ui.track.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.kraven.data.URLFactory
import com.kraven.data.pojo.Parameters
import com.kraven.data.repository.GoogleRepository
import com.kraven.data.repository.UserRepository
import com.kraven.delivery.model.google.Directions
import com.kraven.model.google.DistanceM
import com.kraven.ui.base.APILiveData
import com.kraven.ui.base.BaseViewModel
import com.kraven.ui.my.order.model.BeverageHistoryDetails
import com.kraven.ui.my.order.model.OrderList
import com.kraven.ui.order.beverage.model.Beverage
import com.kraven.ui.track.model.OrderDetail
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class TrackViewModel @Inject constructor(private val userRepository: UserRepository,
                                         private val googleRepository: GoogleRepository):BaseViewModel() {

    val getOrderHistory = APILiveData<List<OrderList>>()

    val getOrderDetails = APILiveData<OrderDetail>()
    val getDistance = APILiveData<DistanceM>()
    val getOrderBeverageDetails = APILiveData<BeverageHistoryDetails>()
    val getDriverLatLngLiveData = APILiveData<Any>()

    val directionLiveData by lazy { MutableLiveData<List<Directions.Routes>>() }

    fun getOrderHistory(orderType: String, page: String) {
        //userRepository.getOrderHistory(Parameters(orderType = orderType, page = page)).subscribe(withLiveData(getOrderHistory))
    }

    fun getDirection(startLatLng: LatLng, endLatLong: LatLng) {
        googleRepository.getPath(startLatLng, endLatLong).subscribe(object : SingleObserver<List<Directions.Routes>> {
            override fun onSuccess(t: List<Directions.Routes>) {
                directionLiveData.postValue(t)
            }

            override fun onSubscribe(d: Disposable) {

            }

            override fun onError(e: Throwable) {
                directionLiveData.postValue(null)
            }

        })
    }


    fun getOrderDetails(orderId:String){
        userRepository.getOrderDetails(Parameters(orderId = orderId)).subscribe(withLiveData(getOrderDetails))
    }

    fun getOrderBeverageDetails(orderId:String){
        userRepository.getOrderBeverageDetails(Parameters(orderId = orderId)).subscribe(withLiveData(getOrderBeverageDetails))
    }

    fun getDriverLatLong(parameters: Parameters) {
        userRepository.getDriverLatLong(parameters).subscribe(withLiveData(getDriverLatLngLiveData))
    }
}