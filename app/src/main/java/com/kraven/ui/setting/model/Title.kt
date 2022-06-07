package com.kraven.ui.setting.model


import com.google.gson.annotations.SerializedName

data class Title(
        @SerializedName("driver_dropdownlist")
        val driverDropdownlist: DriverDropdownlist?,
        @SerializedName("user_dropdownlist")
        val userDropdownlist: UserDropdownlist?
) {
    data class UserDropdownlist(
            @SerializedName("0")
            val x0: String?=null,
            @SerializedName("1")
            val x1: String?=null,
            @SerializedName("2")
            val x2: String?=null,
            @SerializedName("3")
            val x3: String?=null,
            @SerializedName("4")
            val x4: String?=null
    )

    data class DriverDropdownlist(
            @SerializedName("0")
            val x0: String?=null,
            @SerializedName("1")
            val x1: String?=null,
            @SerializedName("2")
            val x2: String?=null
    )
}