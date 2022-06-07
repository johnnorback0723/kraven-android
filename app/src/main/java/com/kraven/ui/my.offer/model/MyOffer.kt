package com.kraven.ui.my_offer.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MyOffer(@SerializedName("offer_image")
                   @Expose
                   var offerimage: String? = null,
                   @SerializedName("offer_text")
                   @Expose
                   var offerText: String? = null) {
}