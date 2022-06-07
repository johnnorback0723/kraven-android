package com.kraven.ui.address.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Cay(@SerializedName("id")
                   val cayId: String = "",
               @SerializedName("location")
                   val location: String = "",
               @SerializedName("cayfee")
                   val cayFee: String = "",
               @SerializedName("latitude")
                   val latitude: String = "",
               @SerializedName("longitude")
                   val longitude: String = "",
               @SerializedName("status")
                   val status: String = "",
               @SerializedName("insertdate")
                   val insertdate: String = "") : Parcelable