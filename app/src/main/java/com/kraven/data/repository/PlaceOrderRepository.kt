package com.kraven.data.repository


import com.kraven.data.pojo.DataWrapper
import com.kraven.ui.order.food.model.HostedPageOrder
import com.kraven.ui.payment.model.CardResponse
import com.kraven.ui.payment.model.HostedPageModel
import com.kraven.ui.payment.model.TransactionDetails
import io.reactivex.Single

interface PlaceOrderRepository {

    fun placeOrder(parameters: HashMap<String, Any>): Single<DataWrapper<HostedPageOrder>>

    fun placeOrderBeverage(parameters: HashMap<String, Any>): Single<DataWrapper<HostedPageOrder>>

    fun placeSpecialBeverageOrder(parameters:HashMap<String,Any>):Single<DataWrapper<Any>>

    fun paySpecialBeverageOrder(parameters: HashMap<String, Any>):Single<DataWrapper<Any>>

    fun hostedPagePayment(hashMapOf: HashMap<String, String>): Single<DataWrapper<HostedPageModel>>

    fun cardPayment(hashMapOf: HashMap<String, Any>):Single<DataWrapper<CardResponse>>

    fun getTransactionStatus(hashMapOf: HashMap<String, Any>):Single<DataWrapper<TransactionDetails>>

}