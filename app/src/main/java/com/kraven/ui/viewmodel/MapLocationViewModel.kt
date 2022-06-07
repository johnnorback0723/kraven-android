package com.kraven.ui.viewmodel


import com.google.android.gms.maps.model.LatLng
import com.kraven.core.AppExecutors
import com.kraven.core.StatusCode
import com.kraven.data.pojo.DataWrapper
import com.kraven.data.pojo.ResponseBody
import com.kraven.data.repository.UserRepository
import com.kraven.ui.base.APILiveData
import com.kraven.ui.base.BaseViewModel

import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MapLocationViewModel @Inject constructor(private  val userRepository: UserRepository, private val appExecutors: AppExecutors)
    : BaseViewModel(){
    lateinit var locationData: APILiveData<MutableList<String>>
    fun geocode(key: String,latLng: LatLng){
        locationData= APILiveData<MutableList<String>>()
        userRepository.geocode(key,latLng)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(appExecutors.mainThread()))
                .map {
                    if(it.size>0)
                    return@map DataWrapper(ResponseBody(code = StatusCode.CODE_SUCCESS,message = "",data =  it) , null)
                    else return@map DataWrapper(ResponseBody(code = StatusCode.CODE_NO_DATA,message = "",data =  it) , null)
                }
                .subscribe(withLiveData(locationData))
    }
}