package com.kraven.ui.home.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Dish {

    @SerializedName("menu")
    @Expose
    var menu: String? = null
    @SerializedName("dish")
    @Expose
    var dish: String? = null
    @SerializedName("food")
    @Expose
    var food: String? = null
    @SerializedName("qty")
    @Expose
    var qty: String? = null
    @SerializedName("price")
    @Expose
    var price: String? = null
    @SerializedName("special_note")
    @Expose
    var specialNote: String? = null
    @SerializedName("toppings")
    var toppings: ArrayList<ToppingsItems>?=null
    @SerializedName("special_offer")
    @Expose
    var specialOffer: String? = null

}