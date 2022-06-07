package com.kraven.ui.payment.setting.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Card(@SerializedName("id")
                @Expose
                var id: String? = null,
                @SerializedName("user_id")
                @Expose
                var userId: String? = null,
                @SerializedName("user_type")
                @Expose
                var userType: String? = null,
                @SerializedName("card_token")
                @Expose
                var cardToken: String? = null,
                @SerializedName("cardholder_name")
                @Expose
                var cardholderName: String? = null,
                @SerializedName("expiry_date")
                @Expose
                var expiryDate: String? = null,
                @SerializedName("card_image")
                @Expose
                var cardImage: String? = null,
                @SerializedName("is_expired")
                @Expose
                var isExpired: String? = null,
                @SerializedName("insertdate")
                @Expose
                var insertdate: String? = null) : Parcelable