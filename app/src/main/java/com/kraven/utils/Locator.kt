package com.kraven.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Get device location using various methods
 *
 * @author emil http://stackoverflow.com/users/220710/emil
 */
@Singleton
class Locator @Inject constructor(private val context: Context) : LocationListener {
    enum class Method {
        NETWORK, GPS, NETWORK_THEN_GPS
    }


    private val locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var method: Method? = null
    private var callback: Listener? = null

    fun getLocation(method: Method?, callback: Listener?) {
        this.method = method
        this.callback = callback
        when (this.method) {
            Method.NETWORK, Method.NETWORK_THEN_GPS -> {
                @SuppressLint("MissingPermission") val networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (networkLocation != null) {
                    Log.d(LOG_TAG, "Last known location found for network provider : $networkLocation")
                    this.callback!!.onLocationFound(LatLng(networkLocation.latitude,networkLocation.longitude))
                } else {
                    Log.d(LOG_TAG, "Request updates from network provider.")
                    requestUpdates(LocationManager.NETWORK_PROVIDER)
                }
            }
            Method.GPS -> {
                @SuppressLint("MissingPermission") val gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (gpsLocation != null) {
                    Log.d(LOG_TAG, "Last known location found for GPS provider : $gpsLocation")
                    this.callback!!.onLocationFound(LatLng(gpsLocation.latitude,gpsLocation.longitude))
                } else {
                    Log.d(LOG_TAG, "Request updates from GPS provider.")
                    requestUpdates(LocationManager.GPS_PROVIDER)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestUpdates(provider: String) {
        if (locationManager.isProviderEnabled(provider)) {
            if (provider.contentEquals(LocationManager.NETWORK_PROVIDER)
                    && Connectivity.isConnected(context)) {
                Log.d(LOG_TAG, "Network connected, start listening : $provider")
                locationManager.requestLocationUpdates(provider, TIME_INTERVAL.toLong(), DISTANCE_INTERVAL.toFloat(), this)
            } else if (provider.contentEquals(LocationManager.GPS_PROVIDER)
                    && Connectivity.isConnectedMobile(context)) {
                Log.d(LOG_TAG, "Mobile network connected, start listening : $provider")
                locationManager.requestLocationUpdates(provider, TIME_INTERVAL.toLong(), DISTANCE_INTERVAL.toFloat(), this)
            } else {
                Log.d(LOG_TAG, "Proper network not connected for provider : $provider")
                onProviderDisabled(provider)
            }
        } else {
            onProviderDisabled(provider)
        }
    }

    fun cancel() {
        Log.d(LOG_TAG, "Locating canceled.")
        locationManager.removeUpdates(this)
    }

    override fun onLocationChanged(location: Location) {
        Log.d(LOG_TAG, "Location found : " + location.latitude + ", " + location.longitude + if (location.hasAccuracy()) " : +- " + location.accuracy + " meters" else "")
        locationManager.removeUpdates(this)
        callback!!.onLocationFound(LatLng(location.latitude,location.longitude))
    }

    override fun onProviderDisabled(provider: String) {
        Log.d(LOG_TAG, "Provider disabled : $provider")
        if (method == Method.NETWORK_THEN_GPS
                && provider.contentEquals(LocationManager.NETWORK_PROVIDER)) {
            // Network provider disabled, try GPS
            Log.d(LOG_TAG, "Requesst updates from GPS provider, network provider disabled.")
            requestUpdates(LocationManager.GPS_PROVIDER)
        } else {
            locationManager.removeUpdates(this)
            callback!!.onLocationNotFound()
        }
    }

    override fun onProviderEnabled(provider: String) {
        Log.d(LOG_TAG, "Provider enabled : $provider")
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        Log.d(LOG_TAG, "Provided status changed : $provider : status : $status")
    }

    interface Listener {
        fun onLocationFound(latLng: LatLng)
        fun onLocationNotFound()
    }

    companion object {
        private const val LOG_TAG = "LZX"
        private const val TIME_INTERVAL = 10000 // minimum time between updates in milliseconds
        private const val DISTANCE_INTERVAL = 10 // minimum distance between updates in meters
    }

}