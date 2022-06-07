package com.kraven.ui.my.order.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrderDishesItem(@SerializedName("special_offer")
                           val specialOffer: Int = 0,
                           @SerializedName("dish")
                           val dish: String = "",
                           @SerializedName("special_note")
                           val specialNote: String = "",
                           @SerializedName("price")
                           val price: Float = 0F,
                           @SerializedName("qty")
                           val qty: Int = 0,
                           @SerializedName("extra")
                           val extra: List<ExtraItem>?,
                           @SerializedName("id")
                           val id: Int = 0,
                           @SerializedName("menu")
                           val menu: String = "",
                           @SerializedName("food")
                           val food: String = "") : Parcelable

@Parcelize
data class Restaurant(@SerializedName("min_order_amount")
                      val minOrderAmount: Int = 0,
                      @SerializedName("address")
                      val address: String = "",
                      @SerializedName("reviews")
                      val reviews: Int = 0,
                      @SerializedName("delivery_type")
                      val deliveryType: String = "",
                      @SerializedName("name")
                      val name: String = "",
                      @SerializedName("rating")
                      val rating: Int = 0,
                      @SerializedName("banner")
                      val banner: String = "",
                      @SerializedName("id")
                      val id: Int = 0,
                      @SerializedName("is_favourite")
                      val isFavourite: Int = 0,
                      @SerializedName("cuisines")
                      val cuisines: List<CuisinesItem>?) : Parcelable

@Parcelize
data class ExtraItem(@SerializedName("price")
                     val price: Int = 0,
                     @SerializedName("name")
                     val name: String = "",
                     @SerializedName("id")
                     val id: Int = 0,
                     @SerializedName("status")
                     val status: String = "") : Parcelable

@Parcelize
data class CuisinesItem(@SerializedName("cuisine")
                        val cuisine: String = "",
                        @SerializedName("id")
                        val id: Int = 0) : Parcelable

@Parcelize
data class OrderList(@SerializedName("driver_id")
                     val driverId: Int = 0,
                     @SerializedName("delivery_address")
                     val deliveryAddress: String = "",
                     @SerializedName("distance")
                     val distance: Float = 0F,
                     @SerializedName("restaurant_status")
                     val restaurantStatus: String = "",
                     @SerializedName("restaurant_id")
                     val restaurantId: Int = 0,
                     @SerializedName("order_size")
                     val orderSize: String = "",
                     @SerializedName("pickup_address")
                     val pickupAddress: String = "",
                     @SerializedName("discount")
                     val discount: Float = 0F,
                     @SerializedName("pending_time")
                     val pendingTime: String = "",
                     @SerializedName("preparation_minutes")
                     val preparationMinutes: Int = 0,
                     @SerializedName("total")
                     val total: String = "%.2f",
                     @SerializedName("pickup_latitude")
                     val pickupLatitude: String = "",
                     @SerializedName("delivery_type")
                     val deliveryType: String = "",
                     @SerializedName("tip")
                     val tip: Float = 0F,
                     @SerializedName("id")
                     val id: Int = 0,
                     @SerializedName("cancel_by")
                     val cancelBy: String = "",
                     @SerializedName("order_type")
                     val orderType: String = "",
                     @SerializedName("delivery_latitude")
                     val deliveryLatitude: String = "",
                     @SerializedName("payment_method")
                     val paymentMethod: String = "",
                     @SerializedName("share_percent")
                     val sharePercent: String = "",
                     @SerializedName("transaction_id")
                     val transactionId: String = "",
                     @SerializedName("owner_share")
                     val ownerShare: Float = 0F,
                     @SerializedName("pickedup_time")
                     val pickedupTime: String = "",
                     @SerializedName("minimum_miles")
                     val minimumMiles: Float = 0F,
                     @SerializedName("wallet")
                     val wallet: Float = 0F,
                     @SerializedName("pickup_longitude")
                     val pickupLongitude: String = "",
                     @SerializedName("address_type")
                     val addressType: String = "",
                     @SerializedName("started_time")
                     val startedTime: String = "",
                     @SerializedName("preparing_time")
                     val preparingTime: String = "",
                     @SerializedName("restaurant")
                     val restaurant: Restaurant,
                     @SerializedName("tip_percent")
                     val tipPercent: Float = 0F,
                     @SerializedName("base_fare")
                     val baseFare: Float = 0F,
                     @SerializedName("order_datetime")
                     val orderDatetime: String = "",
                     @SerializedName("driver_share")
                     val driverShare: Float = 0F,
                     @SerializedName("restaurant_share")
                     val restaurantShare: Float = 0F,
                     @SerializedName("per_miles_rate")
                     val perMilesRate: Float = 0F,
                     @SerializedName("promocode_id")
                     val promocodeId: Float = 0F,
                     @SerializedName("total_vat")
                     val totalVat: Float = 0F,
                     @SerializedName("cancel_reason")
                     val cancelReason: String = "",
                     @SerializedName("delivery_charge")
                     val deliveryCharge: Float = 0F,
                     @SerializedName("user_id")
                     val userId: Int = 0,
                     @SerializedName("delivered_time")
                     val deliveredTime: String = "",
                     @SerializedName("vat_percent")
                     val vatPercent: Float = 0F,
                     @SerializedName("sub_total")
                     val subTotal: Float = 0F,
                     @SerializedName("insertdate")
                     val insertdate: String = "",
                     @SerializedName("delivery_longitude")
                     val deliveryLongitude: String = "",
                     @SerializedName("status")
                     val status: String = "",
                     @SerializedName("order_dishes")
                     val orderDishes: List<OrderDishesItem>?) : Parcelable


