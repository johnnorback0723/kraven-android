package com.kraven.data.mvnsource

import com.google.gson.annotations.SerializedName

data class HostedPageOrderResponse(
    @SerializedName("code")
    var code: Int = 0, // 1
    @SerializedName("data")
    var `data`: String = "", // There is no response data for the Order ID provided
    @SerializedName("message")
    var message: String = "" // success
)