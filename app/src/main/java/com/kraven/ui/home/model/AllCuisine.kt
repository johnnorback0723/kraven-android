package com.kraven.ui.home.model

import com.google.gson.annotations.SerializedName

class AllCuisine(
        @SerializedName("cuisine_list")
        var cuisineList: ArrayList<Cuisine>? = null,
        @SerializedName("custom_list")
        var customList: ArrayList<Cuisine>? = null
) {
    data class Cuisine(
            @SerializedName("id")
            var id: String? = null,
            @SerializedName("image")
            var image: String? = null,
            @SerializedName("name")
            var name: String? = null,
            @SerializedName("status")
            var status: String? = null
    )

}