package com.kraven.data.repository


import com.kraven.data.pojo.DataWrapper
import com.kraven.data.pojo.Parameters
import com.kraven.data.pojo.User
import com.kraven.ui.address.model.Address
import com.kraven.ui.address.model.Cay
import com.kraven.ui.authentication.model.AppVersion
import com.kraven.ui.bartender.model.BartenderOrderList
import com.kraven.ui.cart.model.SelectAddress
import com.kraven.ui.home.model.MenuModel
import com.kraven.ui.home.model.Service
import com.kraven.ui.my_offer.model.MyOffer
import com.kraven.ui.my.order.model.OrderList
import com.kraven.ui.notification.model.Notifications
import com.kraven.ui.order.beverage.model.Beverage
import com.kraven.ui.order.food.model.Filters
import com.kraven.ui.payment.model.Payment
import com.kraven.ui.portable.bar.rental.model.PortableBar
import com.kraven.ui.portable.bar.rental.model.PortableOrderList
import com.kraven.ui.restaurant.reservation.model.Restaurant
import com.kraven.ui.review.model.Review
import com.kraven.ui.wallet.model.Transaction
import io.reactivex.Single

interface AuthenticationRepository {


    fun getAppVersion():Single<DataWrapper<AppVersion>>

    fun verifyEmail(parameters: Parameters): Single<DataWrapper<User>>

    fun verifyOtp(parameters: Parameters): Single<DataWrapper<User>>

    fun signUp(parameters: Parameters): Single<DataWrapper<User>>

    fun login(parameters: Parameters): Single<DataWrapper<User>>

    fun updateDeviceInfo():Single<DataWrapper<Any>>

    fun logout(): Single<DataWrapper<Any>>

    fun addAddress(parameters: Parameters): Single<DataWrapper<Any>>

    fun forgotPassword(parameters: Parameters): Single<DataWrapper<Any>>

    fun changePassword(parameters: Parameters): Single<DataWrapper<Any>>

    fun contactUs(parameters: Parameters): Single<DataWrapper<Any>>

    fun editUser(parameters: Parameters): Single<DataWrapper<User>>

    fun getUser(parameters: Parameters): Single<DataWrapper<User>>

    fun getAddressList(parameters: Parameters): Single<DataWrapper<List<Address>>>

    fun deleteAddress(parameters: Parameters): Single<DataWrapper<Any>>

    fun editAddress(parameters: Parameters):Single<DataWrapper<Address>>

    fun serviceList(): Single<DataWrapper<Service>>

    fun menuList(): Single<DataWrapper<List<MenuModel>>>

//    fun reviewList(): Single<DataWrapper<List<Review>>>
    fun getReviewList(parameters: Parameters): Single<DataWrapper<List<Review>>>

    fun addressList(): Single<DataWrapper<List<SelectAddress>>>

    fun cardList(): Single<DataWrapper<List<Payment>>>

    fun orderList(): Single<DataWrapper<List<OrderList>>>

    fun getBeverageOrderList(): Single<DataWrapper<List<OrderList>>>

    fun futureOrderList(): Single<DataWrapper<List<OrderList>>>



    fun getRestaurantOrderList(): Single<DataWrapper<List<Restaurant>>>

    fun getPortableBarList(): Single<DataWrapper<List<PortableBar>>>

    fun getPortableBarOrderList(): Single<DataWrapper<List<PortableOrderList>>>

    fun getBartenderOrderList(): Single<DataWrapper<List<BartenderOrderList>>>

    fun getTransactionList(parameters: Parameters): Single<DataWrapper<List<Transaction>>>

    fun getOfferList(): Single<DataWrapper<List<MyOffer>>>

    fun getFilterList(): Single<DataWrapper<List<Filters.FilterName>>>

    fun getCayList(parameters: Parameters): Single<DataWrapper<List<Cay>>>
}