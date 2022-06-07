package com.kraven.ui.home.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/*
@Parcelize
data class ToppingsItem(@SerializedName("price")
                        val price: Float = 0F,
                        @SerializedName("name")
                        val name: String = "",
                        @SerializedName("status")
                        val status: String = "",
                        @SerializedName("id")
                        val id: Int = 0) : Parcelable

@Parcelize
data class ExtraItem(@SerializedName("price")
                     val price: Float = 0F,
                     @SerializedName("name")
                     val name: String = "",
                     @SerializedName("id")
                     val id: Int = 0) : Parcelable

@Parcelize
data class DipsItem(@SerializedName("price")
                    val price: Int = 0,
                    @SerializedName("name")
                    val name: String = "",
                    @SerializedName("id")
                    val id: Int = 0) : Parcelable


@Parcelize
data class RestaurantMenu(@SerializedName("special_offer")
                          val specialOffer: Int = 0,
                          @SerializedName("restaurant_id")
                          val restaurantId: Int = 0,
                          @SerializedName("price")
                          val price: Float = 0F,
                          @SerializedName("dips")
                          val dips: List<ToppingsItem>?,
                          @SerializedName("extra")
                          val extra: List<ToppingsItem>?,
                          @SerializedName("name")
                          val name: String = "",
                          @SerializedName("description")
                          val description: String = "",
                          @SerializedName("toppings")
                          val toppings: List<ToppingsItem>?,
                          @SerializedName("id")
                          val id: Int = 0,
                          @SerializedName("menu_id")
                          val menuId: Int = 0,
                          @SerializedName("status")
                          val status: String = "",
                          @SerializedName("food")
                          val food: String = "") : Parcelable
*/

@Parcelize
data class ToppingsItems(
        var avialbeCount: Int = 0,
        @SerializedName("id")
        var id: Int? = 0,
        @SerializedName("type")
        var type: String? = null,
        @SerializedName("category")
        var category: String? = null,
        @SerializedName("min_qty")
        var minQty: Int? = null,
        @SerializedName("min_count")
        var minCount: Int = 0,
        @SerializedName("list")
        var toppingList: ArrayList<ToppingListItem>? = null
) : Parcelable


@Parcelize
data class ToppingListItem(
        var isCheckItem: Boolean = false,
        @SerializedName("inserDate")
        var inserDateTime: String? = null,
        @SerializedName("price")
        var price: Float? = 0F,
        @SerializedName("name")
        var name: String? = null,
        @SerializedName("id")
        var id: Int? = null,
        @SerializedName("vat_price")
        var vatPrice: String? = null,
        @SerializedName("status")
        var status: String? = null
) : Parcelable

@Parcelize
class SendToppingsItems(
        @SerializedName("id")
        var id: Int? = 0,
        @SerializedName("type")
        var type: String? = null,
        @SerializedName("category")
        var category: String? = null,
        @SerializedName("list")
        var toppingList: ArrayList<SendToppingListItem>? = null
) : Parcelable

@Parcelize
data class SendToppingListItem(
        @SerializedName("price")
        var price: Float? = 0F,
        @SerializedName("name")
        var name: String? = null,
        @SerializedName("id")
        var id: Int? = null
) : Parcelable


@Parcelize
class RestaurantMenu(
        @SerializedName("original_price")
        var originalPrice: String? = null,
        @SerializedName("special_offer")
        var specialOffer: String? = null,
        @SerializedName("restaurant_id")
        var restaurantId: String? = null,
        @SerializedName("price")
        var price: String? = null,
        @SerializedName("name")
        var name: String? = null,
        @SerializedName("description")
        var description: String? = null,
        @SerializedName("toppings")
        var toppings: ArrayList<ToppingsItems>? = null,
        @SerializedName("id")
        var id: Int? = null,
        @SerializedName("menu_id")
        var menuId: Int? = null,
        @SerializedName("vat")
        var vat: String? = null,
        @SerializedName("vat_price")
        var vatPrice: String? = null,

        @SerializedName("food")
        var food: String? = null,
        @SerializedName("toppingPrice")
        var toppingPrice: Float? = null,
        @SerializedName("qty")
        var qty: Int? = null,
        @SerializedName("dish")
        var dish: String? = null,
        @SerializedName("menu")
        var menu: String? = null,
        @SerializedName("status")
        val status: String? = null
) : Parcelable





