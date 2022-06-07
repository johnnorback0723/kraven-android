package com.kraven.data.datasource

import android.content.Context
import com.google.gson.Gson
import com.kraven.core.AppExecutors
import com.kraven.core.StatusCode
import com.kraven.data.pojo.DataWrapper
import com.kraven.data.pojo.ResponseBody
import com.kraven.exception.ServerException
import com.kraven.exception.UpdateException
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

open class BaseDataSource() {

    fun <T> execute(observable: Single<ResponseBody<T>>): Single<DataWrapper<T>> {
        return observable
                /*.subscribeOn(Schedulers.from(appExecutors.networkIO()))
                .observeOn(Schedulers.from(appExecutors.mainThread()))*/
                .subscribeOn(Schedulers.io())
                .map { t -> DataWrapper(t, null) }
                .onErrorReturn { t -> DataWrapper(null, t) }
                .map {
                    if (it.responseBody != null) {
                        when (it.responseBody.code) {
                            6 ->
                                return@map DataWrapper(it.responseBody, UpdateException(it.responseBody.message, it.responseBody.code))
                        }
                    }
                    return@map it
                }
    }
    fun <T> executeGoogle(observable: Single<T>): Single<T> {
        return observable
                /*.subscribeOn(Schedulers.from(appExecutors.networkIO()))
                .observeOn(Schedulers.from(appExecutors.mainThread()))*/
                .subscribeOn(Schedulers.io())
                .map { t -> (t) }
                .onErrorReturn { _ ->  null }
//                .map {
//                    if (it.responseBody != null) {
//                        when (it.responseBody.code) {
//                            0, 3, 4 -> return@map DataWrapper(it.responseBody, ServerException(it.responseBody.message, it.responseBody.code))
//                        }
//                    }
//                    return@map it
//                }
    }
    fun <T> wrapWithResponseBody(t: T) = ResponseBody(StatusCode.CODE_SUCCESS, "", t)

}