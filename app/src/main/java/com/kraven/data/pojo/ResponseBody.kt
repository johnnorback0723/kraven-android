package com.kraven.data.pojo

import com.google.gson.annotations.SerializedName

data class ResponseBody<T>(@SerializedName("code") var code: Int,
                           @SerializedName("message") val message: String,
                           @SerializedName("data") val data: T?) {}