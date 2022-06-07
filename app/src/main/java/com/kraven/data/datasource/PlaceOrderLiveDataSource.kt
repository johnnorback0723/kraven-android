package com.kraven.data.datasource


import android.content.Context

import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.kraven.core.AppExecutors
import com.kraven.core.Session
import com.kraven.core.StatusCode
import com.kraven.data.pojo.DataWrapper
import com.kraven.data.pojo.Parameters
import com.kraven.data.repository.PlaceOrderRepository
import com.kraven.data.repository.UserRepository
import com.kraven.data.service.PlaceOrderService
import com.kraven.data.service.UserService
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
import okhttp3.OkHttpClient
import com.kraven.ui.viewmodel.Address
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaceOrderLiveDataSource @Inject constructor(private val userService: PlaceOrderService, val appExecutors: AppExecutors,
                                                   private val context: Context, private val gson: Gson) :
        BaseDataSource(), PlaceOrderRepository {


    override fun placeOrder(parameters: HashMap<String, Any>): Single<DataWrapper<HostedPageOrder>> {
        return execute(userService.placeOrder(parameters))
    }

    override fun placeOrderBeverage(parameters: HashMap<String, Any>): Single<DataWrapper<HostedPageOrder>> {
        return execute(userService.placeOrderBeverage(parameters))
    }

    override fun placeSpecialBeverageOrder(parameters: HashMap<String, Any>): Single<DataWrapper<Any>> {
        return execute(userService.placeSpecialBeverageOrder(parameters))
    }

    override fun paySpecialBeverageOrder(parameters: HashMap<String, Any>): Single<DataWrapper<Any>> {
        return execute(userService.paySpecialBeverageOrder(parameters))
    }

    override fun cardPayment(hashMapOf: java.util.HashMap<String, Any>): Single<DataWrapper<CardResponse>> {
        return execute(userService.cardPayment(hashMapOf))
    }

    override fun hostedPagePayment(hashMapOf: HashMap<String, String>): Single<DataWrapper<HostedPageModel>> {
        return execute(userService.hostedPagePayment(hashMapOf))
    }

    override fun getTransactionStatus(hashMapOf: HashMap<String, Any>): Single<DataWrapper<TransactionDetails>> {
        return execute(userService.getTransactionStatus(hashMapOf))
    }

}
