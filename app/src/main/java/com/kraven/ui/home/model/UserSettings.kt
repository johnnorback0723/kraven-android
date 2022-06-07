package com.kraven.ui.home.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



data class UserSettings(

    @SerializedName("distance_radius")
    @Expose
    var distanceRadius: String? = null,
    @SerializedName("user_referral_wallet")
    @Expose
     var userReferralWallet: String? = null,
    @SerializedName("delivery_per_miles_rate")
    @Expose
     var deliveryPerMilesRate: String? = null,
    @SerializedName("delivery_base_fare")
    @Expose
     var deliveryBaseFare: String? = null,
    @SerializedName("delivery_minimum_miles")
    @Expose
     var deliveryMinimumMiles: String? = null,
    @SerializedName("vat")
    @Expose
     var vat: String? = null,
    @SerializedName("foodorder_owner_share")
    @Expose
     var foodorderOwnerShare: String? = null,
    @SerializedName("foodorder_restaurant_share")
    @Expose
     var foodorderRestaurantShare: String? = null,
    @SerializedName("foodorder_driver_share")
    @Expose
     var foodorderDriverShare: String? = null,
    @SerializedName("delivery_charge_btn_info")
    @Expose
     var deliveryChargeBtnInfo: String? = null,
    @SerializedName("cash_on_delivery_btn_info")
    @Expose
     var cashOnDeliveryBtnInfo: String? = null,
    @SerializedName("foodorder_accept_min_limit")
    @Expose
     var foodorderAcceptMinLimit: String? = null,
    @SerializedName("foodorder_accept_ratio_min")
    @Expose
     var foodorderAcceptRatioMin: String? = null,
    @SerializedName("beforemin_foodorder_goto_driver")
    @Expose
     var beforeminFoodorderGotoDriver: String? = null,
    @SerializedName("multiple_orders_dropoff_distance")
    @Expose
     var multipleOrdersDropoffDistance: String? = null,
    @SerializedName("multiple_orders_pickup_distance")
    @Expose
     var multipleOrdersPickupDistance: String? = null,
    @SerializedName("driver_request_waiting_sec")
    @Expose
     var driverRequestWaitingSec: String? = null,
    @SerializedName("customer_order_cancellation_min")
    @Expose
     var customerOrderCancellationMin: String? = null,
    @SerializedName("future_order_reminder_min")
    @Expose
     var futureOrderReminderMin: String? = null,
    @SerializedName("driver_need_to_arrive_before_min")
    @Expose
     var driverNeedToArriveBeforeMin: String? = null,
    @SerializedName("bev_delivery_charge_btn_info")
    @Expose
     var bevDeliveryChargeBtnInfo: String? = null,
    @SerializedName("pending_cancellation_min")
    @Expose
     var pendingCancellationMin: String? = null
)
