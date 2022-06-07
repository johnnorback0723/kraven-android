package com.kraven.ui.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.kraven.data.pojo.DataWrapper
import com.kraven.data.pojo.ResponseBody
import com.kraven.exception.NoDataFoundException


class APILiveData<T> : MutableLiveData<DataWrapper<T>>() {


    /**
     *  @param owner : Life Cycle Owner
     *  @param onChange : live data
     *  @param onError : Server and KravenCustomer error -> return true to handle error by base else return false to handle error by your self
     *
     */
    public fun observe(owner: BaseFragment, onChange: (ResponseBody<T>) -> Unit, onError: (Throwable) -> Boolean = { true }) {
        super.observe(owner, Observer<DataWrapper<T>> {
            if (it?.throwable != null) {
                if (onError(it.throwable)) owner.onError(it.throwable)
            } else if (it?.responseBody != null) {
                /* var toString = Gson().toJson(it.responseBody.data)
                 owner.onError(JSONException(toString))
 */
               /* if(it.responseBody.data!=null){
                    owner.onError(NoDataFoundException("In response,result.value not found"))
                }else{*/
                    onChange(it.responseBody)
               // }

            }
        })
    }

    public fun observe(owner: BaseActivity, onChange: (ResponseBody<T>) -> Unit, onError: (Throwable) -> Boolean = { true }) {
        super.observe(owner, Observer<DataWrapper<T>> {
            if (it?.throwable != null) {
                if (onError(it.throwable)) owner.onError(it.throwable)
            } else if (it?.responseBody != null) {
               /* if(it.responseBody.data!=null){
                    owner.onError(NoDataFoundException("In response,result.value not found"))
                }else{*/
                    onChange(it.responseBody)
               // }

            }
        })
    }


}