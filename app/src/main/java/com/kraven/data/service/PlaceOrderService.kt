package com.kraven.data.service

import com.kraven.data.URLFactory
import com.kraven.data.pojo.Parameters
import com.kraven.data.pojo.ResponseBody
import com.kraven.ui.home.model.*
import com.kraven.ui.my.order.model.OrderList
import com.kraven.ui.order.beverage.model.Beverage
import com.kraven.ui.my.order.model.BeverageHistory
import com.kraven.ui.my.order.model.BeverageHistoryDetails
import com.kraven.ui.notification.model.Notifications
import com.kraven.ui.order.beverage.model.QuantityTypeList
import com.kraven.ui.order.beverage.model.SpecialBeverage
import com.kraven.ui.order.beverage.model.SpecialBeverageDetails
import com.kraven.ui.order.food.model.HostedPageOrder
import com.kraven.ui.payment.model.CardResponse
import com.kraven.ui.payment.model.HostedPageModel
import com.kraven.ui.payment.model.TransactionDetails
import com.kraven.ui.payment.setting.model.Card
import com.kraven.ui.setting.model.Title
import com.kraven.ui.track.model.OrderDetail
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST
import java.net.URL

interface PlaceOrderService {


    @POST(URLFactory.Method.PLACEORDER)
    fun placeOrder(@Body parameters: HashMap<String,Any>): Single<ResponseBody<HostedPageOrder>>

    @POST(URLFactory.Method.ORDER_BEVERAGE_PLACEORDER)
    fun placeOrderBeverage(@Body parameters: HashMap<String,Any>): Single<ResponseBody<HostedPageOrder>>


    @POST(URLFactory.Method.SPECIAL_BEVERAGE_PLACEORDER)
    fun placeSpecialBeverageOrder(@Body parameters: HashMap<String, Any>):Single<ResponseBody<Any>>


    @POST(URLFactory.Method.HOSTEDPAGEPAYMENT)
    fun hostedPagePayment(@Body hashMapOf: HashMap<String, String>): Single<ResponseBody<HostedPageModel>>

    @POST(URLFactory.Method.CARD_PAYMENT)
    fun cardPayment(@Body hashMapOf: java.util.HashMap<String, Any>):Single<ResponseBody<CardResponse>>

    @POST(URLFactory.Method.PAY_SPECIAL_BEVERAGE)
    fun paySpecialBeverageOrder(@Body parameters: HashMap<String, Any>):Single<ResponseBody<Any>>

    @POST(URLFactory.Method.GET_TRANSACTION_STATUS)
    fun getTransactionStatus(@Body parameters: HashMap<String, Any>):Single<ResponseBody<TransactionDetails>>

}