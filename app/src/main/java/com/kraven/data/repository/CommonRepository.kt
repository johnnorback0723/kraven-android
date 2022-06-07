package com.kraven.data.repository

import com.kraven.data.pojo.DataWrapper
import com.kraven.data.pojo.Parameters
import io.reactivex.Single


/**
 * Created by Android Developer on 3/5/19.
 */
interface CommonRepository {
    fun updateLocation(parameters: Parameters): Single<DataWrapper<Any>>
}