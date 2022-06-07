package com.kraven.ui.order.beverage.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BeverageStore(
        @SerializedName("store_name")
        @Expose
        var storeName: String? = null
)