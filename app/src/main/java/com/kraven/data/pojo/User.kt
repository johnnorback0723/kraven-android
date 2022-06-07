package com.kraven.data.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(@SerializedName("device_id")
                val deviceId: String = "",
                @SerializedName("island_id")
                var islandId: String? = null,
                @SerializedName("island")
                var island: String? = null,
                @SerializedName("social_id")
                var socialId: String = "",
                @SerializedName("latitude")
                val latitude: String = "",
                @SerializedName("device_type")
                var deviceType: String = "",
                @SerializedName("token")
                val token: String = "",
                @SerializedName("country_code")
                val countryCode: String = "",
                @SerializedName("referral_code")
                val referralCode: String = "",
                @SerializedName("profile_image")
                val profileImage: String = "",
                @SerializedName("user_type")
                val userType: String = "",
                @SerializedName("phone")
                val phone: String = "",
                @SerializedName("service")
                val service: String? = "",
                @SerializedName("id_image")
                var idImage: String? = "",
                @SerializedName("name")
                val name: String? = "",
                @SerializedName("id")
                val id: String? = "",
                @SerializedName("licence_image")
                var licenceImage: String? = "",
                @SerializedName("country_short_code")
                val countryShortCode: String? = null,
                @SerializedName("email")
                var email: String = "",
                @SerializedName("longitude")
                val longitude: String = "",
                @SerializedName("otp")
                val otp: String = "",
                @SerializedName("fname")
                var firstName: String = "",
                @SerializedName("lname")
                var lastName: String = "",
                @SerializedName("gender")
                var gender: String = "",
                @SerializedName("dob")
                var dob: String = "",
                @SerializedName("wallet")
                var wallet: Float? = 0.00F,
                @SerializedName("signup_type")
                var signupType: String = "",
                @SerializedName("address")
                val address: String = "") : Parcelable {
    override fun toString(): String {
        return firstName
    }
}