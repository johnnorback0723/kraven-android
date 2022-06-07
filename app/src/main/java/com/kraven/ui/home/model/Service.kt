package com.kraven.ui.home.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Service(@SerializedName("serviceList")
                   @Expose
                   var serviceList: List<Services>,
                   @SerializedName("restaurantList")
                   @Expose
                   var restaurantList: List<Restaurant>) : Parcelable {

    @Parcelize
    class Services(@SerializedName("image")
                   @Expose
                   @DrawableRes var src: Int? = null,
                   @SerializedName("foodName")
                   @Expose var name: String? = null) : Parcelable

    @Parcelize
    class Restaurant(@SerializedName("image")
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
                     var isOffline: Int? = 0,
                     @SerializedName("isClosed")
                     @Expose
                     var isClose: String? = null,
                     @SerializedName("")
                     @Expose
                     var deliveryType: String? = null) : Parcelable
}




