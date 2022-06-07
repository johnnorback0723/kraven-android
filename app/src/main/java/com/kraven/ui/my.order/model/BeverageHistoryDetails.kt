package com.kraven.ui.my.order.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.kraven.ui.home.model.ToppingListItem
import com.kraven.ui.home.model.ToppingsItems
import com.kraven.ui.track.model.DriverDetails
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BeverageHistoryDetails(
        @SerializedName("tollfee")
        val tollFee: String? = null,
        @SerializedName("beverage_review")
        val beverageReview: Int? = null,
        @SerializedName("driver_review")
        val driverReview: Int? = null,
        @SerializedName("address_type")
        val addressType: String? = null,
        @SerializedName("base_fare")
        val baseFare: Float? = null,
        @SerializedName("beverage")
        val beverage: Beverage? = null,
        @SerializedName("card_token")
        val cardToken: String? = null,
        @SerializedName("beverage_id")
        val beverageId: Int? = null,
        @SerializedName("beverage_share")
        val beverageShare: String? = null,
        @SerializedName("beverage_status")
        val beverageStatus: String? = null,
        @SerializedName("cancel_by")
        val cancelBy: String? = null,
        @SerializedName("cancel_reason")
        val cancelReason: String? = null,
        @SerializedName("confirmed_time")
        val confirmedTime: String? = null,
        @SerializedName("delivered_time")
        val deliveredTime: String? = null,
        @SerializedName("delivery_address")
        val deliveryAddress: String? = null,
        @SerializedName("delivery_charge")
        val deliveryCharge: Float? = null,
        @SerializedName("delivery_commission")
        val deliveryCommission: Float? = null,
        @SerializedName("delivery_latitude")
        val deliveryLatitude: String? = null,
        @SerializedName("delivery_longitude")
        val deliveryLongitude: String? = null,
        @SerializedName("delivery_type")
        val deliveryType: String? = null,
        @SerializedName("discount")
        val discount: Float = 0f,
        @SerializedName("distance")
        val distance: String? = null,
        @SerializedName("driver_id")
        val driverId: Int? = null,
        @SerializedName("driver_share")
        val driverShare: Float? = null,
        @SerializedName("id")
        val id: Int? = null,
        @SerializedName("insertdate")
        val insertdate: String? = null,
        @SerializedName("minimum_miles")
        val minimumMiles: Int? = null,
        @SerializedName("order_datetime")
        val orderDatetime: String? = null,
        @SerializedName("order_items")
        val orderItems: List<OrderItem>? = null,
        @SerializedName("order_size")
        val orderSize: String? = null,
        @SerializedName("order_type")
        val orderType: String? = null,
        @SerializedName("owner_share")
        val ownerShare: Float? = null,
        @SerializedName("payment_method")
        val paymentMethod: String? = null,
        @SerializedName("pending_time")
        val pendingTime: String? = null,
        @SerializedName("per_miles_rate")
        val perMilesRate: Int? = null,
        @SerializedName("pickedup_time")
        val pickedupTime: String? = null,
        @SerializedName("pickup_address")
        val pickupAddress: String? = null,
        @SerializedName("pickup_latitude")
        val pickupLatitude: String? = null,
        @SerializedName("pickup_longitude")
        val pickupLongitude: String? = null,
        @SerializedName("preparation_minutes")
        val preparationMinutes: Int? = null,
        @SerializedName("preparing_time")
        val preparingTime: String? = null,
        @SerializedName("promocode_id")
        val promocodeId: Int? = null,
        @SerializedName("share_percent")
        val sharePercent: String? = null,
        @SerializedName("started_time")
        val startedTime: String? = null,
        @SerializedName("status")
        val status: String? = null,
        @SerializedName("sub_total")
        val subTotal: Float? = null,
        @SerializedName("tip")
        val tip: Float? = null,
        @SerializedName("tip_percent")
        val tipPercent: Float? = null,
        @SerializedName("total")
        val total: Float? = null,
        @SerializedName("total_vat")
        val totalVat: Float? = null,
        @SerializedName("transaction_id")
        val transactionId: String? = null,
        @SerializedName("user_details")
        val userDetails: UserDetails? = null,
        @SerializedName("user_id")
        val userId: Int? = null,
        @SerializedName("vat_percent")
        val vatPercent: Float? = null,
        @SerializedName("wallet")
        val wallet: Float? = null,
        @SerializedName("order_driver")
        val orderDriver: OrderDriver? = null,
        @SerializedName("driver_details")
        val driverDetails: DriverDetails
) : Parcelable {


    @Parcelize
    data class OrderItem(
            @SerializedName("id")
            val id: Int? = null,
            @SerializedName("image")
            val image: String? = null,
            @SerializedName("item")
            val item: String? = null,
            @SerializedName("menu")
            val menu: String? = null,
            @SerializedName("milliliter")
            val milliliter: Int? = null,
            @SerializedName("price")
            val price: Float? = null,
            @SerializedName("qty")
            val qty: Int? = null,
            @SerializedName("status")
            val status: String? = null,
            @SerializedName("item_id")
            val itemId: String? = null,
            @SerializedName("special_note")
            val specialNote: String? = null,
            @SerializedName("toppings")
            val toppings: List<ToppingListItem>? = null
    ) : Parcelable

    @Parcelize
    data class PassOrderItem(
            @SerializedName("id")
            var id: Int? = null,
            @SerializedName("status")
            var status: String? = null,
            @SerializedName("image")
            var image: String? = null,
            @SerializedName("item")
            var item: String? = null,
            @SerializedName("menu")
            var menu: String? = null,
            @SerializedName("milliliter")
            var milliliter: Int? = null,
            @SerializedName("price")
            var price: Float? = null,
            @SerializedName("qty")
            var qty: Int? = null,
            @SerializedName("item_id")
            var itemId: String? = null,
            @SerializedName("special_note")
            var specialNote: String? = null,
            @SerializedName("toppings")
            var toppings: ArrayList<ToppingsItems>? = null
    ) : Parcelable

    @Parcelize
    data class Beverage(
            @SerializedName("address")
            val address: String? = null,
            @SerializedName("banner")
            val banner: String? = null,
            @SerializedName("delivery_type")
            val deliveryType: String? = null,
            @SerializedName("id")
            val id: Int? = null,
            @SerializedName("is_favourite")
            val isFavourite: Int? = null,
            @SerializedName("min_order_amount")
            val minOrderAmount: Int? = null,
            @SerializedName("name")
            val name: String? = null,
            @SerializedName("rating")
            val rating: Int? = null,
            @SerializedName("reviews")
            val reviews: Int? = null
    ) : Parcelable

    @Parcelize
    data class UserDetails(
            @SerializedName("country_code")
            val countryCode: String? = null,
            @SerializedName("email")
            val email: String? = null,
            @SerializedName("fname")
            val fname: String? = null,
            @SerializedName("id")
            val id: Int? = null,
            @SerializedName("latitude")
            val latitude: String? = null,
            @SerializedName("lname")
            val lname: String? = null,
            @SerializedName("longitude")
            val longitude: String? = null,
            @SerializedName("phone")
            val phone: String? = null,
            @SerializedName("profile_image")
            val profileImage: String? = null,
            @SerializedName("signup_type")
            val signupType: String? = null,
            @SerializedName("wallet")
            val wallet: Float? = null
    ) : Parcelable

        @Parcelize
        data class OrderDriver(@SerializedName("id")
                          val id: String? = null,
                          @SerializedName("order_id")
                          val orderId: String? = null,
                          @SerializedName("user_id")
                          val userId: String? = null,
                          @SerializedName("restaurant_id")
                          val restaurantId: String? = null,
                          @SerializedName("driver_id")
                          val driverId: String? = null,
                          @SerializedName("requested_at")
                          val requestedAt: String? = null,
                          @SerializedName("accepted_at")
                          val acceptedAt: String? = null,
                          @SerializedName("started_at")
                          val startedAt: String? = null,
                          @SerializedName("pickedup_at")
                          val pickedupAt: String? = null,
                          @SerializedName("delivered_at")
                          val deliveredAt: String? = null,
                          @SerializedName("rejected_at")
                          val rejectedAt: String? = null,
                          @SerializedName("cancelled_at")
                          val cancelledAt: String? = null,
                          @SerializedName("cancel_reason")
                          val cancelReason: String? = null,
                          @SerializedName("status")
                          val status: String? = null,
                          @SerializedName("insertdate")
                          val insertdate: String? = null,
                          @SerializedName("driver")
                          val driver: String? = null) : Parcelable
}