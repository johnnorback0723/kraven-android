package com.kraven.ui.home.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class NewDishList(
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("dish_id")
    var dishId: String? = null,
    @SerializedName("menu_id")
    var menuId: String? = null,
    @SerializedName("item_id")
    var itemId: String? = null,
    @SerializedName("beverageId")
    var beverageId:String?=null,
    @SerializedName("dish")
    var dish: String? = null,
    @SerializedName("special_note")
    var specialNote: String? = null,
    @SerializedName("description")
    var description: String? = null,
    @SerializedName("price")
    var price: Float? = null,
    @SerializedName("qty")
    var qty: Int? = null,
    @SerializedName("toppings")
    var toppings: ArrayList<ToppingsItems>? = null,
    /*@SerializedName("extra")
    var extra: List<ToppingsItem>? = null
    @SerializedName("dips")
    var dips: List<ToppingsItem>? = null*/
    @SerializedName("menu")
    var menu: String? = null,
    @SerializedName("restaurantMenu")
    var restaurantMenu: RestaurantMenu? = null,
    @SerializedName("special_offer")
    var specialOffer: Float? = null,
    @SerializedName("status")
    var status: String? = null,
    @SerializedName("food")
    var food: String? = null,
    @SerializedName("image")
    var image: String? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("dishPriceTopping")
    var dishPriceTopping: Float? =null,
    @SerializedName("total_price")
    var totalPrice: Float? =null,
    @SerializedName("milliliter")
    var milliliter:String?=null
) : Parcelable