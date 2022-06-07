package com.kraven.ui.wallet.model


import com.google.gson.annotations.SerializedName

data class Transaction(@SerializedName("transaction_id")
                       val transactionId: String = "",
                       @SerializedName("amount")
                       val amount: Float = 0F,
                       @SerializedName("user_id")
                       val userId: Int = 0,
                       @SerializedName("description")
                       val description: String = "",
                       @SerializedName("insertdate")
                       val insertdate: String = "",
                       @SerializedName("id")
                       val id: Int = 0,
                       @SerializedName("type")
                       val type: String = "",
                       @SerializedName("order_id")
                       val orderId: Int = 0,
                       @SerializedName("status")
                       val status: String = "")


