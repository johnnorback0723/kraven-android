package com.kraven.ui.order.food.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HostedPageOrder(
    @SerializedName("order_id")
    var orderId: Int? = null,
) : Parcelable

