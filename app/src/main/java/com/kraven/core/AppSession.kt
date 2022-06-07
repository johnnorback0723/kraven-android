package com.kraven.core


import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kraven.core.Session.Companion.TRANSACTION_ID_CARD
import com.kraven.core.Session.Companion.USER_SETTING
import com.kraven.data.pojo.User
import com.kraven.ui.home.model.CartModel
import com.kraven.ui.home.model.UserSettings
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton


/**
 * Created by hlink21 on 11/7/16.
 */
@Singleton
class AppSession @Inject
constructor(private val appPreferences: AppPreferences,
            private val context: Context,
            @param:Named("api-key") override var apiKey: String) : Session {

    override var cartModel: CartModel?
        get() {
            val cartModel = appPreferences.getString("cart")
            return gson.fromJson(cartModel, CartModel::class.java)
        }
        set(cartModel) {

            if (cartModel != null) {
                val cart = gson.toJson(cartModel)
                appPreferences.putString("cart", cart)
            } else {
                appPreferences.putString("cart", "")
            }
        }

    override var saveTempParameters: HashMap<String, Any>?
        get() {
            val saveTempParameters = appPreferences.getString("parameters")
            return gson.fromJson(saveTempParameters, object : TypeToken<HashMap<String?, Any?>?>() {}.type)
        }
        set(saveTempParameters) {

            if (saveTempParameters != null) {
                val cart = gson.toJson(saveTempParameters)
                appPreferences.putString("parameters", cart)
            } else {
                appPreferences.putString("parameters", "")
            }
        }


    override var saveTempWalletParameters: HashMap<String, Any>?
        get() {
            val saveTempParameters = appPreferences.getString("saveTempWalletParameters")
            return gson.fromJson(saveTempParameters, object : TypeToken<HashMap<String?, Any?>?>() {}.type)
        }
        set(saveTempParameters) {

            if (saveTempParameters != null) {
                val cart = gson.toJson(saveTempParameters)
                appPreferences.putString("saveTempWalletParameters", cart)
            } else {
                appPreferences.putString("saveTempWalletParameters", "")
            }
        }

    override var saveOrderPage: String?
        get() = appPreferences.getString("saveOrderPage")
        set(value) {
            appPreferences.putString("saveOrderPage", value!!)
        }

    override var saveRestaurantName: String?
        get() = appPreferences.getString("saveRestaurantName")
        set(value) {
            appPreferences.putString("saveRestaurantName", value!!)
        }

    override var cartCount: String?
        get() = appPreferences.getString(Session.CART_COUNT)
        set(value) {
            appPreferences.putString(Session.CART_COUNT, value!!)
        }


    override var totalPrice: Float?
        get() = appPreferences.getFloat(Session.TOTAL_PRICE)
        set(value) {
            appPreferences.putFloat(Session.TOTAL_PRICE, value!!)
        }
    override var transactionIdCard: String?
        get() = appPreferences.getString(Session.TRANSACTION_ID_CARD)
        set(value) {
            appPreferences.putString(Session.TRANSACTION_ID_CARD, value!!)
        }
    override var transactionIdWallet: String?
        get() = appPreferences.getString(Session.TRANSACTION_ID_WALLET)
        set(value) {
            appPreferences.putString(Session.TRANSACTION_ID_WALLET, value!!)
        }

    override var setAddressCode: String
        get() = appPreferences.getString(Session.IS_ADDRESS)
        set(userSession) = appPreferences.putString(Session.IS_ADDRESS, userSession)


    override var isUploadImage: Boolean
        get() = appPreferences.getBoolean(Session.IS_UPLOAD_DCOUMENT)
        set(isUploadImage) = appPreferences.putBoolean(Session.IS_UPLOAD_DCOUMENT, isUploadImage)

    override var isProtoType: Boolean
        get() = appPreferences.getBoolean(Session.IS_PROTOTYPE)
        set(isProtoType) = appPreferences.putBoolean(Session.IS_PROTOTYPE, isProtoType)

    private val gson: Gson = Gson()

    override var user: User? = null
        get() {
            val userJSON = appPreferences.getString(USER_JSON)
            return gson.fromJson(userJSON, User::class.java)
        }
        set(value) {
            field = value
            val userJson = gson.toJson(value)
            if (userJson != null)
                appPreferences.putString(USER_JSON, userJson)
        }

    override var userSettings: UserSettings? = null
        get() {
            val userJSON = appPreferences.getString(USER_SETTING)
            return gson.fromJson(userJSON, UserSettings::class.java)
        }
        set(value) {
            field = value
            val userJson = gson.toJson(value)
            if (userJson != null)
                appPreferences.putString(USER_SETTING, userJson)
        }

    override var userSession: String
        get() = appPreferences.getString(Session.USER_SESSION)
        set(userSession) = appPreferences.putString(Session.USER_SESSION, userSession)


    override var userId: String
        get() = appPreferences.getString(Session.USER_ID)
        set(userId) = appPreferences.putString(Session.USER_ID, userId)


    override
    var deviceId: String
        get() = appPreferences.getString(Session.DEVICE_ID)
        set(deviceId) = appPreferences.putString(Session.DEVICE_ID, deviceId)

    override//  return StringUtils.equalsIgnoreCase(appPreferences.getString(Common.LANGUAGE), "ar") ? LANGUAGE_ARABIC : LANGUAGE_ENGLISH;
    val language: String
        get() = "en"


    override fun clearSession() {
        appPreferences.clearAll()
    }


    companion object {
        const val USER_JSON = "user_json"
    }


}
