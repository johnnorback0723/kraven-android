package com.kraven.ui.bartender.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class BartenderOrderList(@SerializedName("order_id")
                         @Expose
                         var orderId: String? = null,
                         @SerializedName("order_status")
                         @Expose
                         var orderStatus: String? = null,
                         @SerializedName("date_time")
                         @Expose
                         var dateTime: String? = null,
                         @SerializedName("price")
                         @Expose
                         var price: String? = null) : Parcelable {
}