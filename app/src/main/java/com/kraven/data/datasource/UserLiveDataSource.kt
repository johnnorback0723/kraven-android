package com.kraven.data.datasource


import android.content.Context
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.kraven.core.AppExecutors
import com.kraven.core.Session
import com.kraven.core.StatusCode
import com.kraven.data.pojo.DataWrapper
import com.kraven.data.pojo.Parameters
import com.kraven.data.pojo.User
import com.kraven.data.repository.UserRepository
import com.kraven.data.service.UserService
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
import com.kraven.ui.viewmodel.Address
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserLiveDataSource @Inject constructor(private val userService: UserService, val appExecutors: AppExecutors,
                                             private val context: Context, private val gson: Gson) :
        BaseDataSource(), UserRepository {
    override fun getNotificationList(parameters: Parameters): Single<DataWrapper<List<Notifications>>> {
        return execute(userService.notificationList(parameters))
    }

    override fun checkLiveOrder(): Single<DataWrapper<Any>> {
        return execute(userService.checkLiveOrder())
    }

    override fun getDishList(parameters: Parameters): Single<DataWrapper<List<RestaurantMenu>>> {
        return execute(userService.getDishList(parameters))
    }

    @Inject
    lateinit var session: Session


    override fun giveRateReview(parameters: Parameters): Single<DataWrapper<Any>> {
        return execute(userService.giveRateReview(parameters))
    }

    override fun cancelOrder(parameters: Parameters): Single<DataWrapper<Any>> {
        return execute(userService.cancelOrder(parameters))
    }

    override fun cancelOrderBeverage(parameters: Parameters): Single<DataWrapper<Any>> {
        return execute(userService.cancelOrderBeverage(parameters))
    }

    override fun getFavoriteList(parameters: Parameters): Single<DataWrapper<List<Restaurants>>> {
        return execute(userService.getFavoriteList(parameters))
    }

    override fun getFavoriteBeverageList(parameters: Parameters): Single<DataWrapper<List<Restaurants>>> {
        return execute(userService.getFavoriteBeverageList(parameters))
    }

    override fun getOrderDetails(parameters: Parameters): Single<DataWrapper<OrderDetail>> {
        return execute(userService.getOrderDetails(parameters))
    }

    override fun getOrderBeverageDetails(parameters: Parameters): Single<DataWrapper<BeverageHistoryDetails>> {
        return execute(userService.getOrderBeverageDetails(parameters))
    }

    override fun getDriverLatLong(parameters: Parameters): Single<DataWrapper<Any>> {
        return execute(userService.getDriverLatLong(parameters))
    }

    override fun checkRestaurantDeliveryRadius(parameters: Parameters): Single<DataWrapper<Any>> {
        return execute(userService.checkRestaurantDeliveryRadius(parameters))
    }

    override fun checkBeverageDeliveryRadius(parameters: Parameters): Single<DataWrapper<Any>> {
        return execute(userService.checkBeverageDeliveryRadius(parameters))
    }

    override fun getRestaurantList(parameters: Parameters): Single<DataWrapper<List<Restaurants>>> {
        parameters.islandId = session.user?.islandId
        return execute(userService.getRestaurantList(parameters))
    }

    override fun restaurantBranchList(parameters: Parameters): Single<DataWrapper<List<Restaurants>>> {
        parameters.islandId = session.user?.islandId
        return execute(userService.restaurantBranchList(parameters))
    }

    override fun beverageList(parameters: Parameters): Single<DataWrapper<List<Restaurants>>> {
        parameters.islandId = session.user?.islandId
        return execute(userService.getBeverageList(parameters))
    }

    override fun beverageBranchList(parameters: Parameters): Single<DataWrapper<List<Restaurants>>> {
        parameters.islandId = session.user?.islandId
        return execute(userService.beverageBranchList(parameters))
    }

    override fun addFavouriteRestaurant(parameters: Parameters): Single<DataWrapper<Any>> {
        return execute(userService.addFavouriteRestaurant(parameters))
    }

    override fun addFavouriteBeverage(parameters: Parameters): Single<DataWrapper<Any>> {
        return execute(userService.addFavouriteBeverage(parameters))
    }

    override fun addMoneyToWallet(parameters: HashMap<String, Any>): Single<DataWrapper<Any>> {
        return execute(userService.addMoneyToWallet(parameters))
    }

    override fun addToFavoriteRestaurant(parameters: Parameters): Single<DataWrapper<Any>> {
        return execute(userService.addToFavoriteRestaurant(parameters))
    }

    override fun getSetting(): Single<DataWrapper<UserSettings>> {
        return execute(userService.getSetting().doOnSuccess {
            if (it.code == StatusCode.CODE_SUCCESS) {
                session.userSettings = it.data
            }
        })
    }

    override fun getPromocodeList(parameters: Parameters): Single<DataWrapper<List<Promocode>>> {
        return execute(userService.promocodeList(parameters))
    }

    override fun getRestaurantDetails(parameters: Parameters): Single<DataWrapper<RestaurantDetails>> {
        return execute(userService.getRestaurantDetails(parameters))
    }

    override fun getBeverageDetails(parameters: Parameters): Single<DataWrapper<RestaurantDetails>> {
        return execute(userService.getBeverageDetails(parameters))
    }

    override fun menuList(parameters: Parameters): Single<DataWrapper<List<RestaurantMenu>>> {
        return execute(userService.getMenuList(parameters))
    }

    override fun beverageMenuList(parameters: Parameters): Single<DataWrapper<List<Beverage>>> {
        return execute(userService.beverageMenuList(parameters))
    }

    override fun getBeverageMenuListSearch(parameters: Parameters): Single<DataWrapper<List<Beverage>>> {
        return execute(userService.getBeverageMenuListSearch(parameters))
    }

    override fun beverageItemsByid(parameters: Parameters): Single<DataWrapper<List<Beverage>>> {
        return execute(userService.beverageItemsByid(parameters))
    }


    /* override fun placeOrder(parameters: HashMap<String, Any>): Single<DataWrapper<Any>> {
         return execute(userService.placeOrder(parameters))
     }

     override fun placeOrderBeverage(parameters: HashMap<String, Any>): Single<DataWrapper<Any>> {
         return execute(userService.placeOrderBeverage(parameters))
     }*/

    override fun checkPromocode(parameters: Parameters): Single<DataWrapper<Promocode>> {
        return execute(userService.checkPromocode(parameters))
    }

    override fun getOrderHistory(parameters: Parameters): Single<DataWrapper<List<OrderList>>> {
        return execute(userService.getOrderHistory(parameters))
    }

    override fun addCard(parameters: Parameters): Single<DataWrapper<Any>> {
        return execute(userService.addCard(parameters))
    }

    override fun cardList(parameters: Parameters): Single<DataWrapper<List<Card>>> {
        return execute(userService.getCardList(parameters))
    }

    override fun deleteCard(parameters: Parameters): Single<DataWrapper<Any>> {
        return execute(userService.deleteCard(parameters))
    }

    override fun getOrderBeverageHistory(page: String): Single<DataWrapper<List<BeverageHistory>>> {
        return execute(userService.getOrderBeverageHistory(Parameters(page = page)))
    }

    override fun getTitleList(): Single<DataWrapper<Title>> {
        return execute(userService.getTitleList())
    }


    override fun geocode(key: String, latLng: LatLng): Single<MutableList<String>> {
        return Single.create<MutableList<String>> { emitter ->
            val client = OkHttpClient.Builder()
                    .writeTimeout(3, TimeUnit.MINUTES)
                    .readTimeout(3, TimeUnit.MINUTES)
                    .connectTimeout(3, TimeUnit.MINUTES)
                    .build()
            val request = Request.Builder()
                    .url("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latLng.latitude + "," + latLng.longitude + "&key=" + key)
                    .build()

            try {
                val response = client.newCall(request).execute()

                if (response.code == 200) {
                    val responce = response.body?.string()
                    val mObject = Gson().fromJson<Address>(responce, Address::class.java)
                    val strings = mutableListOf<String>()

                    if (mObject.status == StatusCode.OK) {
                        strings.add(mObject.getResults().get(0).getFormattedAddress() + "")
                        emitter.onSuccess(strings)
                    } else {
                        // strings.addBeverage(mObject.getResults().get(0).getFormattedAddress() + "")
                    }
                } else emitter.onSuccess(mutableListOf())
            } catch (e1: IOException) {
                e1.printStackTrace()
                emitter.onSuccess(mutableListOf())
            } catch (e1: JsonSyntaxException) {
                e1.printStackTrace()
                emitter.onSuccess(mutableListOf())
            } catch (e1: Exception) {
                e1.printStackTrace()
                emitter.onSuccess(mutableListOf())
            }
        }
    }


    //Spcial Beverage API

    override fun specialBeverageDropDownList(): Single<DataWrapper<QuantityTypeList>> {
        return execute(userService.specialBeverageDropDownList())
    }

    /* override fun placeSpecialBeverageOrder(parameters: HashMap<String, Any>): Single<DataWrapper<Any>> {
         return execute(userService.placeSpecialBeverageOrder(parameters))
     }
 */
    override fun getSpecialBeverageList(parameters: Parameters): Single<DataWrapper<List<SpecialBeverage>>> {
        return execute(userService.getSpecialBeverageList(parameters))
    }

    override fun getSpecialBeverageOrderDetails(parameters: Parameters): Single<DataWrapper<SpecialBeverageDetails>> {
        return execute(userService.getSpecialBeverageOrderDetails(parameters))
    }

    override fun cancelSpecialBeverageOrder(parameters: Parameters): Single<DataWrapper<Any>> {
        return execute(userService.cancelSpecialBeverageOrder(parameters))
    }


    override fun getNewCuisineList(): Single<DataWrapper<AllCuisine>> {
        return execute(userService.getNewCuisineList())
    }

    override fun checkToolFeeLocation(parameters: Parameters): Single<DataWrapper<TollFee>> {
        return execute(userService.checkToolFeeLocation(parameters))
    }


    override fun deleteSavedCards(parameters: Parameters): Single<DataWrapper<Any>> {
        return execute(userService.deleteSavedCards(parameters))
    }


    override fun beforeVerifyFoodOrder(parameters: Parameters): Single<DataWrapper<Any>> {
        return execute(userService.beforeVerifyFoodOrder(parameters))
    }


    override fun beforeVerifyBeverageOrder(parameters: Parameters): Single<DataWrapper<Any>> {
        return execute(userService.beforeVerifyBeverageOrder(parameters))
    }

    override fun deleteFacLog(parameters: Parameters): Single<DataWrapper<Any>> {
        return execute(userService.deleteFacLog(parameters))
    }

    override fun cancelHostedPageFood(id: String): Single<DataWrapper<Any>> {
        return execute(userService.cancelHostedPageFood(id))
    }

    override fun cancelHostedPageBeverage(id: String): Single<DataWrapper<Any>> {
        return execute(userService.cancelHostedPageBeverage(id))
    }

    override fun addWalletMoneyFac(parameters: Parameters): Single<DataWrapper<Any>> {
        return execute(userService.addWalletMoneyFac(parameters))
    }


    override fun getIslandList(): Single<DataWrapper<List<Island>>> {
        return execute(userService.getIslandList())
    }

    override fun setIsland(parameters: Parameters): Single<DataWrapper<User>> {
        //return execute(userService.setIsland(parameters))
        return execute(userService.setIsland(parameters).doOnSuccess {
            if (it.code == StatusCode.CODE_SUCCESS) {
                session.user = it.data
            }
        })
    }
}
