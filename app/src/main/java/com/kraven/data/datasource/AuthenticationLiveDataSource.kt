package com.kraven.data.datasource


import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.kraven.application.KravenCustomer
import com.kraven.core.AppExecutors
import com.kraven.core.Session
import com.kraven.core.StatusCode
import com.kraven.data.pojo.DataWrapper
import com.kraven.data.pojo.Parameters
import com.kraven.data.pojo.User
import com.kraven.data.repository.AuthenticationRepository
import com.kraven.data.service.AuthenticationService
import com.kraven.ui.TestApi
import com.kraven.ui.address.model.Address
import com.kraven.ui.address.model.Cay
import com.kraven.ui.authentication.model.AppVersion
import com.kraven.ui.bartender.model.BartenderOrderList
import com.kraven.ui.cart.model.SelectAddress
import com.kraven.ui.home.model.MenuModel
import com.kraven.ui.home.model.Service
import com.kraven.ui.my.order.model.OrderList
import com.kraven.ui.my_offer.model.MyOffer
import com.kraven.ui.order.food.model.Filters
import com.kraven.ui.payment.model.Payment
import com.kraven.ui.portable.bar.rental.model.PortableBar
import com.kraven.ui.portable.bar.rental.model.PortableOrderList
import com.kraven.ui.restaurant.reservation.model.Restaurant
import com.kraven.ui.review.model.Review
import com.kraven.ui.wallet.model.Transaction
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationLiveDataSource @Inject constructor(private val authenticationService: AuthenticationService,
                                                       val appExecutors: AppExecutors, private val context: Context, private val gson: Gson) :
        BaseDataSource(), AuthenticationRepository {


    @Inject
    lateinit var session: Session



    override fun getAppVersion(): Single<DataWrapper<AppVersion>> {
        return  execute(authenticationService.getAppVersion())
    }

    override fun editAddress(parameters: Parameters): Single<DataWrapper<Address>> {
        return execute(authenticationService.editAddress(parameters))
    }

    override fun deleteAddress(parameters: Parameters): Single<DataWrapper<Any>> {
        return execute(authenticationService.deleteAddress(parameters))
    }

    override fun getAddressList(parameters: Parameters): Single<DataWrapper<List<Address>>> {
        return if (session.isProtoType) {
            execute(Single.just(wrapWithResponseBody(TestApi.getAddressList())))
        } else {
            execute(authenticationService.getAddressList(parameters))
        }
    }

    override fun editUser(parameters: Parameters): Single<DataWrapper<User>> {
        return execute(authenticationService.editUser(parameters)
                .doOnSuccess {
                    if (it.code == StatusCode.CODE_SUCCESS) {
                        session.user = it.data
                    }
                })
    }

    override fun getUser(parameters: Parameters): Single<DataWrapper<User>> {
        return execute(authenticationService.getUser(parameters)
                .doOnSuccess {
                    if (it.code == StatusCode.CODE_SUCCESS) {
                        session.user = it.data
                    }
                })
    }

    override fun forgotPassword(parameters: Parameters): Single<DataWrapper<Any>> {
        return execute(authenticationService.forgotPassword(parameters))
    }

    override fun contactUs(parameters: Parameters): Single<DataWrapper<Any>> {
        return execute(authenticationService.contactUs(parameters))
    }

    override fun changePassword(parameters: Parameters): Single<DataWrapper<Any>> {
        return execute(authenticationService.changePassword(parameters))
    }

    override fun addAddress(parameters: Parameters): Single<DataWrapper<Any>> {
        return execute(authenticationService.addAddress(parameters))
    }

    override fun verifyOtp(parameters: Parameters): Single<DataWrapper<User>> {
        return execute(authenticationService.verifyOtp(parameters))
    }

    override fun getReviewList(parameters: Parameters): Single<DataWrapper<List<Review>>> {
        return execute(authenticationService.getReviewList(parameters))
    }

    override fun verifyEmail(parameters: Parameters): Single<DataWrapper<User>> {
        return execute(authenticationService.verifyEmail(parameters))
    }

    override fun signUp(parameters: Parameters): Single<DataWrapper<User>> {
        parameters.osVersion = Build.VERSION.RELEASE + "(" + Build.VERSION.SDK_INT + ")"
        parameters.deviceName = Build.MANUFACTURER
        parameters.modelName = Build.MODEL
        return execute(authenticationService.signUp(parameters)
                .doOnSuccess {
                    if (it.code == StatusCode.CODE_SUCCESS) {
                        session.user = it.data
                        session.userSession = it.data!!.token
                        session.setAddressCode = ""
                    }
                })
    }

    override fun updateDeviceInfo(): Single<DataWrapper<Any>> {
        val parameters = Parameters()
        try {
            val pInfo: PackageInfo = KravenCustomer.appContext.packageManager.getPackageInfo(KravenCustomer.appContext.packageName, 0)
            parameters.osVersion = Build.VERSION.RELEASE + "(" + Build.VERSION.SDK_INT + ")"
            parameters.deviceName = Build.MANUFACTURER
            parameters.modelName = Build.MODEL
            parameters.deviceId = if (session.deviceId.isBlank()) FirebaseInstanceId.getInstance().token else session.deviceId
            parameters.deviceType = "A"
            parameters.appVersion = pInfo.versionCode.toString()
            parameters.versionName = "1.0"
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return execute(authenticationService.updateDeviceId(parameters))
    }

    override fun login(parameters: Parameters): Single<DataWrapper<User>> {
        return execute(authenticationService.login(parameters)
                .doOnSuccess {
                    session.setAddressCode = it.code.toString()

                    if (it.code == StatusCode.CODE_SUCCESS) {
                        session.user = it.data
                        session.userSession = it.data!!.token
                    } else if (it.code == StatusCode.CODE_ADD_ADDRESS) {
                        session.user = it.data
                        session.userSession = it.data!!.token
                    }
                })
    }



    override fun logout(): Single<DataWrapper<Any>> {
        return execute(authenticationService.logout())
    }

    override fun getFilterList(): Single<DataWrapper<List<Filters.FilterName>>> {
        return execute(authenticationService.getCuisineList())
    }


    override fun getOfferList(): Single<DataWrapper<List<MyOffer>>> {
        return execute(Single.just(wrapWithResponseBody(TestApi.getOfferList())))
    }

    override fun getTransactionList(parameters: Parameters): Single<DataWrapper<List<Transaction>>> {
        return execute(authenticationService.getWalletList(parameters))
    }

    override fun getBartenderOrderList(): Single<DataWrapper<List<BartenderOrderList>>> {
        return execute(Single.just(wrapWithResponseBody(TestApi.getBartenderOrderList())))
    }

    override fun getPortableBarOrderList(): Single<DataWrapper<List<PortableOrderList>>> {
        return execute(Single.just(wrapWithResponseBody(TestApi.getPortableBarOrderList())))
    }

    override fun getPortableBarList(): Single<DataWrapper<List<PortableBar>>> {
        return execute(Single.just(wrapWithResponseBody(TestApi.getPortableBarList())))
    }

    override fun getRestaurantOrderList(): Single<DataWrapper<List<Restaurant>>> {
        return execute(Single.just(wrapWithResponseBody(TestApi.getRestaurantOrderList())))
    }


    override fun getBeverageOrderList(): Single<DataWrapper<List<OrderList>>> {
        return execute(Single.just(wrapWithResponseBody(TestApi.beverageOrderList())))
    }


    override fun futureOrderList(): Single<DataWrapper<List<OrderList>>> {
        return execute(Single.just(wrapWithResponseBody(TestApi.futureOrderList())))
    }


    override fun orderList(): Single<DataWrapper<List<OrderList>>> {
        return execute(Single.just(wrapWithResponseBody(TestApi.orderList())))
    }

    override fun cardList(): Single<DataWrapper<List<Payment>>> {
        return execute(Single.just(wrapWithResponseBody(TestApi.cardList())))
    }

    override fun addressList(): Single<DataWrapper<List<SelectAddress>>> {
        return execute(Single.just(wrapWithResponseBody(TestApi.addressList())))
    }

//    override fun reviewList(): Single<DataWrapper<List<Review>>> {
//        return execute(Single.just(wrapWithResponseBody(TestApi.reviewList())))
//    }

    override fun menuList(): Single<DataWrapper<List<MenuModel>>> {
        return execute(Single.just(wrapWithResponseBody(TestApi.menuList())))
    }


    override fun serviceList(): Single<DataWrapper<Service>> {
        return execute(Single.just(wrapWithResponseBody(TestApi.service())))
    }

    override fun getCayList(parameters: Parameters): Single<DataWrapper<List<Cay>>> {
        return if (session.isProtoType) {
            execute(Single.just(wrapWithResponseBody(TestApi.getCayList())))
        } else {
            execute(authenticationService.getCayList(parameters))
        }
    }
}
