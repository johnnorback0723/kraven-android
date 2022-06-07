package com.kraven.ui.notification.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
data class Notifications(
        @SerializedName("beverage_status")
        val beverageStatus: Any?,
        @SerializedName("food_status")
        val foodStatus: String?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("insertdate")
        val insertdate: String?,
        @SerializedName("message")
        val message: String?,
        @SerializedName("order_id")
        val orderId: String?,
        @SerializedName("order_type")
        val orderType: String?,
        @SerializedName("status")
        val status: String?,
        @SerializedName("user_id")
        val userId: Int?,
        @SerializedName("user_type")
        val userType: String?
)