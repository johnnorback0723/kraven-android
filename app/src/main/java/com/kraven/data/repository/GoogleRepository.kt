package com.kraven.data.repository

import com.google.android.gms.maps.model.LatLng
import com.kraven.delivery.model.google.Directions
import com.kraven.ui.base.APILiveData
import com.kraven.model.google.DistanceM
import com.kraven.model.google.ReverseGeocodedAddress
import io.reactivex.Single

interface GoogleRepository {
    fun getPath(startLatLng: LatLng, endLatLong: LatLng):Single<List<Directions.Routes>>

    fun getAddress(startLatitude: String, startLongitude: String): Single<ReverseGeocodedAddress>

    //fun getDistanceMatrix(parameters: Map<String, String>, liveData: APILiveData<DistanceM>)
    //fun findLatLong(placeId: String): Single<LatLng>

    fun getDistanceMatrix(startLatitude: String, startLongitude: String, endLatitude: String, endLongitude: String): Single<DistanceM>


    fun getDistanceMatrixCart(parameters: Map<String, String>): Single<DistanceM>


}