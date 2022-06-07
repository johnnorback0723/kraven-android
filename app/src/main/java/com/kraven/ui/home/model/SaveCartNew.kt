package com.kraven.ui.home.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.kraven.ui.order.beverage.model.Beverage
import kotlinx.android.parcel.Parcelize


@Parcelize
class SaveCartNew (
    @SerializedName("wallet")
    var wallet: String? = null,
    @SerializedName("distance")
    var distance: String? = null,
    @SerializedName("restaurant_id")
    var restaurantId: String? = null,
    @SerializedName("address_id")
    var addressId: String? = null,
    @SerializedName("discount")
    var discount: String? = null,
    @SerializedName("order_datetime")
    var orderDatetime: String? = null,
    @SerializedName("dishes")
    var dishes: List<SendItems>? = null,
    @SerializedName("items")
    var items:List<Beverage>?=null,
    @SerializedName("total_vat")
    var totalVat: String? = null,
    @SerializedName("total")
    var total: String? = null,
    @SerializedName("delivery_type")
    var deliveryType: String? = null,
    @SerializedName("vat_percent")
    var vatPercent: String? = null,
    @SerializedName("sub_total")
    var subTotal: String? = null,
    @SerializedName("order_type")
    var orderType: String? = null,
    @SerializedName("payment_method")
    var paymentMethod: String? = null,
    @SerializedName("menuId")
    var menuId: String? = null,
    @SerializedName("total_qty")
    var total_qty: String? = null,
    @SerializedName(" delivery_charge")
    var deliveryCharge: String? = null,
    @SerializedName("tip")
    var tip: String? = null,
    @SerializedName("promocode")
    var promoCode:String? = null,
    @SerializedName("card_token")
    var cardToken:String? = null,
    @SerializedName("tip_percent")
    var tipPercent:String? = null,
    @SerializedName("order_page")
    var orderPage:String?=null,
    @SerializedName("beverage_id")
    var beverageId:String?=null
) : Parcelable {

}



