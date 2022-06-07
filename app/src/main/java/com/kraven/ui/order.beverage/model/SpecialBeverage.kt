package com.kraven.ui.order.beverage.model


import com.google.gson.annotations.SerializedName

data class SpecialBeverage(
    @SerializedName("address_type")
    var addressType: String?=null,
    @SerializedName("admin_notes")
    var adminNotes: String?=null,
    @SerializedName("cancel_by")
    var cancelBy: String?=null,
    @SerializedName("cancel_reason")
    var cancelReason: String?=null,
    @SerializedName("card_token")
    var cardToken: String?=null,
    @SerializedName("delivery_address")
    var deliveryAddress: String?=null,
    @SerializedName("delivery_charge")
    var deliveryCharge: String?=null,
    @SerializedName("delivery_datetime")
    var deliveryDatetime: String?=null,
    @SerializedName("delivery_latitude")
    var deliveryLatitude: String?=null,
    @SerializedName("delivery_longitude")
    var deliveryLongitude: String?=null,
    @SerializedName("discount")
    var discount: String?=null,
    @SerializedName("id")
    var id: String?=null,
    @SerializedName("insertdate")
    var insertdate: String?=null,
    @SerializedName("payment_method")
    var paymentMethod: String?=null,
    @SerializedName("promocode_id")
    var promocodeId: String?=null,
    @SerializedName("status")
    var status: String?=null,
    @SerializedName("sub_total")
    var subTotal: String?=null,
    @SerializedName("tip")
    var tip: String?=null,
    @SerializedName("tip_percent")
    var tipPercent: String?=null,
    @SerializedName("total")
    var total: String?=null,
    @SerializedName("total_vat")
    var totalVat: String?=null,
    @SerializedName("transaction_id")
    var transactionId: String?=null,
    @SerializedName("user_id")
    var userId: String?=null,
    @SerializedName("vat_percent")
    var vatPercent: String?=null,
    @SerializedName("wallet")
    var wallet: String?=null
)