package com.kraven.ui.home.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Promocode( @SerializedName("amount")
                      var amount: Int? = null,
                      @SerializedName("banner")
                      var banner: String? = null,
                      @SerializedName("beverage_id")
                      var beverageId: Int? = null,
                      @SerializedName("description")
                      var description: String? = null,
                      @SerializedName("end_date")
                      var endDate: String? = null,
                      @SerializedName("id")
                      var id: Int? = null,
                      @SerializedName("image")
                      var image: String? = null,
                      @SerializedName("insertdate")
                      var insertdate: String? = null,
                      @SerializedName("is_visible")
                      var isVisible: String? = null,
                      @SerializedName("minimum_amount")
                      var minimumAmount: Int? = null,
                      @SerializedName("per_user_count")
                      var perUserCount: Int? = null,
                      @SerializedName("product_type")
                      var productType: String? = null,
                      @SerializedName("promo_type")
                      var promoType: String? = null,
                      @SerializedName("promocode")
                      var promocode: String? = null,
                      @SerializedName("restaurant_id")
                      var restaurantId: Int? = null,
                      @SerializedName("sequence")
                      var sequence: Int? = null,
                      @SerializedName("start_date")
                      var startDate: String? = null,
                      @SerializedName("status")
                      var status: String? = null,
                      @SerializedName("total_count")
                      var totalCount: Int? = null,
                      @SerializedName("type")
                      var type: String? = null,
                      @SerializedName("used_count")
                      var usedCount: Int? = null,
                      @SerializedName("user_ids")
                      var userIds: String? = null) : Parcelable


