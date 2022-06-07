package com.kraven.ui.favorite.viewmodel

import com.google.android.gms.maps.model.LatLng
import com.kraven.data.pojo.Parameters
import com.kraven.data.repository.UserRepository
import com.kraven.ui.base.APILiveData
import com.kraven.ui.base.BaseViewModel
import com.kraven.ui.home.model.Restaurants
import com.kraven.utils.Formatter
import java.util.*
import javax.inject.Inject

class FavoriteViewModel @Inject constructor(private val userRepository: UserRepository) : BaseViewModel() {

    val getFavoriteList = APILiveData<List<Restaurants>>()
    val getFavoriteBeverageList = APILiveData<List<Restaurants>>()


    fun getFavoriteList(latLng: LatLng, page: String, searchText: String) {
        userRepository.getFavoriteList(Parameters(latitude = latLng.latitude.toString(),
                longitude = latLng.longitude.toString(),
                futureDate = Formatter.format(Date().toString(), Formatter.YYYY_MM_DD_T_HH_MM_SSS_Z, "yyyy-MM-dd", false).toString(),
                page = page, search = searchText))
                .subscribe(withLiveData(getFavoriteList))
    }

    fun getFavoriteBeverageList(latLng: LatLng, page: String, searchText: String) {
        userRepository.getFavoriteBeverageList(Parameters(latitude = latLng.latitude.toString(),
                futureDate = Formatter.format(Date().toString(), Formatter.YYYY_MM_DD_T_HH_MM_SSS_Z, "yyyy-MM-dd", false).toString(),
                longitude = latLng.longitude.toString(),
                page = page, search = searchText))
                .subscribe(withLiveData(getFavoriteBeverageList))
    }

}