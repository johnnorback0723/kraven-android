package com.kraven.data.mvnsource

import retrofit2.Call
import retrofit2.http.*

interface OrderCancelService {

    @FormUrlEncoded
    @POST("/api/v9/order_food/order-payment-status")
    fun orderPaymentStatus(@Field("orderID") order_id: Int): Call<OrderPaymentStatus>

    @FormUrlEncoded
    @POST("/api/v9/order_food/update-notify-status")
    fun updateNotifyStatus(@Field("orderID") order_id: Int): Call<UpdateNotifyStatus>

    @GET("cron/HostedPageCancel/food/{food_order_id}")
    fun cancelHostedPageFoodOrderRetrofit(@Path("food_order_id") id: String): Call<HostedPageOrderResponse>

    @GET("cron/HostedPageCancel/beverage/{beverage_order_id}")
    fun cancelHostedPageBeverageOrderRetrofit(@Path("beverage_order_id") id: String): Call<HostedPageOrderResponse>

    @GET("cron/cancelOrder/{food_order_id}")
    fun cancelDeclinedFoodOrderRetrofit(@Path("food_order_id") id: String): Call<Any?>?

    @GET("cron/cancelOrderBeverage/{beverage_order_id}")
    fun cancelDeclinedBeverageOrderRetrofit(@Path("beverage_order_id") id: String): Call<Any?>?

    @GET("cron/paymentProcessed/{food_order_id}")
    fun performCardPaymentProcessedFood(@Path("food_order_id") id: String): Call<Any?>?

    @GET("cron/paymentProcessedBeverage/{beverage_order_id}")
    fun performCardPaymentProcessedBeverage(@Path("beverage_order_id") id: String): Call<Any?>?
}