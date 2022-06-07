package com.kraven.ui.track.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.kraven.ui.home.model.DishesItem
import com.kraven.ui.home.model.ToppingListItem
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
data class OrderDetail(@SerializedName("tollfee")
                       val tollFee: String? = null,
                       @SerializedName("driver_id")
                       val driverId: Int = 0,
                       @SerializedName("delivery_address")
                       val deliveryAddress: String = "",
                       @SerializedName("user_details")
                       val userDetails: UserDetails,
                       @SerializedName("pickup_address")
                       val pickupAddress: String = "",
                       @SerializedName("discount")
                       val discount: Float = 0F,
                       @SerializedName("delivery_type")
                       val deliveryType: String = "",
                       @SerializedName("tip")
                       val tip: Float = 0F,
                       @SerializedName("id")
                       val id: Int = 0,
                       @SerializedName("order_type")
                       val orderType: String = "",
                       @SerializedName("payment_method")
                       val paymentMethod: String = "",
                       @SerializedName("share_percent")
                       val sharePercent: String = "",
                       @SerializedName("transaction_id")
                       val transactionId: String = "",
                       @SerializedName("pickup_longitude")
                       val pickupLongitude: String = "",
                       @SerializedName("total_vat")
                       val totalVat: Float = 0F,
                       @SerializedName("delivery_charge")
                       val deliveryCharge: Float = 0F,
                       @SerializedName("delivery_commission")
                       val deliveryCommission: Float = 0F,
                       @SerializedName("user_id")
                       val userId: Int = 0,
                       @SerializedName("driver_details")
                       val driverDetails: DriverDetails,
                       @SerializedName("delivered_time")
                       val deliveredTime: String = "",
                       @SerializedName("delivery_longitude")
                       val deliveryLongitude: String = "",
                       @SerializedName("status")
                       val status: String = "",
                       @SerializedName("card_token")
                       val cardToken: String? = null,
                       @SerializedName("order_dishes")
                       val orderDishes: List<DishesItem>?,
                       @SerializedName("confirmed_time")
                       val confirmedTime: String = "",
                       @SerializedName("distance")
                       val distance: Float = 0F,
                       @SerializedName("restaurant_status")
                       val restaurantStatus: String = "",
                       @SerializedName("restaurant_id")
                       val restaurantId: Int = 0,
                       @SerializedName("order_size")
                       val orderSize: String = "",
                       @SerializedName("pending_time")
                       val pendingTime: String = "",
                       @SerializedName("preparation_minutes")
                       val preparationMinutes: Int = 0,
                       @SerializedName("total")
                       val total: Float = 0F,
                       @SerializedName("pickup_latitude")
                       val pickupLatitude: String = "",
                       @SerializedName("cancel_by")
                       val cancelBy: String = "",
                       @SerializedName("delivery_latitude")
                       val deliveryLatitude: String = "",
                       @SerializedName("owner_share")
                       val ownerShare: Float = 0F,
                       @SerializedName("pickedup_time")
                       val pickedupTime: String = "",
                       @SerializedName("minimum_miles")
                       val minimumMiles: Float = 0F,
                       @SerializedName("wallet")
                       val wallet: Float = 0F,
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
                       @SerializedName("cancel_reason")
                       val cancelReason: String = "",
                       @SerializedName("vat_percent")
                       val vatPercent: Float = 0F,
                       @SerializedName("sub_total")
                       val subTotal: Float = 0F,
                       @SerializedName("restaurant_review")
                       val restaurantReview: Int? = null,
                       @SerializedName("driver_review")
                       val driverReview: Int? = null,
                       @SerializedName("order_driver")
                       val orderDriver: OrderDriver? = null,
                       @SerializedName("insertdate")
                       val insertdate: String = "") : Parcelable

@Parcelize
class OrderDriver(@SerializedName("id")
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


@Parcelize
class DishesItems : Parcelable {
    @SerializedName("id")
    var id: String? = null
    @SerializedName("menu_id")
    var menuId: String? = null
    @SerializedName("dish")

    var dish: String? = null
    @SerializedName("total_price")
    var totalPrice: Float? = null

    @SerializedName("special_note")
    var specialNote: String? = null
    @SerializedName("price")
    var price: Float? = 0F
    @SerializedName("qty")
    var qty: Int? = 0
    /*@SerializedName("toppings")
    var toppings: ArrayList<ToppingsItems>? = null*/

    /*@SerializedName("toppings")
    var toppings: ToppingsItems? = null*/

    @SerializedName("toppings")
    var toppings: MutableMap<String, ArrayList<ToppingListItem>>? = null

    @SerializedName("menu")
    var menu: String? = null
    @SerializedName("status")
    var status: String? = null
    @SerializedName("food")
    var food: String? = null
    @SerializedName("name")
    var name: String? = null

    @SerializedName("dishPriceTopping")
    var dishPriceTopping: Float? = null
}


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
data class DriverDetails(@SerializedName("fname")
                         val fname: String? = null,
                         @SerializedName("country_code")
                         val countryCode: String? = null,
                         @SerializedName("lname")
                         val lname: String? = null,
                         @SerializedName("profile_image")
                         val profileImage: String? = null,
                         @SerializedName("phone")
                         val phone: String? = null,
                         @SerializedName("latitude")
                         val latitude: String? = null,
                         @SerializedName("name")
                         val name: String? = null,
                         @SerializedName("id")
                         val id: Int? = null,
                         @SerializedName("email")
                         val email: String? = null,
                         @SerializedName("longitude")
                         val longitude: String? = null) : Parcelable

@Parcelize
data class UserDetails(@SerializedName("fname")
                       val fname: String = "",
                       @SerializedName("country_code")
                       val countryCode: String = "",
                       @SerializedName("lname")
                       val lname: String = "",
                       @SerializedName("profile_image")
                       val profileImage: String = "",
                       @SerializedName("wallet")
                       val wallet: Float = 0F,
                       @SerializedName("phone")
                       val phone: String = "",
                       @SerializedName("latitude")
                       val latitude: String = "",
                       @SerializedName("id")
                       val id: Int = 0,
                       @SerializedName("email")
                       val email: String = "",
                       @SerializedName("longitude")
                       val longitude: String = "",
                       @SerializedName("signup_type")
                       val signupType: String = "") : Parcelable

@Parcelize
data class ExtraItem(@SerializedName("price")
                     val price: Float = 0F,
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


