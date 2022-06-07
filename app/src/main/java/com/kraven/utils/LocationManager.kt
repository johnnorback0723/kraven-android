package com.kraven.utils

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResult
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.maps.model.LatLng
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
open class LocationManager @Inject
constructor() : GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<LocationSettingsResult> {

    private var mGoogleApiClient: GoogleApiClient? = null
    private var activity: AppCompatActivity? = null
    private var mLocationRequest: LocationRequest? = null
    private var isFreshLocation = false

    private var locationListener: LocationListener? = null

    var subject: Subject<Location> = PublishSubject.create()

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mLocationCallback: LocationCallback? = null
    private var isOneTime = true
    private var mLastLocation: Location? = null

    val locationUpdateLiveData: MutableLiveData<LatLng> by lazy {
        MutableLiveData<LatLng>()
    }

    /* public static LocationManager newInstance(AppCompatActivity activity) {
        this.activity = activity;
        LocationManager locationManager = new LocationManager();
        locationManager.setActivity(activity);
        return locationManager;
    }
*/

    fun setActivity(activity: AppCompatActivity) {
        this.activity = activity
    }

    fun triggerLocation(locationListener: LocationListener) {
        isOneTime = true
        this.locationListener = locationListener
        init()
    }

    fun triggerLocation(isFreshLocation: Boolean, locationListener: LocationListener) {
        isOneTime = true
        this.isFreshLocation = isFreshLocation
        this.locationListener = locationListener
        init()
    }

    fun getLocationUpdate(locationListener: LocationListener) {
        isOneTime = false
        isFreshLocation = true
        this.locationListener = locationListener
        init()
    }

    fun relese() {
        this.locationListener = null
        this.mFusedLocationClient = null
        this.mLocationCallback = null
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun requestPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            activity!!.requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CHECK_PERMISSION)
        } else if (ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            activity!!.requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_CHECK_PERMISSION)
        } else if (ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            activity!!.requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CHECK_PERMISSION)
        } else {
            return true
        }
        return false
    }

    private fun init() {
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                for (location in locationResult!!.locations) {
                    // Update UI with location data
                    // ...
                    onLocationChanged(location)
                }
            }
        }
        if (checkPlayServices()) {

            connectToClient()

        } else {

            if (locationListener != null)
                locationListener!!.onFail(LocationListener.Status.NO_PLAY_SERVICE)
        }
    }

    private fun connectToClient() {
        buildGoogleApiClient()
        if (mGoogleApiClient != null) {
            mGoogleApiClient!!.connect()
        }
    }

    @Nullable
    fun getLastLocation(): Location? {
        return mLastLocation
    }

    /**
     * Creating google api client object
     */
    @Synchronized
    fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(activity!!)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApiIfAvailable<Api.ApiOptions.NoOptions>(LocationServices.API).build()
    }

    /**
     * Creating location request object
     */
    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = UPDATE_INTERVAL.toLong()
        mLocationRequest!!.fastestInterval = FATEST_INTERVAL.toLong()
        mLocationRequest!!.priority = if (isFreshLocation) LocationRequest.PRIORITY_HIGH_ACCURACY else LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        mLocationRequest!!.smallestDisplacement = DISPLACEMENT.toFloat()

    }


    /**
     * Stopping location updates
     */
    fun stopLocationUpdates() {
        if (mFusedLocationClient != null)
            mFusedLocationClient!!.removeLocationUpdates(mLocationCallback!!)
    }


    /**
     * Method to display the location on UI
     */

    fun getLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        if (isFreshLocation) {
            checkLocationEnableAndStartUpdate(true)
        } else {
            if (!checkPermission()) {
                mFusedLocationClient!!.lastLocation.addOnSuccessListener(activity!!) { location ->
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
                        // Logic to handle location object
                        val latitude = location.latitude
                        val longitude = location.longitude

                        if (locationListener != null) {
                            locationUpdateLiveData.postValue(LatLng(latitude, longitude))
                        }
                        locationListener!!.onLocationAvailable(LatLng(latitude, longitude))
                        if (isOneTime)
                            stop()
                    } else {
                        if (mLocationRequest == null) {
                            mLocationRequest = LocationRequest()
                            mLocationRequest!!.interval = UPDATE_INTERVAL.toLong()
                            mLocationRequest!!.fastestInterval = FATEST_INTERVAL.toLong()
                            mLocationRequest!!.priority = if (isFreshLocation) LocationRequest.PRIORITY_HIGH_ACCURACY else LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
                            mLocationRequest!!.smallestDisplacement = DISPLACEMENT.toFloat()
                            checkLocationEnableAndStartUpdate(true)
                        } else {
                            mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                            checkLocationEnableAndStartUpdate(true)
                        }
                    }
                }
            }
        }
    }

    private fun checkPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
    }


    /**
     * Method to verify google play services on the device
     */
    private fun checkPlayServices(): Boolean {

        val googleAPI = GoogleApiAvailability.getInstance()
        val result = googleAPI.isGooglePlayServicesAvailable(activity)
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(activity, result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show()
            }

            return false
        }
        return true
    }

    fun checkLocationEnableAndStartUpdate(startUpdate: Boolean) {
        val builder = LocationSettingsRequest.Builder()
        builder.setAlwaysShow(true)
        builder.addLocationRequest(mLocationRequest!!)

        val client = LocationServices.getSettingsClient(activity!!)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener(activity!!) {
            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...
            if (startUpdate) {
                if (!checkPermission() && mFusedLocationClient != null)
                    if (mLocationRequest == null) {
                        mLocationRequest = LocationRequest()
                        mLocationRequest!!.interval = UPDATE_INTERVAL.toLong()
                        mLocationRequest!!.fastestInterval = FATEST_INTERVAL.toLong()
                        mLocationRequest!!.priority = if (isFreshLocation) LocationRequest.PRIORITY_HIGH_ACCURACY else LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
                        mLocationRequest!!.smallestDisplacement = DISPLACEMENT.toFloat()
                        mFusedLocationClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback!!, Looper.myLooper())
                    } else {
                        mFusedLocationClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback!!, Looper.myLooper())
                    }

            } else
                getLocation()
        }

        task.addOnFailureListener(activity!!) { e ->
            when ((e as ApiException).statusCode) {
                CommonStatusCodes.RESOLUTION_REQUIRED ->
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        val resolvable = e as ResolvableApiException
                        resolvable.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }

                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                }
            }// Location settings are not satisfied. However, we have no way
            // to fix the settings so we won't show the dialog.
        }


    }

    override fun onConnected(bundle: Bundle?) {
        createLocationRequest()
        checkLocationEnableAndStartUpdate(false)
    }

    override fun onConnectionSuspended(i: Int) {

    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

    }


    fun onLocationChanged(location: Location) {
        if (isOneTime)
            stop()

        mLocationRequest!!.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        if (locationListener != null)
            mLastLocation = location
        locationUpdateLiveData.postValue(LatLng(location.latitude, location.longitude))
        locationListener!!.onLocationAvailable(LatLng(location.latitude, location.longitude))
        subject.onNext(location)

    }

    fun stop() {
        stopLocationUpdates()
        if (mGoogleApiClient != null)
            mGoogleApiClient!!.disconnect()
    }


    override fun onResult(locationSettingsResult: LocationSettingsResult) {

        val status = locationSettingsResult.status
        if (status.hasResolution()) {
            Toast.makeText(activity, "Please Enable the Location service ", Toast.LENGTH_SHORT).show()
            try {
                status.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS)
            } catch (e: IntentSender.SendIntentException) {
                e.printStackTrace()
            }
        } else {
            getLocation()
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == REQUEST_CHECK_SETTINGS) {

            if (resultCode == Activity.RESULT_OK) {
                getLocation()
            } else if (resultCode == Activity.RESULT_CANCELED) {
                openGpsEnableSetting()
            } else {
                if (locationListener != null)
                    locationListener!!.onFail(LocationListener.Status.DENIED_LOCATION_SETTING)
            }
        }

    }

    private fun openGpsEnableSetting() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        activity!!.startActivityForResult(intent, 10)
    }


    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CHECK_PERMISSION) {

            // We have requested multiple permissions for contacts, so all of them need to be
            // checked.
            if (PermissionUtil.verifyPermissions(grantResults)) {
                // All required permissions have been granted, display contacts fragment.
                connectToClient()
            } else {
                if (locationListener != null)
                    locationListener!!.onFail(LocationListener.Status.PERMISSION_DENIED)


            }

        }
    }

    interface LocationListener {

        fun onLocationAvailable(latLng: LatLng)

        fun onFail(status: Status)

        enum class Status {
            PERMISSION_DENIED, NO_PLAY_SERVICE, DENIED_LOCATION_SETTING
        }
    }

    companion object {


        private val PLAY_SERVICES_RESOLUTION_REQUEST = 1000
        val REQUEST_CHECK_SETTINGS = 111
        private val REQUEST_CHECK_PERMISSION = 222

        // Location updates intervals in sec
        private val UPDATE_INTERVAL = 10000 // 10 sec
        private val FATEST_INTERVAL = 5000 // 1 sec
        private val DISPLACEMENT = 10 // 10 meters
    }
}

