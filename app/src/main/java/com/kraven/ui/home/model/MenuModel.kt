package com.kraven.ui.home.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MenuModel(@SerializedName("veg")
                     @Expose
                     var vegNonVeg: String? = null,
                     @SerializedName("name")
                     @Expose
                     var name: String? = null,
                     @SerializedName("itemType")
                     @Expose
                     var itemType: String? = null,
                     @SerializedName("itemPrice")
                     @Expose
                     var itemPrice: String? = null,
                     @SerializedName("specialOffer")
                     @Expose
                     var specialOffer: String? = null,
                     @SerializedName("itemCount")
                     @Expose
                     var itemCount: Int? = null,
                     @SerializedName("item_off_price")
                     @Expose
                     var itemOffPrice: String? = null
) : Parcelable {
}