package com.kraven.ui.my.order.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BeverageHistory(
        @SerializedName("address_type")
        val addressType: String?=null,
        @SerializedName("base_fare")
        val baseFare: Float?=null,
        @SerializedName("beverage")
        val beverage: Beverage?=null,
        @SerializedName("beverage_id")
        val beverageId: Int?=null,
        @SerializedName("beverage_share")
        val beverageShare: Float?=null,
        @SerializedName("beverage_status")
        val beverageStatus: String?=null,
        @SerializedName("cancel_by")
        val cancelBy: String?=null,
        @SerializedName("cancel_reason")
        val cancelReason: String?=null,
        @SerializedName("delivered_time")
        val deliveredTime: String?=null,
        @SerializedName("delivery_address")
        val deliveryAddress: String?=null,
        @SerializedName("delivery_charge")
        val deliveryCharge: Float?=null,
        @SerializedName("delivery_latitude")
        val deliveryLatitude: String?=null,
        @SerializedName("delivery_longitude")
        val deliveryLongitude: String?=null,
        @SerializedName("delivery_type")
        val deliveryType: String?=null,
        @SerializedName("discount")
        val discount: Float?=null,
        @SerializedName("distance")
        val distance: String?=null,
        @SerializedName("driver_id")
        val driverId: Int?=null,
        @SerializedName("driver_share")
        val driverShare: Float?=null,
        @SerializedName("id")
        val id: Int?=null,
        @SerializedName("insertdate")
        val insertdate: String?=null,
        @SerializedName("minimum_miles")
        val minimumMiles: Int?=null,
        @SerializedName("order_datetime")
        val orderDatetime: String?=null,
        @SerializedName("order_items")
        val orderItems: List<OrderItem>?=null,
        @SerializedName("order_size")
        val orderSize: String?=null,
        @SerializedName("order_type")
        val orderType: String?=null,
        @SerializedName("owner_share")
        val ownerShare: Float?=null,
        @SerializedName("payment_method")
        val paymentMethod: String?=null,
        @SerializedName("pending_time")
        val pendingTime: String?=null,
        @SerializedName("per_miles_rate")
        val perMilesRate: Int?=null,
        @SerializedName("pickedup_time")
        val pickedupTime: String?=null,
        @SerializedName("pickup_address")
        val pickupAddress: String?=null,
        @SerializedName("pickup_latitude")
        val pickupLatitude: String?=null,
        @SerializedName("pickup_longitude")
        val pickupLongitude: String?=null,
        @SerializedName("preparation_minutes")
        val preparationMinutes: Int?=null,
        @SerializedName("preparing_time")
        val preparingTime: String?=null,
        @SerializedName("promocode_id")
        val promocodeId: Int?=null,
        @SerializedName("share_percent")
        val sharePercent: String?=null,
        @SerializedName("started_time")
        val startedTime: String?=null,
        @SerializedName("status")
        val status: String?=null,
        @SerializedName("sub_total")
        val subTotal: Float?=null,
        @SerializedName("tip")
        val tip: Float?=null,
        @SerializedName("tip_percent")
        val tipPercent: Int?=null,
        @SerializedName("total")
        val total: Float?=null,
        @SerializedName("total_vat")
        val totalVat: Float?=null,
        @SerializedName("transaction_id")
        val transactionId: String?=null,
        @SerializedName("user_id")
        val userId: Int?=null,
        @SerializedName("vat_percent")
        val vatPercent: Int?=null,
        @SerializedName("wallet")
        val wallet: Float?=null
) : Parcelable {
        @Parcelize
    data class OrderItem(
            @SerializedName("id")
            val id: Int?=null,
            @SerializedName("image")
            val image: String?=null,
            @SerializedName("item")
            val item: String?=null,
            @SerializedName("menu")
            val menu: String?=null,
            @SerializedName("milliliter")
            val milliliter: Int?=null,
            @SerializedName("price")
            val price: Double?=null,
            @SerializedName("qty")
            val qty: Int?=null,
            @SerializedName("special_note")
            val specialNote: String?=null
    ) : Parcelable

        @Parcelize
    data class Beverage(
            @SerializedName("address")
            val address: String?=null,
            @SerializedName("banner")
            val banner: String?=null,
            @SerializedName("delivery_type")
            val deliveryType: String?=null,
            @SerializedName("id")
            val id: Int?=null,
            @SerializedName("is_favourite")
            val isFavourite: Int?=null,
            @SerializedName("min_order_amount")
            val minOrderAmount: Int?=null,
            @SerializedName("name")
            val name: String?=null,
            @SerializedName("rating")
            val rating: Int?=null,
            @SerializedName("reviews")
            val reviews: Int?=null
    ) : Parcelable
}