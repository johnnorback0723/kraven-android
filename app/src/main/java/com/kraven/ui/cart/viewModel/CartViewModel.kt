package com.kraven.ui.cart.viewModel


import com.google.android.gms.maps.model.LatLng
import com.kraven.data.URLFactory
import com.kraven.data.pojo.Parameters
import com.kraven.data.pojo.User
import com.kraven.data.repository.AuthenticationRepository
import com.kraven.data.repository.GoogleRepository
import com.kraven.data.repository.PlaceOrderRepository
import com.kraven.data.repository.UserRepository
import com.kraven.model.google.DistanceM
import com.kraven.ui.base.APILiveData
import com.kraven.ui.base.BaseViewModel
import com.kraven.ui.home.model.*
import com.kraven.ui.order.beverage.model.Beverage
import com.kraven.ui.order.food.model.HostedPageOrder
import com.kraven.utils.ConstantApp
import io.reactivex.*

import javax.inject.Inject

class CartViewModel @Inject constructor(private val userRepository: UserRepository,
                                        private val placeOrderRepository: PlaceOrderRepository,
                                        val googleRepository: GoogleRepository,
                                        private val authenticationRepository: AuthenticationRepository) : BaseViewModel() {

    //val placeOrder = APILiveData<Any>()
    val placeOrder = APILiveData<HostedPageOrder>()
    val checkPromocode = APILiveData<Promocode>()
    //val getMenuList = MutableLiveData<List<RestaurantMenu>>()
    val getMenuList = APILiveData<List<RestaurantMenu>>()
    val getBeverageLists = APILiveData<List<Beverage>>()
    val addMoneyToWallet = APILiveData<Any>()
    val distance = APILiveData<DistanceM>()
    val getRestaurantDetails = APILiveData<RestaurantDetails>()
    val getBeverageDetails = APILiveData<RestaurantDetails>()
    val getUser = APILiveData<User>()
    val getDistance = APILiveData<DistanceM>()
    val beforeVerifyFoodOrderLiveData = APILiveData<Any>()
    val beforeVerifyBeverageOrderLiveData = APILiveData<Any>()
    val deleteFacLogLiveData = APILiveData<Any>()
    val cancelHostedPageFoodLiveData = APILiveData<Any>()
    val cancelHostedPageBeverageLiveData = APILiveData<Any>()

    fun cancelHostedPageFood(id: String) {
        userRepository.cancelHostedPageFood(id).subscribe(withLiveData(cancelHostedPageFoodLiveData))
    }

    fun cancelHostedPageBeverage(id: String) {
        userRepository.cancelHostedPageBeverage(id).subscribe(withLiveData(cancelHostedPageBeverageLiveData))
    }

    fun deleteFacLog(parameters: Parameters){
        userRepository.deleteFacLog(parameters).subscribe(withLiveData(deleteFacLogLiveData))
    }

    fun beforeVerifyFoodOrder(parameters: Parameters){
        userRepository.beforeVerifyFoodOrder(parameters).subscribe(withLiveData(beforeVerifyFoodOrderLiveData))
    }

    fun beforeVerifyBeverageOrder(parameters: Parameters){
        userRepository.beforeVerifyBeverageOrder(parameters).subscribe(withLiveData(beforeVerifyBeverageOrderLiveData))
    }
    //APILiveData<DistanceM>()

    fun getUser(userId: String) { authenticationRepository.getUser(Parameters(userId = userId)).subscribe(withLiveData(getUser)) }



    /*fun getDistance(activity: FragmentActivity, pickUpLatLng: LatLng, cureentLatLng: LatLng, key: String): Float {
        var distanceGet = 0.0f
        googleRepository.getDistanceMatrixCart(hashMapOf(
                Pair(URLFactory.Method.GOOGLE_PARAMETER_ORIGINS, "${pickUpLatLng.latitude},${pickUpLatLng.longitude}"),
                Pair(URLFactory.Method.GOOGLE_PARAMETER_DESTINATIONS, "${cureentLatLng.latitude},${cureentLatLng.longitude}"),
                Pair(URLFactory.Method.GOOGLE_PARAMETER_KEY, key))
        ).map { return@map successDataWrrapper(it, "") }
                .subscribe(withLiveData(getDistance))

        getDistance.observe(activity, androidx.lifecycle.Observer {
            when (it.responseBody?.code) {
                StatusCode.CODE_SUCCESS -> {
                    //callBack((it.responseBody.data!!.rows!![0].elements!![0].distance!!.value / 1609.344).toFloat())

                    distanceGet = (it.responseBody.data!!.rows!![0].elements!![0].distance!!.value / 1609.344).toFloat()

                    Log.d("Hlink", "distanceGet$distanceGet")
                    *//* distance = (it.responseBody.data!!.rows!![0].elements!![0].distance!!.value / 1609.344).toFloat()

                     if (distance > restaurantDetails?.delivery_minimum_miles!!) {
                         val extraDistance = restaurantDetails?.delivery_minimum_miles!! - distance
                         val resultOfExtraDistance = abs(extraDistance) * restaurantDetails?.delivery_per_miles_rate!!
                         val finalResult = restaurantDetails!!.delivery_base_fare + resultOfExtraDistance
                         textViewDeliveryChargeValue.text = convertTwoDigit(finalResult)
                     } else {
                         textViewDeliveryChargeValue.text = convertTwoDigit(restaurantDetails!!.delivery_base_fare)
                     }*//*
                }

            }
        })
        return distanceGet
    }*/

    fun placeOrder(parameters: HashMap<String, Any>) {
        placeOrderRepository.placeOrder(parameters).subscribe(withLiveData(placeOrder))
    }

    /* fun placeOrder(saveCart: SaveCartNew) {
         userRepository.placeOrder(Parameters(restaurantId = saveCart.restaurantId,
                 deliveryType = saveCart.deliveryType,
                 cardToken = saveCart.cardToken,
                 tip = saveCart.tip,
                 tipPercent = saveCart.tipPercent,
                 distance = saveCart.distance,
                 addressId = saveCart.addressId,
                 total_qty = saveCart.total_qty,
                 paymentMethod = saveCart.paymentMethod,
                 discount = saveCart.discount,
                 wallet = saveCart.wallet,
                 orderType = saveCart.orderType,
                 subTotal = saveCart.subTotal, vatPercent = saveCart.vatPercent,
                 totalVat = saveCart.totalVat, total = saveCart.total,
                 orderDatetime = saveCart.orderDatetime,
                 dishes = saveCart.dishes, deliveryCharge = saveCart.deliveryCharge,beverageId = saveCart.beverageId))
                 .subscribe(withLiveData(placeOrder))
     }*/

    fun placeOrderBeverage(parameters: HashMap<String, Any>) {
        placeOrderRepository.placeOrderBeverage(parameters).subscribe(withLiveData(placeOrder))
        /* userRepository.placeOrderBeverage(Parameters(
                 deliveryType = saveCart.deliveryType,
                 cardToken = saveCart.cardToken,
                 tip = saveCart.tip,
                 tipPercent = saveCart.tipPercent,
                 items = saveCart.items,
                 distance = saveCart.distance, addressId = saveCart.addressId, total_qty = saveCart.total_qty,
                 paymentMethod = saveCart.paymentMethod, discount = saveCart.discount,
                 wallet = saveCart.wallet, orderType = saveCart.orderType,
                 subTotal = saveCart.subTotal, vatPercent = saveCart.vatPercent,
                 totalVat = saveCart.totalVat, total = saveCart.total,
                 orderDatetime = saveCart.orderDatetime,
                 deliveryCharge = saveCart.deliveryCharge,beverageId = saveCart.beverageId))
                 .subscribe(withLiveData(placeOrder))*/
    }

    fun checkPromocode(subtotal: String, promocode: String, productType: String, restaurantId: Int, orderPage: String?) {

        val parameters = Parameters()
        parameters.subTotal = subtotal
        parameters.promocode = promocode
        parameters.product_type = productType
        if (orderPage == ConstantApp.PassValue.ORDER_FOOD) {
            parameters.restaurantId = restaurantId.toString()
        } else {
            parameters.beverageId = restaurantId.toString()
        }
        userRepository.checkPromocode(parameters).subscribe(withLiveData(checkPromocode))
    }

    fun addMoneyToWallet(parameters: HashMap<String, Any>) {
        userRepository.addMoneyToWallet(parameters)
                .subscribe(withLiveData(addMoneyToWallet))
    }


    fun getRestaurantDetails(restaurantId: String, currentLatLng: LatLng, date: String, isPageVisit: String) {
        userRepository.getRestaurantDetails(Parameters(isPageVisit = isPageVisit, futureDate = date, restaurantId = restaurantId, latitude = currentLatLng.latitude.toString(), longitude = currentLatLng.longitude.toString()))
                .subscribe(withLiveData(getRestaurantDetails))
    }

    fun getBeverageDetails(restaurantId: String, currentLatLng: LatLng) {
        userRepository.getBeverageDetails(Parameters(beverageId = restaurantId, latitude = currentLatLng.latitude.toString(), longitude = currentLatLng.longitude.toString()))
                .subscribe(withLiveData(getBeverageDetails))
    }


    /*fun getMenuLists(dishes: List<DishesItem>, restaurantId: String) {
        val dishesDisposable = Observable.fromIterable(dishes)
                .flatMap {
                    userRepository.getDishList(Parameters(restaurantId = restaurantId, dishIds = it.menuId.toString())).toObservable()
                }
                .toList()
                .toObservable()
                .subscribe({
                    val dishItemList = mutableListOf<RestaurantMenu>()
                    for (response in it) {
                        response.responseBody?.data?.let { dishItem ->
                            dishItemList.addAll(dishItem)
                        }
                    }
                    getMenuList.postValue(dishItemList)
                }, {

                })

        compositeDisposable.addBeverage(dishesDisposable)

    }*/

    fun getBeverageList(dishes: List<DishesItem>, restaurantId: String) {
        val dishesDisposable = Observable.fromIterable(dishes)
                .flatMap {

                    userRepository.beverageItemsByid(Parameters(beverageId = restaurantId, itemIds = it.id.toString())).toObservable()
                }
                .toList()
                .toObservable()
                .subscribe({
                    val dishItemList = mutableListOf<Beverage>()
                    for (response in it) {
                        response.responseBody?.data?.let { dishItem ->
                            dishItemList.addAll(dishItem)
                        }
                    }

                }, {

                })

        compositeDisposable.add(dishesDisposable)

    }

    fun getBeverageListLast(beverageId: String, dishe: String) {
        userRepository.beverageItemsByid(Parameters(beverageId = beverageId, itemIds = dishe))
                .subscribe(withLiveData(getBeverageLists))
    }

    fun getMenuLists(restaurantId: String, dishId: String) {
        userRepository.getDishList(Parameters(restaurantId = restaurantId, dishIds = dishId)).subscribe(withLiveData(getMenuList))
    }

     fun calculateCharges(pickUpLatLng: LatLng, cureentLatLng: LatLng, key: String): Observable<Float> {
        return googleRepository.getDistanceMatrixCart(hashMapOf(
                Pair(URLFactory.Method.GOOGLE_PARAMETER_ORIGINS, "${cureentLatLng.latitude},${cureentLatLng.longitude}"),
                Pair(URLFactory.Method.GOOGLE_PARAMETER_DESTINATIONS, "${pickUpLatLng.latitude},${pickUpLatLng.longitude}"),
                Pair(URLFactory.Method.GOOGLE_PARAMETER_KEY, key)))
                .flatMap {

                    val miles = it.rows!![0].elements!![0].distance?.text?.split(" ")
                    var milesValue = 0F
                    when {
                        miles?.last()=="ft" -> {
                            val valueOfMiles = miles.first().toFloat() / 5280
                            milesValue = valueOfMiles
                        }
                        miles?.last()=="mi" -> {
                            val valueOfMiles = miles.first().toFloat()
                            milesValue = valueOfMiles
                        }
                        miles?.last()=="km" -> {
                            val valueOfMiles = miles.first().toFloat() / 1.609
                            milesValue = valueOfMiles.toFloat()
                        }
                    }
                    Single.just(milesValue)

                }.toObservable()

    }

}