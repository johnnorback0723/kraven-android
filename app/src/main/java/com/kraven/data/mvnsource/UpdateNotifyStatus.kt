package com.kraven.data.mvnsource


import com.google.gson.annotations.SerializedName

data class UpdateNotifyStatus(
    @SerializedName("result")
    var result: Result = Result()
) {
    data class Result(
        @SerializedName("code")
        var code: Int = 0, // 1
        @SerializedName("message")
        var message: String = "" // Updated Successfully
    )
}