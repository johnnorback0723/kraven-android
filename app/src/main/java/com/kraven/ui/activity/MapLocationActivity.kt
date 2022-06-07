package com.kraven.ui.activity

import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.kraven.R
import com.kraven.core.AppExecutors
import com.kraven.core.Common
import com.kraven.data.repository.GoogleRepository
import com.kraven.di.component.ActivityComponent
import com.kraven.extensions.requirePermission
import com.kraven.ui.base.BaseActivity
import com.kraven.ui.my.order.model.BeverageHistoryDetails
import com.kraven.ui.viewmodel.MapLocationViewModel
import kotlinx.android.synthetic.main.activity_map.*
import java.util.*
import javax.inject.Inject


class MapLocationActivity : BaseActivity(), OnMapReadyCallback {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var mMap: GoogleMap? = null
    private val PLACE_AUTOCOMPLETE_REQUEST_CODE = 2
    lateinit var address: String
    private var reason = -1
    var isShow: Boolean = false
    var isZoom: Boolean = false
    var zoom = 11f
    var shouldtakeAddress = true
    private var addressLatlng: LatLng? = null

    private lateinit var viewModel: MapLocationViewModel

    @Inject
    lateinit var googleRepository: GoogleRepository
    var appExecutors = AppExecutors()

    override fun findFragmentPlaceHolder() = 0
    override fun findContentView() = R.layout.activity_map

    override fun inject(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)[MapLocationViewModel::class.java]
        //locationManager.setActivity(this)
        textViewMainTitle.text = ""

        setSupportActionBar(appToolbarMap)

        appToolbarMap.setNavigationOnClickListener {
            goBack()
        }
        if (map != null) {
            initMap()
        }
        onSearchPlacesClicked()
    }

    private fun initMap() {

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
                ?: SupportMapFragment.newInstance()
        mapFragment?.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap?) {
        if (mMap != null) mMap?.clear()
        mMap = googleMap
        mMap?.uiSettings?.isTiltGesturesEnabled = false
        mMap?.uiSettings?.isMapToolbarEnabled = false

        mMap?.setOnCameraMoveStartedListener {
            this@MapLocationActivity.reason = it
        }

        mMap?.setOnCameraIdleListener {
            if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                val center = mMap?.cameraPosition?.target
                if (shouldtakeAddress) {
                    addressLatlng = center
                    setPlace(center!!.latitude, center.longitude)
                } else {
                    shouldtakeAddress = true
                }
            }
            reason = -1
        }

       /* locationManager.triggerLocation(object : LocationManager.LocationListener {
            override fun onLocationAvailable(latLng: LatLng) {
                addressLatlng = latLng
                setPlace(latLng.latitude, latLng.longitude)
            }

            override fun onFail(status: LocationManager.LocationListener.Status) {

            }
        })*/

        requirePermission(this,"This app need permissions to use this feature.You can grant them in app settings.") {
            val latitude = intent.getStringExtra("latitude")
            val longitude = intent.getStringExtra("longitude")
            val strzoom = intent.getStringExtra("zoom")
            Log.d("JSR", "MapLocation - 1 - {${latitude}}, {${longitude}}, {${zoom}}")
            isZoom = true
            zoom = strzoom!!.toFloat()
            if (latitude != "0" && longitude != "0") {
                latitude?.toDouble()?.let { longitude?.toDouble()?.let { it1 -> addressLatlng = LatLng(it, it1) } }
                addressLatlng?.let { setPlace(it.latitude, it.longitude) }
            } else {
                locationManager.checkLocationEnableAndStartUpdate(true)
                locationManager.locationUpdateLiveData.observe(this, {
                    addressLatlng = it
                    setPlace(it.latitude, it.longitude)
                    locationManager.locationUpdateLiveData.removeObservers(this)
                })
            }
        }
    }

    fun setPlace(lat: Double, lng: Double) {
        getLocation(lat, lng)
    }

    private fun getLocation(latitude: Double, longitude: Double) {
        if (!latitude.isNaN() && !longitude.isNaN()) {
            val latLng = LatLng(latitude, longitude)
            val markerOptions = MarkerOptions()
            markerOptions.position(latLng)
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin))
            mMap?.clear()
            //mMap?.setMapStyle(MapStyleOptions.loadRawResourceStyle(this@MapLocationActivity, R.raw.style_json))
            if (!isZoom) {
                mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
                isZoom = true
            } else {
                mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))

            }
            //map.onResume()

           /* googleRepository.getAddress(latitude.toString(), longitude.toString()).subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.from(appExecutors.mainThread())).subscribe(object : SingleObserver<ReverseGeocodedAddress> {
                        override fun onSuccess(it: ReverseGeocodedAddress) {
                            if (it.results != null && it.results!!.isNotEmpty()) {

                                textViewLocation.text = it.results!![0].formattedAddress
                                *//*selectedAddress = it.results!![0].formattedAddress
                                it.results!!.forEach { t: Results? ->

                                    editTextSearch.text = selectedAddress
                                    t!!.addressComponents!!.forEach { t: AddressComponents? ->
                                        if (t!!.types!!.contains(Constant.AreaType.SUB_LOCALITY) || t.types!!.contains(Constant.AreaType.LOCALITY)) {
                                            city = t.longName
                                        }
                                        if (t.types!!.contains(Constant.AreaType.COUNTRY)) {
                                            country = t.longName
                                        }
                                    }
                                }*//*
                            }
                        }

                        override fun onSubscribe(d: Disposable) {

                        }

                        override fun onError(e: Throwable) {

                        }

                    })
*/
            editTextSearch.setText(getCompleteAddressString(latitude, longitude))
            /* marker.setOnClickListener {
                 isShow = !isShow
                 if (isShow) {
                     textViewLocation.visibility = View.VISIBLE
                 } else {
                     textViewLocation.visibility = View.GONE

                 }
             }
             textViewLocation.text = getCompleteAddressString(latitude, longitude)*/


        }
    }

    private fun getCompleteAddressString(LATITUDE: Double, LONGITUDE: Double): String {
        var strAdd = ""
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1)
            if (addresses != null) {
                val returnedAddress = addresses[0]
                val strReturnedAddress = StringBuilder("")

                for (i in 0..returnedAddress.maxAddressLineIndex) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n")
                }
                strAdd = strReturnedAddress.toString()
            } else {
                showErrorMessage(getString(R.string.NoAddress))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showErrorMessage(getString(R.string.NoAddress))
        }

        return strAdd
    }

    private fun autoCompleteMethod() {

        val fields = listOf(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG)
        val intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields
        )
                .build(this)
        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)
    }

    private fun onSearchPlacesClicked() {
        editTextSearch.setOnClickListener({ autoCompleteMethod() })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val refreshView = layoutInflater.inflate(R.layout.raw_toolbar_image, null)
        val imageViewMessage = refreshView.findViewById<ImageView>(R.id.imageView)
        val outValue = TypedValue()
        this.theme.resolveAttribute(R.attr.selectableItemBackgroundBorderless, outValue, true)
        imageViewMessage.setBackgroundResource(outValue.resourceId)
        imageViewMessage.setImageResource(R.drawable.ic_map_done)
        menu?.add(0, 0, 0, "ddd")?.setActionView(imageViewMessage)?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        imageViewMessage.setOnClickListener {
            val intent = Intent()
            if (editTextSearch.text.isNotEmpty()) {
                intent.putExtra(Common.ADDRESS_LAT_LONG, addressLatlng)
                intent.putExtra(Common.LOCATION, editTextSearch.text.toString())
            } else {
                if (locationManager.getLastLocation() == null) {
                    intent.putExtra(Common.LOCATION, "")
                    intent.putExtra(Common.ERROR_TYPE, getString(R.string.location_error))
                } else {
                    intent.putExtra(Common.LOCATION, "")
                    intent.putExtra(Common.ERROR_TYPE, getString(R.string.location_error))
                }
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        return true
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            isZoom = false
            if (resultCode == Activity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                if (place.address != null) {
                    addressLatlng = place.latLng
                    setPlace(place.latLng?.latitude!!, place.latLng?.longitude!!)
                }

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = Autocomplete.getStatusFromIntent(data!!)
            } else if (resultCode == Activity.RESULT_CANCELED) {

            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


}