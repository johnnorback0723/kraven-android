package com.kraven.fcm

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AppFirebaseNotification(
        @SerializedName("message")
        @Expose
        var message: String?,

        @SerializedName("flag")
        @Expose
        var tag: String,

        @SerializedName("order_id")
        @Expose
        var orderId: String?

) : Parcelable

