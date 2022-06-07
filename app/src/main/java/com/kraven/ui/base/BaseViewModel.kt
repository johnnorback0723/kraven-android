package com.kraven.ui.base

import androidx.lifecycle.ViewModel
import com.kraven.core.StatusCode
import com.kraven.data.URLFactory
import com.kraven.data.pojo.DataWrapper
import com.kraven.data.pojo.ResponseBody
import io.reactivex.SingleObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver

open class BaseViewModel : ViewModel() {

    val compositeDisposable = CompositeDisposable()

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()

    }

    protected fun <T> withLiveData(liveData: APILiveData<T>): SingleObserver<DataWrapper<T>> {
        return object : SingleObserver<DataWrapper<T>> {
            override fun onError(e: Throwable) {}
            override fun onSuccess(t: DataWrapper<T>) = liveData.postValue(t)
            override fun onSubscribe(d: Disposable) {
                compositeDisposable.add(d)
            }
        }
    }

    fun <T> wrapWithResponseBody(t: T) = ResponseBody(StatusCode.CODE_SUCCESS, "", t)

    fun <T> wrapWithResponseBody(t: T, code: Int) = ResponseBody(code, "", t)

    fun <T> successDataWrrapper(t:T,message:String="suceess"):DataWrapper<T>{
        return  DataWrapper(successBoday(message = message,t = t),null)
    }

    fun <T> successBoday(t:T,message:String="suceess"):ResponseBody<T>{
        return  ResponseBody(message  =message,code = StatusCode.CODE_SUCCESS,data = t)
    }
}