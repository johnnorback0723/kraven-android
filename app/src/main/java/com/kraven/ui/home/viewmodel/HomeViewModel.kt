package com.kraven.ui.home.viewmodel


import com.google.android.gms.maps.model.LatLng
import com.kraven.data.pojo.Parameters
import com.kraven.data.pojo.User
import com.kraven.data.repository.AuthenticationRepository
import com.kraven.data.repository.UserRepository
import com.kraven.ui.address.model.Address
import com.kraven.ui.authentication.model.AppVersion
import com.kraven.ui.base.APILiveData
import com.kraven.ui.base.BaseViewModel
import com.kraven.ui.cart.model.TollFee

import com.kraven.ui.home.model.*
import com.kraven.ui.notification.model.Notifications
import com.kraven.ui.order.beverage.model.Beverage
import com.kraven.ui.order.food.model.Filters
import com.kraven.ui.payment.model.Payment
import com.kraven.ui.portable.bar.rental.model.PortableBar
import com.kraven.ui.review.model.Review
import com.kraven.ui.wallet.model.Transaction
import com.kraven.utils.Formatter
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

class HomeViewModel @Inject constructor(private val userRepository: UserRepository,
                                        private val authenticationRepository: AuthenticationRepository) : BaseViewModel() {
    val getList = APILiveData<Service>()


    val getAddressList = APILiveData<List<Address>>()
    val getCardList = APILiveData<List<Payment>>()

    val getUserLiveData = APILiveData<User>()
    val getBeverageList = APILiveData<List<Restaurants>>()

    val getBeverageOrderDetailsList = APILiveData<List<Beverage>>()
    val getPortableBarList = APILiveData<List<PortableBar>>()
    val getTransactionList = APILiveData<List<Transaction>>()
    val getNotificationList = APILiveData<List<Notifications>>()

    val getFilterList = APILiveData<List<Filters.FilterName>>()

    val getRestaurantList = APILiveData<List<Restaurants>>()
    val restaurantBranchListLiveData = APILiveData<List<Restaurants>>()
    val addFavouriteRestaurant = APILiveData<Any>()
    val addMoneyToWallet = APILiveData<Any>()
    val addToFavoriteRestaurant = APILiveData<Any>()
    val reviews = APILiveData<List<Review>>()
    val getSetting = APILiveData<UserSettings>()
    val getPromoCodeList = APILiveData<List<Promocode>>()
    val getRestaurantDetails = APILiveData<RestaurantDetails>()
    val getMenuList = APILiveData<List<RestaurantMenu>>()
    val getBeverageMenuList = APILiveData<List<Beverage>>()
    val checkRestaurantDeliveryRadius = APILiveData<Any>()
    val checkBeverageDeliveryRadius = APILiveData<Any>()
    val getNewCuisineListLiveData = APILiveData<AllCuisine>()


    val beverageMenuItems = APILiveData<List<RestaurantMenu>>()
    val checkLiveOrder = APILiveData<Any>()
    val checkToolFeeLocationLiveData = APILiveData<TollFee>()

    val anyLiveData = APILiveData<Any>()

    var currentLatLng :LatLng?=null


    val getAppVersionLiveData = APILiveData<AppVersion>()

    fun getAppVersion(){
        authenticationRepository.getAppVersion().subscribe(withLiveData(getAppVersionLiveData))
    }


    fun checkToolFeeLocation(parameters: Parameters){
        userRepository.checkToolFeeLocation(parameters).subscribe(withLiveData(checkToolFeeLocationLiveData))
    }

    fun checkRestaurantDeliveryRadius(restaurantId: String, deliveryLatitude: String, deliveryLongitude: String) {
        userRepository.checkRestaurantDeliveryRadius(Parameters(restaurantId = restaurantId, deliveryLatitude = deliveryLatitude, deliveryLongitude = deliveryLongitude))
                .subscribe(withLiveData(checkRestaurantDeliveryRadius))
    }

    fun checkBeverageDeliveryRadius(restaurantId: String, deliveryLatitude: String, deliveryLongitude: String) {
        userRepository.checkBeverageDeliveryRadius(Parameters(beverageId = restaurantId, deliveryLatitude = deliveryLatitude, deliveryLongitude = deliveryLongitude))
                .subscribe(withLiveData(checkBeverageDeliveryRadius))
    }
    fun getUser(userId: String) {
        authenticationRepository.getUser(Parameters(userId = userId)).subscribe(withLiveData(getUserLiveData))
    }
    fun getPromoCodeList(page: String, islandId: String?) {
        userRepository.getPromocodeList(Parameters(islandId =islandId,page = page,date =Formatter.format(Date().toString(), Formatter.YYYY_MM_DD_T_HH_MM_SSS_Z, "yyyy-MM-dd", false).toString() ))
                .subscribe(withLiveData(getPromoCodeList))
    }

    fun getRestaurantList(parameters: Parameters) {
        userRepository.getRestaurantList(parameters)
                .subscribe(withLiveData(getRestaurantList))
    }

    fun restaurantBranchList(parameters: Parameters) {
        userRepository.restaurantBranchList(parameters)
                .subscribe(withLiveData(restaurantBranchListLiveData))
    }


    fun getBeverageList(parameters: Parameters) {
        userRepository.beverageList(parameters)
                .subscribe(withLiveData(getBeverageList))

    }
    fun beverageBranchList(parameters: Parameters) {
        userRepository.beverageBranchList(parameters)
                .subscribe(withLiveData(getBeverageList))

    }

    fun getRestaurantDetails(restaurantId: String, currentLatLng: LatLng, selectedDate: String, isPageVisit: String) {
        if (selectedDate != null && selectedDate.isNotEmpty()) {
            userRepository.getRestaurantDetails(Parameters(restaurantId = restaurantId,
                    latitude = currentLatLng.latitude.toString(),
                    longitude = currentLatLng.longitude.toString(),
                    isPageVisit = isPageVisit,
                    futureDate = Formatter.format(selectedDate, Formatter.YYYY_MM_DD_HH_MM_SS, "yyyy-MM-dd")))
                    .subscribe(withLiveData(getRestaurantDetails))
        } else {
            userRepository.getRestaurantDetails(Parameters(futureDate = Formatter.format(selectedDate, Formatter.YYYY_MM_DD_HH_MM_SS, "yyyy-MM-dd"),
                    isPageVisit = isPageVisit,
                    restaurantId = restaurantId, latitude = currentLatLng.latitude.toString(), longitude = currentLatLng.longitude.toString()))
                    .subscribe(withLiveData(getRestaurantDetails))
        }

    }

    fun getBeverageDetails(beverageId: String, currentLatLng: LatLng) {
        userRepository.getBeverageDetails(Parameters(beverageId = beverageId, latitude = currentLatLng.latitude.toString(), longitude = currentLatLng.longitude.toString()))
                .subscribe(withLiveData(getRestaurantDetails))

    }

    fun addFavouriteRestaurant(parameters: Parameters) {
        userRepository.addFavouriteRestaurant(parameters)
                .subscribe(withLiveData(addFavouriteRestaurant))
    }

    fun addFavouriteBeverage(parameters: Parameters) {
        userRepository.addFavouriteBeverage(parameters)
                .subscribe(withLiveData(addFavouriteRestaurant))
    }

    fun addMoneyToWallet(parameters: HashMap<String, Any>) {
        userRepository.addMoneyToWallet(parameters)
                .subscribe(withLiveData(addMoneyToWallet))
    }

    /*fun addToFavoriteRestaurant(parameters: Parameters) {
        userRepository.addToFavoriteRestaurant(parameters)
                .subscribe(withLiveData(addToFavoriteRestaurant))
    }*/

    fun getSetting() {
        userRepository.getSetting()
                .subscribe(withLiveData(getSetting))
    }

    fun checkLiveTracking() {
        userRepository.checkLiveOrder()
                .subscribe(withLiveData(checkLiveOrder))
    }

    fun getMenuList(menuId: String, restaurantId: String) {
        userRepository.menuList(Parameters(restaurantId = restaurantId, menuId = menuId))
                .subscribe(withLiveData(getMenuList))
    }

    fun getBeverageMenuList(menuId: String, restaurantId: String) {
        userRepository.beverageMenuList(Parameters(beverageId = restaurantId, menuId = menuId))
                .subscribe(withLiveData(getBeverageMenuList))
    }


    fun getBeverageMenuListSearch(search: String, restaurantId: String) {
        userRepository.getBeverageMenuListSearch(Parameters(search = search, beverageId = restaurantId))
                .subscribe(withLiveData(getBeverageMenuList))
    }

    fun getServiceList() {
        authenticationRepository.serviceList()
                .subscribe(withLiveData(getList))
    }


    fun getAddressList(parameters: Parameters) {
        authenticationRepository.getAddressList(parameters)
                .subscribe(withLiveData(getAddressList))
    }

    fun reviews(parameters: Parameters) {
        authenticationRepository.getReviewList(parameters)
                .subscribe(withLiveData(reviews))
    }

    fun getCardList() {
        authenticationRepository.cardList()
                .subscribe(withLiveData(getCardList))
    }


    fun getPortabalBarList() {
        authenticationRepository.getPortableBarList()
                .subscribe(withLiveData(getPortableBarList))
    }


    fun getTransactionList(parameters: String) {
        authenticationRepository.getTransactionList(Parameters(page = parameters))
                .subscribe(withLiveData(getTransactionList))
    }


    fun getNotificationList(parameters: Parameters) {
        userRepository.getNotificationList(parameters)
                .subscribe(withLiveData(getNotificationList))
    }


    fun getFilterList() {
        authenticationRepository.getFilterList()
                .subscribe(withLiveData(getFilterList))
    }

    /*fun getBeverageMenuItem(beverageId: String?, id: String?) {
        userRepository.beverageMenuListItem(Parameters(beverageId = beverageId,itemIds = id))
                .subscribe(withLiveData(beverageMenuItems))
    }
*/

    fun getNewCuisineList(){
        userRepository.getNewCuisineList().subscribe(withLiveData(getNewCuisineListLiveData))
    }

    fun updateDeviceInfo() {
        authenticationRepository.updateDeviceInfo().subscribe(withLiveData(anyLiveData))
    }
}