package com.kraven.data.service

import com.kraven.data.URLFactory
import com.kraven.data.pojo.Parameters
import com.kraven.data.pojo.ResponseBody
import com.kraven.data.pojo.User
import com.kraven.ui.authentication.model.Island
import com.kraven.ui.cart.model.TollFee
import com.kraven.ui.home.model.*
import com.kraven.ui.my.order.model.BeverageHistory
import com.kraven.ui.my.order.model.BeverageHistoryDetails
import com.kraven.ui.my.order.model.OrderList
import com.kraven.ui.notification.model.Notifications
import com.kraven.ui.order.beverage.model.Beverage
import com.kraven.ui.order.beverage.model.QuantityTypeList
import com.kraven.ui.order.beverage.model.SpecialBeverage
import com.kraven.ui.order.beverage.model.SpecialBeverageDetails
import com.kraven.ui.payment.setting.model.Card
import com.kraven.ui.setting.model.Title
import com.kraven.ui.track.model.OrderDetail
import io.reactivex.Single
import io.reactivex.SingleObserver
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.net.URL

interface UserService {

    @POST(URLFactory.Method.NEARBY_RESTAURANT)
    fun getRestaurantList(@Body parameters: Parameters): Single<ResponseBody<List<Restaurants>>>

    @POST(URLFactory.Method.RESTAURANT_BRANCH_LIST)
    fun restaurantBranchList(@Body parameters: Parameters): Single<ResponseBody<List<Restaurants>>>

    @POST(URLFactory.Method.NEARBY_BEVERAGE)
    fun getBeverageList(@Body parameters: Parameters): Single<ResponseBody<List<Restaurants>>>

    @POST(URLFactory.Method.BEVERAGE_BRANCH_LIST)
    fun beverageBranchList(@Body parameters: Parameters): Single<ResponseBody<List<Restaurants>>>

    @POST(URLFactory.Method.ADD_FAVOURITE_RESTAURANT)
    fun addFavouriteRestaurant(@Body parameters: Parameters): Single<ResponseBody<Any>>

    @POST(URLFactory.Method.ADD_FAVOURITE_BEVERAGE)
    fun addFavouriteBeverage(@Body parameters: Parameters): Single<ResponseBody<Any>>

    @POST(URLFactory.Method.GET_SETTING)
    fun getSetting(): Single<ResponseBody<UserSettings>>

    @POST(URLFactory.Method.PROMOCODE_LIST)
    fun promocodeList(@Body parameters: Parameters): Single<ResponseBody<List<Promocode>>>

    @POST(URLFactory.Method.GET_RESTAURANT_DETAIL)
    fun getRestaurantDetails(@Body parameters: Parameters): Single<ResponseBody<RestaurantDetails>>

    @POST(URLFactory.Method.GET_BEVERAGE_RESTAURANT)
    fun getBeverageDetails(@Body parameters: Parameters): Single<ResponseBody<RestaurantDetails>>

    @POST(URLFactory.Method.GET_RESTAURANT_MENU_DISHES)
    fun getMenuList(@Body parameters: Parameters): Single<ResponseBody<List<RestaurantMenu>>>

    @POST(URLFactory.Method.GET_BEVERAGE_MENU_ITEMS)
    fun beverageMenuList(@Body parameters: Parameters): Single<ResponseBody<List<Beverage>>>

    @POST(URLFactory.Method.getBeverageMenuListSearch)
    fun getBeverageMenuListSearch(@Body parameters: Parameters): Single<ResponseBody<List<Beverage>>>

    @POST(URLFactory.Method.GET_BEVERAGE_ITEMS_BYID)
    fun beverageItemsByid(@Body parameters: Parameters): Single<ResponseBody<List<Beverage>>>

    @POST(URLFactory.Method.PLACEORDER)
    fun placeOrder(@Body parameters: HashMap<String, Any>): Single<ResponseBody<Any>>

    @POST(URLFactory.Method.ORDER_BEVERAGE_PLACEORDER)
    fun placeOrderBeverage(@Body parameters: HashMap<String, Any>): Single<ResponseBody<Any>>

    @POST(URLFactory.Method.GET_ORDER_FOOD_HISTORY)
    fun getOrderHistory(@Body parameters: Parameters): Single<ResponseBody<List<OrderList>>>

    @POST(URLFactory.Method.VERIFY_PROMOCODE)
    fun checkPromocode(@Body parameters: Parameters): Single<ResponseBody<Promocode>>

    @POST(URLFactory.Method.ADD_WALLET_MONEY)
    fun addMoneyToWallet(@Body parameters: HashMap<String, Any>): Single<ResponseBody<Any>>

    @POST(URLFactory.Method.ADD_FAVOURITE_RESTAURANT)
    fun addToFavoriteRestaurant(@Body parameters: Parameters): Single<ResponseBody<Any>>

    @POST(URLFactory.Method.CHECK_RESTAURANT_DELIVERY_RADIUS)
    fun checkRestaurantDeliveryRadius(@Body parameters: Parameters): Single<ResponseBody<Any>>

    @POST(URLFactory.Method.CHECK_BEVERAGE_DELIVERY_RADIUS)
    fun checkBeverageDeliveryRadius(@Body parameters: Parameters): Single<ResponseBody<Any>>

    @POST(URLFactory.Method.GET_ORDER_DETAILS)
    fun getOrderDetails(@Body parameters: Parameters): Single<ResponseBody<OrderDetail>>

    @POST(URLFactory.Method.GET_ORDER_BEVERAGE_DETAILS)
    fun getOrderBeverageDetails(@Body parameters: Parameters): Single<ResponseBody<BeverageHistoryDetails>>

    @POST(URLFactory.Method.GET_DRIVER_LAT_LONG)
    fun getDriverLatLong(@Body parameters: Parameters): Single<ResponseBody<Any>>

    @POST(URLFactory.Method.FAVORITELIST)
    fun getFavoriteList(@Body parameters: Parameters): Single<ResponseBody<List<Restaurants>>>

    @POST(URLFactory.Method.FAVOURITE_BEVERAGE_LIST)
    fun getFavoriteBeverageList(@Body parameters: Parameters): Single<ResponseBody<List<Restaurants>>>

    @POST(URLFactory.Method.GIVERATEREVIEW)
    fun giveRateReview(@Body parameters: Parameters): Single<ResponseBody<Any>>

    @POST(URLFactory.Method.CANCEL_FOOD_ORDER)
    fun cancelOrder(@Body parameters: Parameters): Single<ResponseBody<Any>>

    @POST(URLFactory.Method.CANCEL_BEVERAGE_ORDER)
    fun cancelOrderBeverage(@Body parameters: Parameters): Single<ResponseBody<Any>>

    @POST(URLFactory.Method.GET_RESTAURANT_DISHES_BYID)
    fun getDishList(@Body parameters: Parameters): Single<ResponseBody<List<RestaurantMenu>>>

    @POST(URLFactory.Method.ADD_CARD)
    fun addCard(@Body parameters: Parameters): Single<ResponseBody<Any>>

    @POST(URLFactory.Method.GET_CARD_LIST)
    fun getCardList(@Body parameters: Parameters): Single<ResponseBody<List<Card>>>

    @POST(URLFactory.Method.DELETE_CARD)
    fun deleteCard(@Body parameters: Parameters): Single<ResponseBody<Any>>

    @POST(URLFactory.Method.GET_ORDER_BEVERAGE_HISTORY)
    fun getOrderBeverageHistory(@Body parameters: Parameters): Single<ResponseBody<List<BeverageHistory>>>

    @POST(URLFactory.Method.NOTIFICATION_LIST)
    fun notificationList(@Body parameters: Parameters): Single<ResponseBody<List<Notifications>>>

    @POST(URLFactory.Method.CHECK_LIVE_ORDER_TRACKING)
    fun checkLiveOrder(): Single<ResponseBody<Any>>

    @POST(URLFactory.Method.CONTACT_US_LIST)
    fun getTitleList(): Single<ResponseBody<Title>>
    //Special Beverage API

    @POST(URLFactory.Method.SPECIAL_BEVERAGE_DROPDOWNLIST)
    fun specialBeverageDropDownList(): Single<ResponseBody<QuantityTypeList>>

    @POST(URLFactory.Method.GET_SPECIAL_BEVERAGE_ORDER_HISTORY)
    fun getSpecialBeverageList(@Body parameters: Parameters): Single<ResponseBody<List<SpecialBeverage>>>

    @POST(URLFactory.Method.GET_SPECIAL_ORDER)
    fun getSpecialBeverageOrderDetails(@Body parameters: Parameters): Single<ResponseBody<SpecialBeverageDetails>>

    @POST(URLFactory.Method.CANCEL_SPECIAL_BEVERAGE)
    fun cancelSpecialBeverageOrder(@Body parameters: Parameters): Single<ResponseBody<Any>>

    @POST(URLFactory.Method.GET_NEW_CUISINE_LIST)
    fun getNewCuisineList(): Single<ResponseBody<AllCuisine>>

    @POST(URLFactory.Method.CHECK_TOLLFEE_LOCATION)
    fun checkToolFeeLocation(@Body parameters: Parameters): Single<ResponseBody<TollFee>>

    @POST(URLFactory.Method.DELETE_SAVED_CARDS)
    fun deleteSavedCards(@Body parameters: Parameters):Single<ResponseBody<Any>>

    @POST(URLFactory.Method.BEFORE_VERIFY_FOOD_ORDER)
    fun beforeVerifyFoodOrder(@Body parameters: Parameters):Single<ResponseBody<Any>>

    @POST(URLFactory.Method.BEFORE_VERIFY_BEVERAGE_ORDER)
    fun beforeVerifyBeverageOrder(@Body parameters: Parameters):Single<ResponseBody<Any>>

    @POST(URLFactory.Method.DELETE_FAC_LOG)
    fun deleteFacLog(@Body parameters: Parameters):Single<ResponseBody<Any>>

    @GET(URLFactory.Method.CANCEL_HOSTED_PAGE_FOOD)
    fun cancelHostedPageFood(@Path("food_oder_id") id: String):Single<ResponseBody<Any>>

    @GET(URLFactory.Method.CANCEL_HOSTED_PAGE_BEVERAGE)
    fun cancelHostedPageBeverage(@Path("beverage_order_id") id: String):Single<ResponseBody<Any>>

    @POST(URLFactory.Method.ADD_WALLET_MONEY_FAC)
    fun addWalletMoneyFac(@Body parameters: Parameters):Single<ResponseBody<Any>>

    @POST(URLFactory.Method.GET_ISLAND_LIST)
    fun getIslandList():Single<ResponseBody<List<Island>>>

    @POST(URLFactory.Method.SET_ISLAND)
    fun setIsland(@Body parameters: Parameters):Single<ResponseBody<User>>
}