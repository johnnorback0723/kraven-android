package com.kraven.data.pojo

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import com.kraven.ui.home.model.SendItems
import com.kraven.ui.order.beverage.model.Beverage
import kotlinx.android.parcel.Parcelize

@Parcelize
class Parameters(
        @SerializedName("island_id")
        @Expose
        var islandId: String? = null,
        @SerializedName("transaction_id")
        @Expose
        var transactionId: String? = null,
        @SerializedName("tollfee")
        @Expose
        var tollFee: String? = null,

        @SerializedName("pickup_latitude")
        @Expose
        var pickupLatitude: String? = null,


        @SerializedName("pickup_longitude")
        @Expose
        var pickupLongitude: String? = null,
        @SerializedName("driver_id")
        @Expose
        var driverId: String? = null,
        @SerializedName("fname")
        @Expose
        var firstName: String? = null,

        @SerializedName("os_version")
        @Expose
        var osVersion: String? = null,
        @SerializedName("device_name")
        @Expose
        var deviceName: String? = null,
        @SerializedName("model_name")
        @Expose
        var modelName: String? = null,

        @SerializedName("is_page_visit")
        @Expose
        var isPageVisit: String? = null,

        @SerializedName("lname")
        @Expose
        var lastName: String? = null,

        @SerializedName("name")
        @Expose
        var name: String? = null,

        @SerializedName("gender")
        @Expose
        var gender: String? = null,

        @SerializedName("dob")
        @Expose
        var dob: String? = null,

        @SerializedName("email")
        @Expose
        var email: String? = null,

        @SerializedName("date")
        @Expose
        var date: String? = null,

        @SerializedName("future_date")
        @Expose
        var futureDate: String? = null,

        @SerializedName("password")
        @Expose
        var password: String? = null,

        @SerializedName("country_code")
        @Expose
        var countryCode: String? = null,

        @SerializedName("dish_ids")
        @Expose
        var dishIds: String? = null,

        @SerializedName("comment")
        @Expose
        var comment: String? = null,

        @SerializedName("phone")
        @Expose
        var phone: String? = null,

        @SerializedName("latitude")
        @Expose
        var latitude: String? = null,

        @SerializedName("main_branch_id")
        @Expose
        var mainBranchId: String? = null,

        @SerializedName("longitude")
        @Expose
        var longitude: String? = null,

        @SerializedName("address")
        @Expose
        var address: String? = null,

        @SerializedName("otp")
        @Expose
        var otp: String? = null,

        @SerializedName("licence_image")
        @Expose
        var licenceImage: String? = null,

        @SerializedName("bank_name")
        @Expose
        var bankName: String? = null,

        @SerializedName("bank_branch")
        @Expose
        var bankBranch: String? = null,

        @SerializedName("account_holder")
        @Expose
        var accountHolderName: String? = null,

        @SerializedName("account_number")
        @Expose
        var accountNumber: String? = null,

        @SerializedName("routing_number")
        @Expose
        var routingNumber: String? = null,

        @SerializedName("device_type")
        @Expose
        var deviceType: String? = null,

        @SerializedName("app_version")
        @Expose
        var appVersion: String? = null,

        @SerializedName("version_name")
        @Expose
        var versionName: String? = null,

        @SerializedName("device_id")
        @Expose
        var deviceId: String? = null,

        @SerializedName("referral_code")
        @Expose
        var referralCode: String? = null,

        @SerializedName("signup_type")
        @Expose
        var signupType: String? = null,

        @SerializedName("user_id")
        @Expose
        var userId: String? = null,

        @SerializedName("profile_image")
        @Expose
        var profileImage: String? = null,

        @SerializedName("old_password")
        @Expose
        var oldPassword: String? = null,

        @SerializedName("new_password")
        @Expose
        var newPassword: String? = null,

        @SerializedName("country")
        @Expose
        var country: String? = null,

        @SerializedName("address_type")
        @Expose
        var addressType: String? = null,

        @SerializedName("street_name")
        @Expose
        var streetName: String? = null,

        @SerializedName("city")
        @Expose
        var city: String? = null,

        @SerializedName("area")
        @Expose
        var area: String? = null,

        @SerializedName("house")
        @Expose
        var house: String? = null,

        @SerializedName("landmark")
        @Expose
        var landmark: String? = null,

        @SerializedName("building_name")
        @Expose
        var buildingName: String? = null,

        @SerializedName("title")
        @Expose
        var title: String? = null,

        @SerializedName("message")
        @Expose
        var message: String? = null,

        @SerializedName("page")
        @Expose
        var page: String? = null,

        @SerializedName("address_id")
        @Expose
        var addressId: String? = null,

        @SerializedName("total_qty")
        @Expose
        var total_qty: String? = null,

        @SerializedName("social_id")
        @Expose
        var socialId: String? = null,

        @SerializedName("country_short_code")
        @Expose
        var countryShortCode: String? = null,

        @SerializedName("restaurant_id")
        @Expose
        var restaurantId: String? = null,

        @SerializedName("menu_id")
        @Expose
        var menuId: String? = null,

        @SerializedName("item_ids")
        @Expose
        var itemIds: String? = null,

        @SerializedName("distance")
        @Expose
        var distance: String? = null,

        @SerializedName("delivery_type")
        @Expose
        var deliveryType: String? = null,


        @SerializedName("payment_method")
        @Expose
        var paymentMethod: String? = null,

        @SerializedName("discount")
        @Expose
        var discount: String? = null,

        @SerializedName("wallet")
        @Expose
        var wallet: String? = null,

        @SerializedName("order_type")
        @Expose
        var orderType: String? = null,

        @SerializedName("sub_total")
        @Expose
        var subTotal: String? = null,

        @SerializedName("vat_percent")
        @Expose
        var vatPercent: String? = null,

        @SerializedName("total_vat")
        @Expose
        var totalVat: String? = null,

        @SerializedName("total")
        @Expose
        var total: String? = null,

        @SerializedName("order_datetime")
        @Expose
        var orderDatetime: String? = null,
        @SerializedName("promocode")
        @Expose
        var promocode: String? = null,
        @SerializedName("product_type")
        @Expose
        var product_type: String? = null,

        @SerializedName("amount")
        @Expose
        var amount: String? = null,
        @SerializedName("vendor_id")
        @Expose
        var vendorId: String? = null,
        @SerializedName("vendor_type")
        @Expose
        var vendorType: String? = null,

        @SerializedName("delivery_longitude")
        @Expose
        var deliveryLongitude: String? = null,

        @SerializedName("delivery_latitude")
        @Expose
        var deliveryLatitude: String? = null,

        @SerializedName("delivery_charge")
        @Expose
        var deliveryCharge: String? = null,

        @SerializedName("search")
        @Expose
        var search: String? = null,


        @SerializedName("order_id")
        @Expose
        var orderId: String? = null,

        @SerializedName("rate")
        @Expose
        var rate: String? = null,

        @SerializedName("cuisine_ids")
        @Expose
        var cuisineIds: List<String>? = null,

        @SerializedName("dishes")
        @Expose
        var dishes: List<SendItems>? = null,

        @SerializedName("user_type")
        @Expose
        var userType: String? = null,

        @SerializedName("cardholder_name")
        @Expose
        var cardholderName: String? = null,

        @SerializedName("card_number")
        @Expose
        var cardNumber: String? = null,

        @SerializedName("expiry_month")
        @Expose
        var expiryMonth: String? = null,

        @SerializedName("expiry_year")
        @Expose
        var expiryear: String? = null,

        @SerializedName("card_token")
        @Expose
        var cardToken: String? = null,


        @SerializedName("tip")
        @Expose
        var tip: String? = null,

        @SerializedName("tip_percent")
        @Expose
        var tipPercent: String? = null,

        @SerializedName("items")
        @Expose
        var items: List<Beverage>? = null,

        @SerializedName("beverage_id")
        @Expose
        var beverageId: String? = null,


        @SerializedName("id_image")
        @Expose
        var idImage: String? = null,


        @SerializedName("is_cay")
        @Expose
        var isCay: String? = null,


        @SerializedName("cay_id")
        @Expose
        var cayId: String? = null,


        @SerializedName("cayfee")
        @Expose
        var cayFee: String? = null) : Parcelable