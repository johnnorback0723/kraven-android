package com.kraven.ui.restaurant.reservation.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Restaurant(@SerializedName("orderId")
                      @Expose
                      var orderId: String? = null,
                      @SerializedName("dateTime")
                      @Expose
                      var dateTime: String? = null,
                      @SerializedName("orderStatus")
                      @Expose
                      var orderStatus: String? = null,
                      @SerializedName("image")
                      @Expose
                      var foodImage: String? = null,
                      @SerializedName("restaurantName")
                      @Expose var restaurantName: String? = null,
                      @SerializedName("restaurantItems")
                      @Expose var restaurantItems: String? = null,
                      @SerializedName("restaurantRating")
                      @Expose var restaurantRating: Int? = null,
                      @SerializedName("fav")
                      @Expose
                      var restaurantFav: String? = null,
                      @SerializedName("restaurantReviews")
                      @Expose
                      var restaurantReviews: String? = null,
                      @SerializedName("isOffline")
                      @Expose
                      var isOffline: Int? = 0) : Parcelable