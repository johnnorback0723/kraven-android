package com.kraven.ui.payment.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Payment(@SerializedName("number")
                   @Expose
                   var card_number: String? = null,
                   @SerializedName("card_type")
                   @Expose
                   var card_type: String? = null) {
}