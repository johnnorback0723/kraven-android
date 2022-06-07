package com.kraven.data.repository


import com.google.android.gms.maps.model.LatLng
import com.kraven.data.pojo.DataWrapper
import com.kraven.data.pojo.Parameters
import com.kraven.data.pojo.User
import com.kraven.ui.authentication.model.Island
import com.kraven.ui.cart.model.TollFee
import com.kraven.ui.home.model.*
import com.kraven.ui.my.order.model.OrderList
import com.kraven.ui.order.beverage.model.Beverage
import com.kraven.ui.my.order.model.BeverageHistory
import com.kraven.ui.my.order.model.BeverageHistoryDetails
import com.kraven.ui.notification.model.Notifications
import com.kraven.ui.order.beverage.model.QuantityTypeList
import com.kraven.ui.order.beverage.model.SpecialBeverage
import com.kraven.ui.order.beverage.model.SpecialBeverageDetails
import com.kraven.ui.payment.model.CardResponse
import com.kraven.ui.payment.model.HostedPageModel
import com.kraven.ui.payment.setting.model.Card
import com.kraven.ui.setting.model.Title
import com.kraven.ui.track.model.OrderDetail
import io.reactivex.Single

interface UserRepository {

    fun getRestaurantList(parameters: Parameters): Single<DataWrapper<List<Restaurants>>>

    fun restaurantBranchList(parameters: Parameters): Single<DataWrapper<List<Restaurants>>>

    fun beverageList(parameters: Parameters): Single<DataWrapper<List<Restaurants>>>

    fun beverageBranchList(parameters: Parameters): Single<DataWrapper<List<Restaurants>>>

    fun addFavouriteRestaurant(parameters: Parameters): Single<DataWrapper<Any>>

    fun addFavouriteBeverage(parameters: Parameters): Single<DataWrapper<Any>>

    fun getSetting(): Single<DataWrapper<UserSettings>>

    fun getPromocodeList(parameters: Parameters): Single<DataWrapper<List<Promocode>>>

    fun getRestaurantDetails(parameters: Parameters): Single<DataWrapper<RestaurantDetails>>

    fun getBeverageDetails(parameters: Parameters): Single<DataWrapper<RestaurantDetails>>

    fun menuList(parameters: Parameters): Single<DataWrapper<List<RestaurantMenu>>>

    fun beverageMenuList(parameters: Parameters): Single<DataWrapper<List<Beverage>>>

    fun getBeverageMenuListSearch(parameters: Parameters): Single<DataWrapper<List<Beverage>>>

    fun getNotificationList(parameters: Parameters): Single<DataWrapper<List<Notifications>>>

    fun beverageItemsByid(parameters: Parameters): Single<DataWrapper<List<Beverage>>>


    //fun placeOrder(parameters: HashMap<String, Any>): Single<DataWrapper<Any>>

    //fun placeOrderBeverage(parameters: HashMap<String, Any>): Single<DataWrapper<Any>>


    fun checkPromocode(parameters: Parameters): Single<DataWrapper<Promocode>>

    fun getOrderHistory(parameters: Parameters): Single<DataWrapper<List<OrderList>>>

    fun getDishList(parameters: Parameters): Single<DataWrapper<List<RestaurantMenu>>>

    //Add monet to wallet
    fun addMoneyToWallet(parameters: HashMap<String,Any>): Single<DataWrapper<Any>>

    //Add Restaurant to Favourite
    fun addToFavoriteRestaurant(parameters: Parameters): Single<DataWrapper<Any>>

    fun checkRestaurantDeliveryRadius(parameters: Parameters): Single<DataWrapper<Any>>

    fun checkBeverageDeliveryRadius(parameters: Parameters): Single<DataWrapper<Any>>

    fun getOrderDetails(parameters: Parameters): Single<DataWrapper<OrderDetail>>

    fun getOrderBeverageDetails(parameters: Parameters): Single<DataWrapper<BeverageHistoryDetails>>

    fun getDriverLatLong(parameters: Parameters): Single<DataWrapper<Any>>

    fun getFavoriteList(parameters: Parameters): Single<DataWrapper<List<Restaurants>>>

    fun getFavoriteBeverageList(parameters: Parameters): Single<DataWrapper<List<Restaurants>>>

    fun giveRateReview(parameters: Parameters): Single<DataWrapper<Any>>

    fun cancelOrder(parameters: Parameters): Single<DataWrapper<Any>>

    fun cancelOrderBeverage(parameters: Parameters): Single<DataWrapper<Any>>

    fun geocode(key: String, latLng: LatLng): Single<MutableList<String>>

    fun addCard(parameters: Parameters): Single<DataWrapper<Any>>



    fun cardList(parameters: Parameters): Single<DataWrapper<List<Card>>>

    fun deleteCard(parameters: Parameters): Single<DataWrapper<Any>>

    fun getOrderBeverageHistory(page: String): Single<DataWrapper<List<BeverageHistory>>>

    fun checkLiveOrder(): Single<DataWrapper<Any>>
    
    fun getTitleList(): Single<DataWrapper<Title>>



    //Special Beverage API
    fun specialBeverageDropDownList():Single<DataWrapper<QuantityTypeList>>

    //fun placeSpecialBeverageOrder(parameters:HashMap<String,Any>):Single<DataWrapper<Any>>

    fun getSpecialBeverageList(parameters: Parameters):Single<DataWrapper<List<SpecialBeverage>>>

    fun getSpecialBeverageOrderDetails(parameters: Parameters):Single<DataWrapper<SpecialBeverageDetails>>

    fun cancelSpecialBeverageOrder(parameters: Parameters) :Single<DataWrapper<Any>>

    fun getNewCuisineList():Single<DataWrapper<AllCuisine>>

    fun checkToolFeeLocation(parameters: Parameters):Single<DataWrapper<TollFee>>

    fun deleteSavedCards(parameters: Parameters):Single<DataWrapper<Any>>

    fun beforeVerifyFoodOrder(parameters: Parameters):Single<DataWrapper<Any>>

    fun beforeVerifyBeverageOrder(parameters: Parameters):Single<DataWrapper<Any>>

    fun deleteFacLog(parameters: Parameters):Single<DataWrapper<Any>>

    fun cancelHostedPageFood(id: String):Single<DataWrapper<Any>>

    fun cancelHostedPageBeverage(id: String):Single<DataWrapper<Any>>

    fun addWalletMoneyFac(parameters: Parameters):Single<DataWrapper<Any>>

    // New Island Implement
    fun getIslandList():Single<DataWrapper<List<Island>>>

    fun setIsland(parameters: Parameters):Single<DataWrapper<User>>


}