package com.kraven.ui.payment.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HostedPageModel(
    @SerializedName("data")
    var `data`: Data?,
    @SerializedName("code")
    var code: String?,
    @SerializedName("message")
    var message: String?
) : Parcelable {
    @Parcelize
    data class Data(
        @SerializedName("HostedPageAuthorizeResult")
        var hostedPageAuthorizeResult: HostedPageAuthorizeResult?,
        @SerializedName("HostedPagesAuthorizationPayURL")
        var hostedPagesAuthorizationPayURL: String?,
        @SerializedName("MerchantResponseUrl")
        var merchantResponseUrl: String?
    ) : Parcelable {
        @Parcelize
        data class HostedPageAuthorizeResult(
            @SerializedName("ResponseCode")
            var responseCode: String?,
            @SerializedName("ResponseCodeDescription")
            var responseCodeDescription: String?,
            @SerializedName("SingleUseToken")
            var singleUseToken: String?
        ) : Parcelable
    }
}