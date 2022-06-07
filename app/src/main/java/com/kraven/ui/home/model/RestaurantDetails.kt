package com.kraven.ui.home.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BannersItem(@SerializedName("banner")
                       val banner: String = "",
                       @SerializedName("id")
                       val id: Int = 0) : Parcelable

@Parcelize
data class RestaurantDetails(@SerializedName("vat_option")
                             val vatOption: String? = null,
                             @SerializedName("average_prep_time")
                             val averagePrepTime: String? = null,
                             @SerializedName("address")
                             val address: String = "",
                             @SerializedName("distance")
                             val distance: Double = 0.0,
                             @SerializedName("timing")
                             var timing: MutableList<Timing>,
                             @SerializedName("availability_status")
                             val availabilityStatus: String? = null,
                             @SerializedName("rating")
                             val rating: Int = 0,
                             @SerializedName("is_favourite")
                             val isFavourite: Int = 0,
                             @SerializedName("banners")
                             val banners: List<BannersItem>?,
                             @SerializedName("cuisines")
                             val cuisines: List<CuisinesItem>?,
                             @SerializedName("min_order_amount")
                             val minOrderAmount: Float = 0F,
                             @SerializedName("reviews")
                             val reviews: Int = 0,
                             @SerializedName("delivery_type")
                             val deliveryType: String = "",
                             @SerializedName("distance_radius")
                             val distanceRadius: Float = 0F,
                             @SerializedName("name")
                             val name: String = "",
                             @SerializedName("latitude")
                             val latitude: String = "",
                             @SerializedName("longitude")
                             val longitude: String = "",
                             @SerializedName("id")
                             val id: Int = 0,
                             @SerializedName("delivery_base_fare")
                             var delivery_base_fare: Float = 0F,
                             @SerializedName("delivery_minimum_miles")
                             val delivery_minimum_miles: Float = 0F,
                             @SerializedName("delivery_per_miles_rate")
                             val delivery_per_miles_rate: Float = 0F,
                             @SerializedName("menus")
                             val menus: ArrayList<MenusItem>?) : Parcelable

@Parcelize
data class MenusItem(@SerializedName("name")
                     val name: String = "",
                     @SerializedName("id")
                     val id: Int = 0) : Parcelable


