package com.kraven.ui.authentication.model


import com.google.gson.annotations.SerializedName

data class Island(
    @SerializedName("id")
    var id: Int? = null,
    @SerializedName("image")
    var image: String? = null,
    @SerializedName("name")
    var name: String? = null
)