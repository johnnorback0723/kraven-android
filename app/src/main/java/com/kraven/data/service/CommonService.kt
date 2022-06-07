package com.kraven.data.service


import com.kraven.data.URLFactory
import com.kraven.data.pojo.Parameters
import com.kraven.data.pojo.ResponseBody
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST


/**
 * Created by Android Developer on 3/5/19.
 */
interface CommonService {
    @POST(URLFactory.Method.UPDATE_LATLONG)
    fun updateDriverLatLng(@Body parameters: Parameters): Single<ResponseBody<Any>>
}