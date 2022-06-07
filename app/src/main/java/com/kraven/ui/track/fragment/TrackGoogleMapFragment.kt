package com.kraven.ui.track.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil
import com.kraven.R
import com.kraven.core.StatusCode
import com.kraven.delivery.model.google.Directions
import com.kraven.di.component.FragmentComponent
import com.kraven.extensions.convertFtToMiles
import com.kraven.extensions.convertKmsToMiles
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.menu.LargeImageActivity
import com.kraven.ui.track.model.OrderDetail
import com.kraven.ui.track.viewmodel.TrackViewModel
import com.kraven.utils.ConstantApp
import com.kraven.utils.LocationManager
import com.kraven.utils.RootUtils
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.track_google_map_fragment.*
import kotlinx.android.synthetic.main.track_order_fragment.*
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer


class TrackGoogleMapFragment : BaseFragment(), OnMapReadyCallback, View.OnClickListener {


    private var mMap: GoogleMap? = null
    private lateinit var viewModel: TrackViewModel

    @Inject
    lateinit var locationManager: LocationManager
    private var schedule: Timer? = null
    private var carMarker: Marker? = null
    private var markerOptionsNew: MarkerOptions? = null
    private var orderDetails: OrderDetail? = null

    private var polylineSecond: Polyline? = null
    private var polylineOptions :PolylineOptions?=null
    private var lastHead = 0.0f

    private val orderId: String? by lazy {
        val args = arguments ?: throw IllegalStateException("Missing arguments!")
        args.getString(ConstantApp.PassValue.ORDER_ID)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)[TrackViewModel::class.java]
        registerObserver()
    }

    private fun registerObserver() {
        viewModel.getOrderDetails.observe(this, {
            showLoader(false)
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    orderDetails = it.data!!
                    if (mMap == null) {
                        initMap()
                    } else if (carMarker != null) {
                        if (carMarker!!.position.latitude != orderDetails!!.driverDetails.latitude!!.toDouble() && carMarker!!.position.longitude != orderDetails!!.driverDetails.longitude!!.toDouble()) {
                            animateMarkerNew(LatLng(orderDetails!!.driverDetails.latitude!!.toDouble(), orderDetails!!.driverDetails.longitude!!.toDouble()), carMarker!!)
//                            getDistan(carMarker!!.position, LatLng(orderDetails!!.deliveryLatitude.toDouble(), orderDetails!!.deliveryLongitude.toDouble()))
                        }

                    }
                }
                else -> {
                    showMessage(it.message)
                }
            }
        })

        viewModel.directionLiveData.observe(this, androidx.lifecycle.Observer { it ->
            showLoader(false)
            if (it == null) {
                showMessage("Route Not Found")
            } else {
                returnPolylineOption(it) {
                    polylineSecond?.remove()
                    polylineSecond = mMap!!.addPolyline(it)
                    mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(carMarker!!.position.latitude, carMarker!!.position.longitude), 16F))
                }
            }
        })
    }

    private fun returnPolylineOption(routes: List<Directions.Routes>, callBack: (PolylineOptions) -> Unit) {
        val options = PolylineOptions()
                .width(7F)
                .color(ContextCompat.getColor(requireContext(), R.color.colorMapScreen))
                .endCap(RoundCap())
                .startCap(RoundCap())
        routes.forEach { t: Directions.Routes? ->
            calculateTimeDistance(t?.legs!!)
            t.legs!!.forEach {
                it.steps!!.forEach { t: Directions.Routes.Legs.Steps? ->
                    val latLng = PolyUtil.decode(t?.polyline!!.points)
                    options.addAll(latLng)
                }
            }
        }
        callBack(options)
    }

    private fun getDistan(starting: LatLng, ending: LatLng) {
        val locationA = Location("point A")
        locationA.latitude = starting.latitude
        locationA.longitude = starting.longitude

        val locationB = Location("point B")
        locationB.latitude = ending.latitude
        locationB.longitude = ending.longitude

        val distance = locationA.distanceTo(locationB)

        if (distance >= 100) {
            setRoute(orderDetails!!, markerOptionsNew!!)
        }
    }

    private fun setRoute(orderDetails: OrderDetail, markerOptionsNew: MarkerOptions) {
        viewModel.getDirection(carMarker!!.position,LatLng(orderDetails.deliveryLatitude.toDouble(), orderDetails.deliveryLongitude.toDouble()))
    }

    private fun calculateTimeDistance(distances: List<Directions.Routes.Legs>) {
        val distance = (distances[0].distance!!.value / 1609.344)
        val time = (distances[0].duration!!.value / 60).toString()

        if (textViewMile != null && textViewTime != null) {
            textViewMile.text = String.format(Locale.US, "Distance : %.2f mi", distance)
            val duration = time.toIntOrNull()
            val hours = duration?.div(60)
            val minutes = duration?.rem(60)
            if (hours != null) {
                if (hours > 1) {
                    textViewTime.text = if (hours == 1) "Time : $hours Hour : $minutes mins" else "Time : $hours Hours : $minutes mins"

                } else {
                    textViewTime.text = "Time : $minutes mins "
                }
            } else {
                textViewTime.text = "Time : $duration mins "
            }
            //textViewTime.text = getString(R.string.times, time)
        }
    }

    override fun onPause() {
        super.onPause()
        if (schedule != null) {
            schedule!!.cancel()
        }
    }

    override fun createLayout(): Int = R.layout.track_google_map_fragment

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun bindData() {
        toolbar.showToolbar(false)

        schedule = fixedRateTimer("timer", false, 0L, 5 * 1000) {
            activity?.runOnUiThread {
                orderId?.let { viewModel.getOrderDetails(it) }
            }
        }

        textViewCall.setOnClickListener(this)

        toolbarMap.setNavigationOnClickListener {
            navigator.goBack()
        }
    }

    private fun initMap() {
        val mapFragment = childFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
        mMap?.uiSettings?.isTiltGesturesEnabled = false
        lastHead = mMap?.cameraPosition?.bearing!!
        if (mMap != null) {
            addPinOnMap()
        }
    }

    private fun addPinOnMap() {
        if (markerOptionsNew == null) {
            markerOptionsNew = MarkerOptions()
                    .position(LatLng(orderDetails!!.pickupLatitude.toDouble(), orderDetails!!.pickupLongitude.toDouble()))
                    .icon(bitmapDescriptorFromVector(this.activity!!, R.drawable.ic_my_current_location))
            mMap!!.addMarker(markerOptionsNew)
        }

        if (carMarker == null) {
            carMarker = mMap!!.addMarker(MarkerOptions()
                    .position(LatLng(orderDetails!!.driverDetails.latitude!!.toDouble(), orderDetails!!.driverDetails.longitude!!.toDouble()))
                    .flat(true)
                    .icon(bitmapDescriptorFromVector(this.activity!!, R.drawable.ic_car)))
        }

        mMap!!.addMarker(MarkerOptions()
                .position(LatLng(orderDetails!!.deliveryLatitude.toDouble(), orderDetails!!.deliveryLongitude.toDouble()))
                .icon(bitmapDescriptorFromVector(this.activity!!, R.drawable.ic_pin_1)))


        val builder = LatLngBounds.Builder()
        builder.include(LatLng(orderDetails!!.deliveryLatitude.toDouble(), orderDetails!!.deliveryLongitude.toDouble()))
        builder.include(carMarker?.position)
        val bounds = builder.build()


        mMap!!.setPadding(0, 0, 0, cardViewDriver.height)
        val cu = CameraUpdateFactory.newLatLngBounds(bounds, cardViewDriver.height)
        mMap!!.animateCamera(cu)


        setRoute(orderDetails!!, markerOptionsNew!!)

        Glide.with(activity!!)
                .load(orderDetails!!.driverDetails.profileImage)
                .apply(RequestOptions().placeholder(R.drawable.placeholder_car))
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .into(imageViewDriver)

        imageViewDriver.setOnClickListener {
            val intent = Intent(activity, LargeImageActivity::class.java)
            intent.putExtra("image", orderDetails!!.driverDetails.profileImage)
            val options = activity?.let { ActivityOptionsCompat.makeSceneTransitionAnimation(it, imageViewDriver, "profile") }
            startActivity(intent, options?.toBundle())
        }

        textViewName.text = orderDetails!!.driverDetails.name
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.textViewCall -> {
                val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", orderDetails?.driverDetails!!.phone, null))
                startActivity(intent)
            }
        }
    }


    private fun animateMarkerNew(endPosition: LatLng, marker: Marker?) {
        //when add icon add flat true
        if (marker != null) {
            val latLngTypeEvaluator: TypeEvaluator<LatLng?> = TypeEvaluator { v, fromLng, toLatLng ->
                SphericalUtil.interpolate(fromLng, toLatLng, v.toDouble())
            }
            val valueAnimator = ValueAnimator.ofObject(latLngTypeEvaluator, marker.position, endPosition)
            valueAnimator.interpolator = LinearInterpolator()
            valueAnimator.duration = 3000
            valueAnimator.addUpdateListener {
                val newLatLng = it.animatedValue as LatLng
                carMarker?.position = newLatLng

            }
            valueAnimator.start()

            val bearingTo = SphericalUtil.computeHeading(marker.position, endPosition)
            val bearingAnimator = ValueAnimator.ofFloat(lastHead, bearingTo.toFloat())
            bearingAnimator.interpolator = AccelerateDecelerateInterpolator()
            bearingAnimator.duration = 400
            bearingAnimator.addUpdateListener {
                val value = it?.animatedValue as Float
                carMarker?.rotation = value
                val cameraPosition = CameraPosition.builder(mMap?.cameraPosition)
                        .bearing(value)
                        .target(endPosition)
                        .zoom(mMap?.cameraPosition?.zoom!!)
                        .build()
                mMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            }

            bearingAnimator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    lastHead = bearingTo.toFloat()
                }

            })
            bearingAnimator.start()
        }
    }

}



