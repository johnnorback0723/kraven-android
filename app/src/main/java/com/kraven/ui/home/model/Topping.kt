package com.kraven.ui.home.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Topping(@SerializedName("Name")
                   @Expose
                   var nameTitle: String? = null) {

    class ToppingName(
            @SerializedName("Name")
            @Expose
            var name: String? = null,
            @SerializedName("price")
            @Expose
            var price: String? = null,
            @SerializedName("isChecked")
            @Expose
            var isChecked: Boolean? = null
    )
}