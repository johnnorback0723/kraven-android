package com.kraven.ui.home.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class CartModel {
    @SerializedName("order_page")
    @Expose
    var orderPage: String? = null
    @SerializedName("isDetails")
    @Expose
    var isDetails: Boolean?=false
    @SerializedName("restaurant_id")
    @Expose
    var restaurantId: String? = null
    @SerializedName("delivery_type")
    @Expose
    var deliveryType: String? = null
    @SerializedName("distance")
    @Expose
    var distance: String? = null
    @SerializedName("address_id")
    @Expose
    var addressId: String? = null
    @SerializedName("payment_method")
    @Expose
    var paymentMethod: String? = null
    @SerializedName("total_qty")
    @Expose
    var totalQty: String? = null
    @SerializedName("discount")
    @Expose
    var discount: String? = null
    @SerializedName("wallet")
    @Expose
    var wallet: String? = null
    @SerializedName("order_type")
    @Expose
    var orderType: String? = null
    @SerializedName("sub_total")
    @Expose
    var subTotal: String? = null
    @SerializedName("vat_percent")
    @Expose
    var vatPercent: String? = null
    @SerializedName("total_vat")
    @Expose
    var totalVat: String? = null
    @SerializedName("total")
    @Expose
    var total: String? = null
    @SerializedName("order_datetime")
    @Expose
    var orderDatetime: String? = null
    @SerializedName("dishes")
    @Expose
    var dishes: ArrayList<DishesItem>? = null
}