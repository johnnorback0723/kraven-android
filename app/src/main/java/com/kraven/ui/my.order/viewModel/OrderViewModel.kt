package com.kraven.ui.my.order.viewModel


import android.util.Log
import com.kraven.data.pojo.Parameters
import com.kraven.data.repository.UserRepository
import com.kraven.ui.base.APILiveData
import com.kraven.ui.base.BaseViewModel
import com.kraven.ui.my.order.model.OrderList
import com.kraven.ui.my.order.model.BeverageHistory

import javax.inject.Inject

class OrderViewModel @Inject constructor(private val userRepository: UserRepository) : BaseViewModel() {

    val getOrderHistory = APILiveData<List<OrderList>>()
    val getOrderHistoryFuture = APILiveData<List<OrderList>>()
    val cancelOrder = APILiveData<Any>()
    val cancelOrderBeverage = APILiveData<Any>()
    val getOrderBeverageHistory  =APILiveData<List<BeverageHistory>>()

    fun getOrderHistory(orderType: String, page: String) {
        userRepository.getOrderHistory(Parameters(orderType = orderType, page = page)).subscribe(withLiveData(getOrderHistory))
    }

    fun getOrderHistoryFuture(orderType: String, page: String) {
        userRepository.getOrderHistory(Parameters(orderType = orderType, page = page)).subscribe(withLiveData(getOrderHistoryFuture))
    }

    fun getOrderBeverageHistory(page:String){
        userRepository.getOrderBeverageHistory(page).subscribe(withLiveData(getOrderBeverageHistory))
    }

    fun cancelOrder(orderId:String){
        userRepository.cancelOrder(Parameters(orderId = orderId)).subscribe(withLiveData(cancelOrder))
    }

    fun cancelOrderBeverage(orderId:String){
        userRepository.cancelOrderBeverage(Parameters(orderId = orderId)).subscribe(withLiveData(cancelOrderBeverage))
    }
}