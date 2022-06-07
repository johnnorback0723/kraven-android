package com.kraven.ui.authentication.model

import com.google.gson.annotations.SerializedName

data class AppVersion(
        @SerializedName("android_driver_app_version")
        var androidDriverAppVersion: String?,
        @SerializedName("android_user_app_version")
        var androidUserAppVersion: String?,
        @SerializedName("ios_driver_app_version")
        var iosDriverAppVersion: String?,
        @SerializedName("ios_user_app_version")
        var iosUserAppVersion: String?
)