package com.kraven.ui.payment.model

import com.google.gson.annotations.SerializedName

data class CardResponse(@SerializedName("code")
                        var code: String? = null,
                        @SerializedName("message")
                        var message: String? = null,
                        @SerializedName("data")
                        var htmlPage: String? = null)
