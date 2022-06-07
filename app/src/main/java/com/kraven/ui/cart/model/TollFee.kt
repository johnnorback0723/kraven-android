package com.kraven.ui.cart.model


import com.google.gson.annotations.SerializedName

data class TollFee(
    @SerializedName("id")
    var id: Int? = null,
    @SerializedName("location")
    var location: String? = null,
    @SerializedName("tollfee")
    var tollfee: Int? = null
)