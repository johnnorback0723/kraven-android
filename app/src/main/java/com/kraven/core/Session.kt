package com.kraven.core


import com.kraven.data.pojo.Parameters
import com.kraven.data.pojo.User
import com.kraven.ui.home.model.CartModel
import com.kraven.ui.home.model.UserSettings


/**
 * Created by hlink21 on 11/7/16.
 */
public interface Session {

    var apiKey: String

    var userSession: String

    var userId: String

    var deviceId: String

    var user: User?

    var userSettings: UserSettings?

    val language: String

    var isProtoType: Boolean

    var isUploadImage: Boolean

    var setAddressCode: String

    var cartModel: CartModel?

    var saveTempParameters: HashMap<String, Any>?

    var saveOrderPage: String?

    var saveRestaurantName: String?

    var saveTempWalletParameters: HashMap<String, Any>?

    var cartCount: String?

    var totalPrice: Float?

    var transactionIdCard: String?

    var transactionIdWallet: String?

    fun clearSession()

    companion object {
        const val API_KEY = "API-KEY"
        const val USER_SESSION = "TOKEN"
        const val USER_ID = "USER_ID"
        const val DEVICE_TYPE = "A"
        const val IS_PROTOTYPE = "is_prototype"
        const val IS_UPLOAD_DCOUMENT = "upload_document"
        val IS_ADDRESS: String = "address"
        val CART_COUNT = "cart_count"
        val TOTAL_PRICE = "total_price"
        val USER_SETTING = "user_setting"
        const val DEVICE_ID = "device_id"
        const val TRANSACTION_ID_CARD = "transactionIdCard"
        const val TRANSACTION_ID_WALLET = "transactionIdWallet"
    }
}
