package com.kraven.data


import com.kraven.R
import com.kraven.application.KravenCustomer
import okhttp3.HttpUrl

/**
 * Created by hlink21 on 11/5/17.
 */

object URLFactory {

    // server details
    const val IS_LOCAL = true
    private val SCHEME = KravenCustomer.appContext.getString(R.string.SCHEME)
    private val HOST = if (IS_LOCAL) KravenCustomer.appContext.getString(R.string.test_host_server) else
        KravenCustomer.appContext.getString(R.string.HOST_IS_SERVER)

    private val API_PATH = if (IS_LOCAL) KravenCustomer.appContext.getString(R.string.API_PATH_LOCAL) else KravenCustomer.appContext.getString(R.string.API_PATH)
    private val PORT = KravenCustomer.appContext.getString(R.string.PORT).toInt()

    fun provideHttpUrl(): HttpUrl {
        return HttpUrl.Builder()
                .scheme(SCHEME)
                .host(HOST)
                .port(PORT)
                .addPathSegments(API_PATH)
                .build()
    }

    // API Methods
    object Method {

        //Authentication API
        const val GET_APP_VERSION = "user/get_app_version/"
        const val VERIFICATION_EMAIL = "user/verify_email/"
        const val VERIFICATION_OTP = "user/otp_verification/"
        const val SIGNUP = "user/signup/"
        const val LOGIN = "user/login/"
        const val GET_USER = "user/get_user/"
        const val EDIT_USER = "user/edit/"
        const val FORGOT_PASSWORD = "user/forgotpassword/"
        const val CHANGE_PASSWORD = "user/changepassword/"

        //app_version, version_name,device_id, device_type
        const val UPDATE_DEVICE_ID = "user/updatedeviceid/"
        const val UPDATE_LATLONG = "user/updatelatlong/"
        const val CONTACT_US = "user/contactus/"
        const val CONTACT_US_LIST = "driver/contactus_dropdownlist/"
        const val ADD_ADDRESS = "user/add_address/"
        const val EDIT_ADDRESS = "user/edit_address/"
        const val REVIEW_LIST = "user/get_review_list/"
        const val DELETE_ADDRESS = "user/delete_address/"
        const val ADDRESS_LIST = "user/get_address_list"
        const val LOGOUT = "user/logout/"
        const val GET_SETTING = "user/get_settings/"
        const val ADD_WALLET_MONEY = "user/add_wallet_money/"
        const val GIVERATEREVIEW = "user/give_rate_review/"
        const val NOTIFICATION_LIST = "order_food/get_notification_list/"
        const val CHECK_LIVE_ORDER_TRACKING = "user/check_live_order_tracking/"
        const val HOSTEDPAGEPAYMENT = "user/hostedpage_payment/"
        const val CARD_PAYMENT = "user/hostedpage_auth_3DS_transaction/"
        const val GET_TRANSACTION_STATUS = "user/get_transaction_status/"
        const val DELETE_SAVED_CARDS = "user/delete_card/"
        const val ADD_WALLET_MONEY_FAC = "user/add_wallet_money_fac/"
        const val CAY_LIST = "user/get_cays"


        //Restaurant API
        const val NEARBY_RESTAURANT = "restaurant/nearby_restaurant/"
        const val RESTAURANT_BRANCH_LIST = "restaurant/restaurant_branch_list/"
        const val ADD_FAVOURITE_RESTAURANT = "restaurant/add_favourite_restaurant/"
        const val PROMOCODE_LIST = "restaurant/promocode_list/"
        const val GET_RESTAURANT_DETAIL = "restaurant/get_restaurant/"
        const val GET_RESTAURANT_MENU_DISHES = "restaurant/get_restaurant_menu_dishes/"
        const val FAVORITELIST = "restaurant/favourite_restaurant_list/"
        const val GETCUISINELIST = "restaurant/get_cuisine_list/"
        const val GETWALLETLIST = "user/get_wallet_list/"
        const val GET_NEW_CUISINE_LIST = "restaurant/get_new_cuisine_list/"
        const val CHECK_TOLLFEE_LOCATION = "user/check_tollfee_location/"
        const val GET_ISLAND_LIST = "restaurant/get_island_list/"
        const val SET_ISLAND = "user/set_island/"


        //Food Order API
        const val PLACEORDER = "order_food/placeorder/"
        const val GET_ORDER_FOOD_HISTORY = "order_food/get_order_food_history/"
        const val VERIFY_PROMOCODE = "order_food/verify_promocode/"
        const val CHECK_RESTAURANT_DELIVERY_RADIUS = "order_food/check_restaurant_delivery_radius/"
        const val CHECK_BEVERAGE_DELIVERY_RADIUS = "order_beverage/check_beverage_delivery_radius/"
        const val GET_RESTAURANT_DISHES_BYID = "restaurant/get_restaurant_dishes_byid/"
        const val GET_ORDER_DETAILS = "order_food/get_order/"
        const val CANCEL_FOOD_ORDER = "order_food/cancel_food_order/"
        const val GET_DRIVER_LAT_LONG = "driver/get_driver_latlong/"
        const val BEFORE_VERIFY_FOOD_ORDER = "order_food/before_verify_foodorder/"
        const val BEFORE_VERIFY_BEVERAGE_ORDER = "order_beverage/before_verify_beverageorder/"

        //Add Card API
        const val GET_CARD_LIST = "user/get_card_list/"
        const val DELETE_CARD = "user/delete_card/"
        const val ADD_CARD = "user/add_card/"

        //Beverage API
        const val NEARBY_BEVERAGE = "beverage/nearby_beverage/"
        const val BEVERAGE_BRANCH_LIST = "beverage/beverage_branch_list/"
        const val GET_BEVERAGE_RESTAURANT = "beverage/get_beverage/"
        const val GET_BEVERAGE_MENU = "beverage/get_beverage_menu/"
        const val GET_BEVERAGE_MENU_ITEMS = "beverage/get_beverage_menu_items/"
        const val getBeverageMenuListSearch = "beverage/search_beverage_menu_items/"
        const val GET_BEVERAGE_ITEMS_BYID = "beverage/get_beverage_items_byid/"
        const val ADD_FAVOURITE_BEVERAGE = "beverage/add_favourite_beverage/"
        const val FAVOURITE_BEVERAGE_LIST = "beverage/favourite_beverage_list/"
        const val ORDER_BEVERAGE_PLACEORDER = "order_beverage/placeorder/"
        const val GET_ORDER_BEVERAGE_HISTORY = "order_beverage/get_order_beverage_history/"
        const val GET_ORDER_BEVERAGE_DETAILS = "order_beverage/get_order/"
        const val CANCEL_BEVERAGE_ORDER = "order_beverage/cancel_order_beverage/"


        //Special Beverage Order API
        const val SPECIAL_BEVERAGE_DROPDOWNLIST = "special_beverage/special_beverage_dropdownlist/"
        const val SPECIAL_BEVERAGE_PLACEORDER = "special_beverage/placeorder/"
        const val GET_SPECIAL_BEVERAGE_ORDER_HISTORY = "special_beverage/get_special_beverage_order_history/"
        const val GET_SPECIAL_ORDER = "special_beverage/get_special_order/"
        const val PAY_SPECIAL_BEVERAGE = "special_beverage/pay_special_beverage/"
        const val CANCEL_SPECIAL_BEVERAGE = "special_beverage/cancel_special_beverage/"
        const val DELETE_FAC_LOG = "user/delete_fac_transaction_log"
        const val CANCEL_HOSTED_PAGE_FOOD = "cron/HostedPageCancel/food/{food_oder_id}"
        const val CANCEL_HOSTED_PAGE_BEVERAGE = "cron/HostedPageCancel/beverage/{beverage_order_id}"

        /*
         * Google APIs
         */
        const val GOOGLE_BASE_URL = "https://maps.googleapis.com/maps/api/"
        const val GOOGLE_PARAMETER_LOCATION = "location"
        const val GOOGLE_PARAMETER_RANK_BY = "rankby"
        const val GOOGLE_PARAMETER_TYPE = "type"
        const val GOOGLE_PARAMETER_KEYWORD = "keyword"
        const val GOOGLE_PARAMETER_RADIUS = "radius"
        const val GOOGLE_PARAMETER_LANGUAGE = "language"
        const val GOOGLE_PARAMETER_LATLNG = "latlng"
        const val GOOGLE_PARAMETER_KEY = "key"
        const val GOOGLE_PARAMETER_MAX_WIDTH = "maxwidth"
        const val GOOGLE_PARAMETER_PHOTO_REFERENCE = "photoreference"
        const val GOOGLE_PARAMETER_INPUT = "input"
        const val GOOGLE_PARAMETER_PLACE_ID = "placeid"
        const val GOOGLE_PARAMETER_SENSOR = "sensor"
        const val GOOGLE_PARAMETER_ORIGIN = "origin"
        const val GOOGLE_PARAMETER_DESTINATION = "destination"
        const val GOOGLE_PARAMETER_ORIGINS = "origins"
        const val GOOGLE_PARAMETER_DESTINATIONS = "destinations"
        const val GOOGLE_ADDRESS_REVERSE_GEOCODE = "geocode/json"
        const val GOOGLE_DISTANCE_MATRIX = "distancematrix/json?units=imperial&"
        const val GOOGLE_DIRECTIONS = "directions/json"
        /*const val GOOGLE_PLACE = "place/nearbysearch/json"
        const val GOOGLE_PLACE_PHOTO = "place/photo"
        const val GOOGLE_ADDRESS_GEOCODING = "place/autocomplete/json"
        const val GOOGLE_ADDRESS_PLACE_DETAILS = "place/details/json"
        const val GOOGLE_ADDRESS_REVERSE_GEOCODE = "geocode/json"
        const val GOOGLE_DIRECTIONS = "directions/json"
        const val GOOGLE_DISTANCE_MATRIX = "distancematrix/json?units=imperial&"

        const val GOOGLE_PARAMETER_ORIGIN = "origin"
        const val GOOGLE_PARAMETER_DESTINATION = "destination"
        const val GOOGLE_PARAMETER_ORIGINS = "origins"
        const val GOOGLE_PARAMETER_DESTINATIONS = "destinations"
        const val GOOGLE_PARAMETER_KEY = "key"
        const val GOOGLE_PARAMETER_LATLNG = "latlng"
*/
    }

}
