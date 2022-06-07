package com.kraven.ui.order.beverage.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BeverageAdd(@SerializedName("delivery_datetime")
                       @Expose
                       var deliveryDate: String? = null,
                       @SerializedName("beverage")
                       @Expose
                       var beverageName: String? = null,
                       @SerializedName("brand")
                       @Expose
                       var beverageBrand: String? = null,
                       @SerializedName("quantity_type")
                       @Expose
                       var quantityType: String? = null,
                       @SerializedName("quantity")
                       @Expose
                       var quantity: String? = null,
                       @SerializedName("special_note")
                       @Expose
                       var specialNote: String? = null)