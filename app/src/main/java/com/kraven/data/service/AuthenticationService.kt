package com.kraven.data.service

import com.kraven.data.URLFactory
import com.kraven.data.pojo.Parameters
import com.kraven.data.pojo.ResponseBody
import com.kraven.data.pojo.User
import com.kraven.ui.address.model.Address
import com.kraven.ui.address.model.Cay
import com.kraven.ui.authentication.model.AppVersion
import com.kraven.ui.order.food.model.Filters
import com.kraven.ui.review.model.Review
import com.kraven.ui.wallet.model.Transaction
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.net.URL

interface AuthenticationService {


    @POST(URLFactory.Method.GET_APP_VERSION)
    fun getAppVersion():Single<ResponseBody<AppVersion>>

    @POST(URLFactory.Method.VERIFICATION_EMAIL)
    fun verifyEmail(@Body parameters: Parameters): Single<ResponseBody<User>>

    @POST(URLFactory.Method.VERIFICATION_OTP)
    fun verifyOtp(@Body parameters: Parameters): Single<ResponseBody<User>>

    @POST(URLFactory.Method.SIGNUP)
    fun signUp(@Body parameters: Parameters): Single<ResponseBody<User>>

    @POST(URLFactory.Method.LOGIN)
    fun login(@Body parameters: Parameters): Single<ResponseBody<User>>

    @POST(URLFactory.Method.UPDATE_DEVICE_ID)
    fun updateDeviceId(@Body parameters: Parameters):Single<ResponseBody<Any>>

    @POST(URLFactory.Method.LOGOUT)
    fun logout(): Single<ResponseBody<Any>>

    @POST(URLFactory.Method.ADD_ADDRESS)
    fun addAddress(@Body parameters: Parameters): Single<ResponseBody<Any>>

    @POST(URLFactory.Method.FORGOT_PASSWORD)
    fun forgotPassword(@Body parameters: Parameters): Single<ResponseBody<Any>>

    @POST(URLFactory.Method.CHANGE_PASSWORD)
    fun changePassword(@Body parameters: Parameters): Single<ResponseBody<Any>>

    @POST(URLFactory.Method.CONTACT_US)
    fun contactUs(@Body parameters: Parameters): Single<ResponseBody<Any>>

    @POST(URLFactory.Method.EDIT_USER)
    fun editUser(@Body parameters: Parameters): Single<ResponseBody<User>>

    @POST(URLFactory.Method.GET_USER)
    fun getUser(@Body parameters: Parameters): Single<ResponseBody<User>>

    @POST(URLFactory.Method.ADDRESS_LIST)
    fun getAddressList(@Body parameters: Parameters): Single<ResponseBody<List<Address>>>

    @POST(URLFactory.Method.DELETE_ADDRESS)
    fun deleteAddress(@Body parameters: Parameters): Single<ResponseBody<Any>>

    @POST(URLFactory.Method.EDIT_ADDRESS)
    fun editAddress(@Body parameters: Parameters):Single<ResponseBody<Address>>

    @POST(URLFactory.Method.REVIEW_LIST)
    fun getReviewList(@Body parameters: Parameters): Single<ResponseBody<List<Review>>>

    @POST(URLFactory.Method.GETCUISINELIST)
    fun getCuisineList() :Single<ResponseBody<List<Filters.FilterName>>>

    @POST(URLFactory.Method.GETWALLETLIST)
    fun getWalletList(@Body parameters: Parameters):Single<ResponseBody<List<Transaction>>>

    @POST(URLFactory.Method.CAY_LIST)
    fun getCayList(@Body parameters: Parameters): Single<ResponseBody<List<Cay>>>

}