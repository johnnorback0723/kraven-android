package com.kraven.ui.payment.model


import com.google.gson.annotations.SerializedName

data class TransactionDetails(
    @SerializedName("code")
    var code: String? = null,
    @SerializedName("data")
    var `data`: Data? = null,
    @SerializedName("message")
    var message: String? = null
) {
    data class Data(
        @SerializedName("TransactionStatusResult")
        var transactionStatusResult: TransactionStatusResult? = null
    ) {
        data class TransactionStatusResult(
            @SerializedName("AcquirerId")
            var acquirerId: String? = null,
            @SerializedName("CreditCardTransactionResults")
            var creditCardTransactionResults: CreditCardTransactionResults? = null,
            @SerializedName("CustomData")
            var customData: String? = null,
            @SerializedName("MerchantId")
            var merchantId: String? = null,
            @SerializedName("OrderNumber")
            var orderNumber: String? = null,
            @SerializedName("Signature")
            var signature: String? = null,
            @SerializedName("SignatureMethod")
            var signatureMethod: String? = null
        ) {
            data class CreditCardTransactionResults(
                @SerializedName("AVSResult")
                var aVSResult: String? = null,
                @SerializedName("AuthCode")
                var authCode: String? = null,
                @SerializedName("CVV2Result")
                var cVV2Result: String? = null,
                @SerializedName("OriginalResponseCode")
                var originalResponseCode: String? = null,
                @SerializedName("PaddedCardNumber")
                var paddedCardNumber: String? = null,
                @SerializedName("ReasonCode")
                var reasonCode: String? = null,
                @SerializedName("ReasonCodeDescription")
                var reasonCodeDescription: String? = null,
                @SerializedName("ReferenceNumber")
                var referenceNumber: String? = null,
                @SerializedName("ResponseCode")
                var responseCode: String? = null,
                @SerializedName("TokenizedPAN")
                var tokenizedPAN: String? = null
            )
        }
    }
}