package com.kraven.ui.order.beverage.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kraven.ui.home.model.ToppingsItems
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Beverage(
        @SerializedName("original_price")
        @Expose
        var originalPrice: String? = null,
        @SerializedName("id")
        @Expose
        var id: String? = null,
        @SerializedName("beverage_id")
        @Expose
        var beverageId: String? = null,
        @SerializedName("menu_id")
        @Expose
        var menuId: String? = null,
        @SerializedName("item_id")
        @Expose
        var itemId: String? = null,
        @SerializedName("name")
        @Expose
        var name: String? = null,
        @SerializedName("price")
        @Expose
        var price: Float? = null,
        @SerializedName("status")
        @Expose
        var status: String? = null,
        @SerializedName("milliliter")
        @Expose
        var milliliter: String? = null,
        @SerializedName("description")
        @Expose
        var description: String? = null,
        @SerializedName("image")
        @Expose
        var image: String? = null,
        @SerializedName("menu")
        @Expose
        var menu: String? = null,
        @SerializedName("vat")
        var vat: String? = null,
        @SerializedName("vat_price")
        var vatPrice: String? = null,
        @SerializedName("special_note")
        @Expose
        var specialNotes: String? = null,
        @SerializedName("item")
        var item: String? = null,
        @SerializedName("qty")
        var qty: Int? = null,
        @SerializedName("dishPriceTopping")
        var dishPriceTopping: Float? = null,
        @SerializedName("total_price")
        var totalPrice: Float? = null,
        @SerializedName("toppings")
        var toppings: ToppingsItems? = null,
        @SerializedName("special_offer")
        @Expose
        var specialOffer: Float? = null) : Parcelable