package com.kraven.data.datasource


import android.content.Context
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.kraven.R
import com.kraven.core.AES
import com.kraven.data.URLFactory.Method.GOOGLE_PARAMETER_DESTINATION
import com.kraven.data.URLFactory.Method.GOOGLE_PARAMETER_DESTINATIONS
import com.kraven.data.URLFactory.Method.GOOGLE_PARAMETER_KEY
import com.kraven.data.URLFactory.Method.GOOGLE_PARAMETER_LATLNG
import com.kraven.data.URLFactory.Method.GOOGLE_PARAMETER_ORIGIN
import com.kraven.data.URLFactory.Method.GOOGLE_PARAMETER_ORIGINS
import com.kraven.data.repository.GoogleRepository
import com.kraven.data.service.GoogleService
import com.kraven.delivery.model.google.Directions
import com.kraven.model.google.DistanceM
import com.kraven.model.google.ReverseGeocodedAddress
import io.reactivex.Single
import io.reactivex.functions.Function
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class GoogleDataSource @Inject
internal constructor(private val aes: AES, private val service: GoogleService, context: Context, gson: Gson) :
        BaseDataSource(), GoogleRepository {

    override fun getDistanceMatrixCart(parameters: Map<String, String>): Single<DistanceM> {
        return executeGoogle(service.getDistances(parameters))
    }


    private val googleKey: String = aes.encrypt(context.getString(R.string.google_maps_broswer_key)).toString()


    override fun getPath(startLatLng: LatLng, endLatLong: LatLng): Single<List<Directions.Routes>> {
        val parameter = HashMap<String, String>()
        parameter[GOOGLE_PARAMETER_KEY] = aes.decrypt(googleKey)!!
        parameter[GOOGLE_PARAMETER_ORIGIN] = "${startLatLng.latitude},${startLatLng.longitude}"
        parameter[GOOGLE_PARAMETER_DESTINATION] = "${endLatLong.latitude},${endLatLong.longitude}"

        return executeGoogle(service.getDirections(parameter)
                .map { t ->
                    if (t.status.equals("OK"))
                        return@map t.routes!!
                    else {
                        throw Exception(t.errorMessage)
                    }
                })
    }

    override fun getAddress(startLatitude: String, startLongitude: String): Single<ReverseGeocodedAddress> {
        val parameters = HashMap<String, String>()
        parameters[GOOGLE_PARAMETER_KEY] = aes.decrypt(googleKey)!!
        parameters[GOOGLE_PARAMETER_LATLNG] = "$startLatitude,$startLongitude"
        return executeGoogle(service.getAddress(parameters))
    }

    override fun getDistanceMatrix(startLatitude: String, startLongitude: String, endLatitude: String, endLongitude: String): Single<DistanceM> {
        val parameters = HashMap<String, String>()
        parameters[GOOGLE_PARAMETER_KEY] = aes.decrypt(googleKey)!!
        parameters[GOOGLE_PARAMETER_ORIGINS] = "$startLatitude,$startLongitude"
        parameters[GOOGLE_PARAMETER_DESTINATIONS] = "$endLatitude,$endLongitude"
        return executeGoogle(service.getDistances(parameters)).map(object : Function<DistanceM, DistanceM> {
            override fun apply(t: DistanceM): DistanceM {
                when {
                    t.status.equals("OK") -> {
                        return t
                    }
                    t.error_message != null -> {
                        throw Exception(t.error_message)
                    }
                    else -> {
                        throw Exception("Distance not found!")
                    }
                }
                return t
            }


        })

    }

}
