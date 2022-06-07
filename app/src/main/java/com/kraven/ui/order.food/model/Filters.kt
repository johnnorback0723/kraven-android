package com.kraven.ui.order.food.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Filters(@SerializedName("filter_label")
                   @Expose
                   var filterLabel: String? = null,
                   var filterList: List<FilterName>) {

    class FilterName(@SerializedName("price")
                     val price: Int = 0,
                     @SerializedName("name")
                     val name: String = "",
                     @SerializedName("id")
                     val id: Int = 0)
}