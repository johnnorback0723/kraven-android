package com.kraven.ui.address.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Address(@SerializedName("area")
                   val area: String = "",
                   @SerializedName("country")
                   val country: String = "",
                   @SerializedName("street_name")
                   val streetName: String = "",
                   @SerializedName("address")
                   val address: String = "",
                   @SerializedName("address_type")
                   val addressType: String = "",
                   @SerializedName("user_id")
                   val userId: String = "",
                   @SerializedName("city")
                   val city: String = "",
                   @SerializedName("latitude")
                   val latitude: String = "",
                   @SerializedName("insertdate")
                   val insertDate: String = "",
                   @SerializedName("id")
                   val id: String = "",
                   @SerializedName("landmark")
                   val landmark: String = "",
                   @SerializedName("building_name")
                   val buildingName: String = "",
                   @SerializedName("house")
                   val house: String = "",
                   @SerializedName("longitude")
                   val longitude: String = "",
                   @SerializedName("is_cay")
                   var isCay: String = "",
                   @SerializedName("cay_id")
                   var cayId: String = "",
                   @SerializedName("cayfee")
                   var cayFee: String = "") : Parcelable