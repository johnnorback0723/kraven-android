package com.kraven.ui.order.beverage.model


import com.google.gson.annotations.SerializedName

data class SpecialBeverageDetails(
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
    @SerializedName("delivery_commission")
    val deliveryCommission: Float = 0F,
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
    @SerializedName("items")
    var items: List<Item?>?=null,
    @SerializedName("payment_method")
    var paymentMethod: String?=null,
    @SerializedName("promocode_id")
    var promocodeId: String?=null,
    @SerializedName("status")
    var status: String?=null,
    @SerializedName("sub_total")
    var subTotal: Float?=null,
    @SerializedName("tollfee")
    var tollFee: String?=null,
    @SerializedName("timing")
    var timing: Timing?=null,
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
) {
    data class Timing(
        @SerializedName("accepted")
        var accepted: String?=null,
        @SerializedName("cancelled")
        var cancelled: String?=null,
        @SerializedName("completed")
        var completed: String?=null,
        @SerializedName("confirmed")
        var confirmed: String?=null,
        @SerializedName("delivered")
        var delivered: String?=null,
        @SerializedName("id")
        var id: String?=null,
        @SerializedName("insertdate")
        var insertdate: String?=null,
        @SerializedName("order_id")
        var orderId: String?=null,
        @SerializedName("pending")
        var pending: String?=null,
        @SerializedName("rejected")
        var rejected: String?=null
    )

    data class Item(
        @SerializedName("beverage")
        var beverage: String?=null,
        @SerializedName("brand")
        var brand: String?=null,
        @SerializedName("id")
        var id: String?=null,
        @SerializedName("image")
        var image: String?=null,
        @SerializedName("insertdate")
        var insertdate: String?=null,
        @SerializedName("order_id")
        var orderId: String?=null,
        @SerializedName("price")
        var price: String?=null,
        @SerializedName("quantity")
        var quantity: String?=null,
        @SerializedName("quantity_type")
        var quantityType: String?=null,
        @SerializedName("size_of_bottle")
        var sizeOfBottle: String?=null,
        @SerializedName("special_note")
        var specialNote: String?=null,
        @SerializedName("status")
        var status: String?=null,
        @SerializedName("user_id")
        var userId: String?=null
    )
}