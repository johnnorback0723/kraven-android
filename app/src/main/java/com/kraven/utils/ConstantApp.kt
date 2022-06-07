package com.kraven.utils

import com.kraven.ui.address.model.Address

class ConstantApp {

    companion object {
        var INPUT_DATE_FORMAT = "dd MMM yyyy"
        var TIME_INPUT_FORMAT = "HH:mm"
        var TIME_OUTPUT_FORMAT = "hh:mm a"
        val ADD: String? = "Add"
        val EDIT: String? = "edit"
        const val FACEBOOK: String = "facebook"
        const val GOOGLE: String = "google"
        const val CASH = "Cash"
        const val CARD = "Card"
        const val DELIVERY = "Delivery"
        const val PICKUP = "Pickup"
        const val NOW = "Now"
        const val FUTURE = "Future"
        const val RESTAUTANT = "Restaurant"
        const val BEVERAGE = "Beverage"

        const val REQUEST_CODE_IMMEDIATE_UPDATE = 17362
    }

    object Firebase {
        const val KEY_NOTIFICATION = "key_notification"
        const val NEW_ORDER_ACCEPT = "new_order"
        const val ACCEPT_FOODORDER = "accept_foodorder"
        const val DELIVERED_FOOD_ORDER_BY_RESTAURANT = "delivered_food_order_by_restaurant"
        const val DELIVERED_FOOD_ORDER_BY_DRIVER = "delivered_food_order_by_driver"
        const val CANCEL_FOODORDER = "cancel_foodorder"
        const val PICKEDUP_FOOD_ORDER_BY_DRIVER = "pickedup_food_order_by_driver"

        const val STARTED_BEVERAGE_ORDER_BY_DRIVER = "started_beverage_order_by_driver"
        const val PICKEDUP_BEVERAGE_ORDER_BY_DRIVER = "pickedup_beverage_order_by_driver"
        const val DELIVERED_BEVERAGE_ORDER_BY_DRIVER = "delivered_beverage_order_by_driver"

        //Beverage Push TAG
        const val CANCEL_BEVERAGEORDER = "cancel_beverageorder"
        const val ACCEPT_BEVERAGEORDER = "accept_beverageorder"

        //Special Beverage Push Tag
        const val ACCEPT_SPECIAL_BEVERAGE = "accept_special_beverage"
        const val REJECT_SPECIAL_BEVERAGE = "reject_special_beverage"
        const val COMPLETE_SPECIAL_BEVERAGE = "complete_special_beverage"
        const val CANCEL_SPECIAL_BEVERAGE = "cancel_special_beverage"

        //Automatically complete PickUp Order
        const val COMPLETE_FOOD_ORDER_AUTOMATICALLY = "complete_food_order_automatically"
        const val COMPLETE_BEVERAG_ORDER_AUTOMATICALLY = "complete_beverag_order_automatically"

    }


    class PassValue {
        companion object {
            const val ITEM = "item"
            const val NEW_ISLAND = "new_island"
            const val TAG = "tag"
            const val FLAG = "flag"
            const val ORDER_ID = "order_id"
            const val deliveryTypes = "deliveryTypes"
            const val cuisinLists = "cuisinLists"
            const val IS_CART = "is_cart"
            const val SAVE_CART = "save_cart"
            const val RESTAURANT_MENU = "restaurant_menu"
            const val MENU_LIST = "menu_list"
            const val CURRENT_LAT_LONG = "current_lat_long"
            const val RESTAURANT_ID = "restaurant_id"
            const val TYPE = "type"
            const val ADDRESS_ID = "address_id"

            const val FUTUREDATE = "futuredate"

            const val passItems = "PASS_CART_LIST"
            const val COUNTRY_CODE = "country_code"
            const val DIAL_CODE = "dial_code"
            const val TOPPING_POSITION = "topping_position"
            const val MENU_POSITION = "menu_position"
            const val ORDER_FRAGMENT_POSITION = "fragment_position"
            // bundleOf("fragment_position" to position)
            const val ORDER_DETAIL = "order_detail"
            const val PAYMENT_POSITION = "payment_position"
            const val STATUS = "status"
            const val BEVERAGE_ON_THE_WAY = "beverage_on_the_way"
            const val PARAMETERS = "parameters"
            const val POSITION = "address_position"
            const val ADDRESS = "address"
            const val TermsLink = "terms_link"
            const val TermsHeder = "terms_heder"
            const val ORDER_FOOD = "order_food"
            const val ORDER_FOOD_FUTURE = "order_food_future"
            const val ORDER_BEVERAGE = "order_beverage"
            const val ORDER_BEVERAGE_SPECIAL = "order_beverage_special"
            //const val ORDER_CUISINE = "order_cuisine"
            const val CUISINE_ID="cuisine_id"
            const val CUISINE_NAME="cuisine_name"
            const val FRAGMENT = "fragment"
            const val USER = "user"
            const val IS_FACEBOOK = "is_facebook"
            const val IS_GOOGLE = "is_facebook"
            const val IS_LOGIN_TYPE = "is_login_type"
            const val RESTAURANT_DISTANCE = "restaurant_distancw"
        }
    }

    class Validation {
        companion object {
            var NAME_VALIDATION = "^[\\p{L}. '-]+$"
        }
    }

    object RestaurantStatus {
        const val OPEN = "Open"
        const val CLOSED = "Closed"
        const val AVAILABLE = "Available"
        const val UNAVAILABLE = "Unavailable"
        const val CURRENTLY_UNAVAILABLE = "Currently Unavailable"
    }

    object OrderStatus {
        const val PENDING = "Pending"
        const val CANCELLED = "Cancelled"
        const val COMPLETED = "Completed"
        const val DELIVERED = "Delivered"
        const val ACCEPTED = "Accepted"
        const val PREPARING = "Preparing"
        const val REJECTED = "Rejected"
        const val ONTHEWAY = "Ontheway"
        const val CONFIRMED = "Confirmed"
    }

    object DriverStatus {
        const val STARTED = "Started"
        const val PICKEDUP = "PickedUp"
        const val ACCEPTED = "Accepted"

    }

    class SaveValue {
        companion object {
            var address: Address? = null
            var isPickUpOrder: Boolean = false
            const val ADDRESS = "address"
            var addressType = "addressType"
            var displayAddress = "displayAddress"
            var CART_LIST = "cart_list"
        }
    }
}