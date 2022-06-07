package com.kraven.ui.portable.bar.rental.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PortableOrderList(@SerializedName("bar_image")
                             @Expose
                             var barimage: String? = null,
                             @SerializedName("order_id")
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