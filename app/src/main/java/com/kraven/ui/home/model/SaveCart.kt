package com.kraven.ui.home.model


import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
class SaveCart(
        @SerializedName("wallet")
        val wallet: String? = null,
        @SerializedName("distance")
        val distance: String? = null,
        @SerializedName("restaurant_id")
        var restaurantId: String? = null,
        @SerializedName("address_id")
        var addressId: String? = null,
        @SerializedName("discount")
        val discount: String? = null,
        @SerializedName("order_datetime")
        var orderDatetime: String? = null,
        @SerializedName("dishes")
        var dishes: List<DishesItem>? = null,
        @SerializedName("total_vat")
        val totalVat: String? = null,
        @SerializedName("total")
        var total: String? = null,
        @SerializedName("delivery_type")
        var deliveryType: String? = null,
        @SerializedName("vat_percent")
        val vatPercent: String? = null,
        @SerializedName("sub_total")
        val subTotal: String? = null,
        @SerializedName("order_type")
        val orderType: String? = null,
        @SerializedName("payment_method")
        var paymentMethod: String? = null,
        @SerializedName("menuId")
        var menuId: String? = null
) : Parcelable


/*
data class ToppingsItems(@SerializedName("price")
                         val price: String = "",
                         @SerializedName("name")
                         val name: String = "")
*/
@Parcelize
class DishesItem(
        @SerializedName("original_price")
        var originalPrice: String? = null,
        @SerializedName("id")
        var id: String? = null,
        @SerializedName("dish_id")
        var dishId: String? = null,
        @SerializedName("menu_id")
        var menuId: String? = null,
        @SerializedName("beverage_id")
        var beverageId: String? = null,
        @SerializedName("dish")
        var dish: String? = null,
        @SerializedName("total_price")
        var totalPrice: Float? = null,
        @SerializedName("vat")
        var vat: String? = null,
        @SerializedName("vat_price")
        var vatPrice: String? = null,
        var calculateVatPrice: String? = null,
        @SerializedName("special_note")
        var specialNote: String? = null,
        @SerializedName("price")
        var price: Float? = 0F,
        @SerializedName("qty")
        var qty: Int? = 0,
        @SerializedName("item_id")
        var itemId: String? = null,
        @SerializedName("item")
        var item: String? = null,
        @SerializedName("milliliter")
        var milliliter: String? = null,
        @SerializedName("image")
        var image: String? = null,

        @SerializedName("toppings")
        var toppings: ArrayList<ToppingsItems>? = null,

        @SerializedName("menu")
        var menu: String? = null,
        @SerializedName("status")
        var status: String? = null,
        @SerializedName("food")
        var food: String? = null,
        @SerializedName("name")
        var name: String? = null,

        @SerializedName("dishPriceTopping")
        var dishPriceTopping: Float? = null,

        @SerializedName("restaurantMenu")
        var restaurantMenu: RestaurantMenu? = null,

        @SerializedName("special_offer")
        var specialOffer: Float? = null,

        @SerializedName("description")
        var description: String? = null
) : Parcelable


@Parcelize
class DishesItemBeverage(
        @SerializedName("id")
        var id: String? = null,
        @SerializedName("menu_id")
        var menuId: String? = null,
        @SerializedName("total_price")
        var totalPrice: Float? = null,
        @SerializedName("special_note")
        var specialNote: String? = null,
        @SerializedName("price")
        var price: Float? = 0F,
        @SerializedName("qty")
        var qty: Int? = 0,
        @SerializedName("menu")
        var menu: String? = null,
        @SerializedName("status")
        var status: String? = null,
        @SerializedName("beverage_id")
        var beverage_id: String? = null,
        @SerializedName("name")
        var name: String? = null,
        @SerializedName("dish")
        var dish: String? = null,
        @SerializedName("dishPriceTopping")
        var dishPriceTopping: Float? = null
) : Parcelable

@Parcelize
class SendItems(
        @SerializedName("dish_id")
        var dishId: String? = null,
        @SerializedName("id")
        var id: String? = null,
        @SerializedName("menu_id")
        var menuId: String? = null,
        @SerializedName("dish")
        var dish: String? = null,
        @SerializedName("special_offer")
        var specialOffer: String? = null,
        @SerializedName("total_price")
        var totalPrice: Float? = null,
        @SerializedName("vat")
        var vat: String? = null,
        @SerializedName("vat_price")
        var vatPrice: String? = null,
        @SerializedName("special_note")
        var specialNote: String? = null,
        @SerializedName("price")
        var price: Float? = 0F,
        @SerializedName("qty")
        var qty: Int? = 0,
        /*@SerializedName("toppings")
        @Expose
        var sendToppings: Map<String,ArrayList<ToppingListItem>>?=null*/
        @SerializedName("toppings")
        @Expose
        var sendToppings: ArrayList<SendToppingsItems>? = null,
        /*@SerializedName("extra")
        var extra: List<ToppingsItem>? = null
        @SerializedName("dips")
        var dips: List<ToppingsItem>? = null*/
        @SerializedName("menu")
        var menu: String? = null,
        @SerializedName("status")
        var status: String? = null,
        @SerializedName("food")
        var food: String? = null,
        @SerializedName("name")
        var name: String? = null,

        @SerializedName("dishPriceTopping")
        var dishPriceTopping: Float? = null
) : Parcelable




