package com.kraven.ui.cart.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SelectAddress(@SerializedName("address_type")
                         @Expose
                         var addressType: String?,
                         @SerializedName("address")
                         @Expose
                         var address: String?) : Parcelable