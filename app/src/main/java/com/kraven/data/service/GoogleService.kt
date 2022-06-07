package com.kraven.data.service

import com.kraven.data.URLFactory

import com.kraven.data.URLFactory.Method.GOOGLE_ADDRESS_REVERSE_GEOCODE
import com.kraven.data.URLFactory.Method.GOOGLE_DISTANCE_MATRIX
import com.kraven.data.pojo.DataWrapper
import com.kraven.data.pojo.Parameters
import com.kraven.delivery.model.google.Directions
import com.kraven.model.google.DistanceM

import com.kraven.model.google.ReverseGeocodedAddress
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap

/**
 * REST API access points
 */
interface GoogleService {
    /*
     * Google Address APIs
     */
   /* @GET(GOOGLE_ADDRESS_GEOCODING)
    fun getAddresses(@QueryMap parameters: Map<String, String>): Deferred<Response<PlaceAddress>>

    @GET(GOOGLE_ADDRESS_PLACE_DETAILS)
    fun getPlaceDetails(@QueryMap parameters: Map<String, String>): Deferred<Response<PlaceAddressDetails>>*/

    @GET(GOOGLE_ADDRESS_REVERSE_GEOCODE)
    fun getAddress(@QueryMap parameters: Map<String, String>): Single<ReverseGeocodedAddress>

    @POST(GOOGLE_DISTANCE_MATRIX)
    fun getDistances(@QueryMap parameters: Map<String, String>): Single<DistanceM>


    @POST(GOOGLE_DISTANCE_MATRIX)
    fun getDistances(@Body parameters: Parameters): Single<DataWrapper<DistanceM>>

    @GET(URLFactory.Method.GOOGLE_DIRECTIONS)
    fun getDirections(@QueryMap parameters: Map<String, String>): Single<Directions>


   /* @POST(GOOGLE_DISTANCE_MATRIX)
    fun getDistance(@QueryMap parameters: Map<String, String>): Single<DistanceM>


    @POST(GOOGLE_DISTANCE_MATRIX)
    fun getDistances(@Body parameters: Parameters): Single<DataWrapper<DistanceM>>*/

}