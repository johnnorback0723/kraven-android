package com.kraven.ui.address.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.NonNull
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.textfield.TextInputLayout
import com.kraven.R
import com.kraven.core.Common
import com.kraven.core.StatusCode
import com.kraven.core.Validator
import com.kraven.data.pojo.Parameters
import com.kraven.di.component.FragmentComponent
import com.kraven.exception.ApplicationException
import com.kraven.extensions.getText
import com.kraven.extensions.requirePermission
import com.kraven.ui.activity.HomeActivity
import com.kraven.ui.activity.MapLocationActivity
import com.kraven.ui.address.model.Cay
import com.kraven.ui.authentication.viewmodel.AuthViewModel
import com.kraven.ui.base.BaseFragment
import com.kraven.utils.ConstantApp
import com.kraven.utils.LocationManager
import com.kraven.utils.PermissionManager
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.SingleOnSubscribe
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.address_fragment.*
import java.util.*
import javax.inject.Inject


class AddressFragment : BaseFragment(), View.OnClickListener {

    @Inject
    lateinit var validator: Validator

    @Inject
    lateinit var locationManager: LocationManager

    @Inject
    lateinit var parameters: Parameters

    private val PLACE_PICKER_REQUEST = 1
    private var latitude: String? = "0"
    private var longitude: String? = "0"
    private var address: Address? = null
    private var addressPosition: com.kraven.ui.address.model.Address? = null
    private var position: String? = null

    private lateinit var authViewModel: AuthViewModel

    private val cayStrList = mutableListOf("Select if it is a cay")
    private var cayList: List<Cay>? = null
    private var cayIdx = 0

    private val isCart: Boolean by lazy {
        val args = arguments ?: throw IllegalStateException("Missing arguments")
        args.getBoolean(ConstantApp.PassValue.IS_CART)
    }

    private val signUp: Boolean by lazy {
        val args = arguments ?: throw IllegalStateException("Missing arguments")
        args.getBoolean("signUp")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            position = arguments?.getString(ConstantApp.PassValue.POSITION)
            if (position.equals(ConstantApp.EDIT)) {
                addressPosition = arguments?.getParcelable(ConstantApp.PassValue.ADDRESS)
            }
        }
        authViewModel = ViewModelProviders.of(this, viewModelFactory)[AuthViewModel::class.java]

        authViewModel.addAddressLiveData.observe(this,
                { responseBody ->
                    showLoader(false)
                    if (responseBody.code == StatusCode.CODE_SUCCESS) {

                        if (signUp) {
                            navigator.loadActivity(HomeActivity::class.java).byFinishingAll().start()
                        } else {
                            if (isCart) {
                                navigator.goBack()
                            } else {
                                val resultIntent = Intent()
                                activity!!.setResult(RESULT_OK, resultIntent)
                                navigator.finish()

                            }
                        }
                    } else {
                        showMessage(responseBody.message)
                    }
                }
                , { true })

        authViewModel.editAddress.observe(this,
                { responseBody ->
                    showLoader(false)
                    if (responseBody.code == StatusCode.CODE_SUCCESS) {
                        navigator.goBack()
                    } else {
                        showMessage(responseBody.message)
                    }

                }, { true })

        authViewModel.getCays.observe(this,
                { responseBody ->
                    showLoader(false)
                    if (responseBody.code == StatusCode.CODE_SUCCESS) {
                        cayList = responseBody.data?.toMutableList()
                        cayList = cayList?.filterIndexed { index, cay ->
                            cay.status == "Active"
                        }

                        if (cayList?.size != 0) spinner_cay_list.visibility = VISIBLE
                        var cayIdx = 0
                        cayList?.forEach {
                            cayStrList.add(it.location)
                            if (addressPosition != null && addressPosition?.cayId == it.cayId) {
                                cayIdx = cayStrList.size - 1
                            }
                        }
                        if (cayIdx != 0) {
                            spinner_cay_list.setSelection(cayIdx)
                        }
                    } else {
                        showMessage(responseBody.message)
                    }
                }, { true })
    }

    override fun createLayout(): Int = R.layout.address_fragment

    override fun inject(fragmentComponent: FragmentComponent) {
        fragmentComponent.inject(this)
    }


    override fun bindData() {
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle("")
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)

        setHint(0)
        if (addressPosition != null) {
            parameters.addressId = addressPosition?.id
            editTextAddress.setText(addressPosition?.address)
            editTextCountry.setText(addressPosition?.country)
            editTextStreetName.setText(addressPosition?.streetName)
            editTextCity.setText(addressPosition?.city)
            editTextLocality.setText(addressPosition?.buildingName)
            editTextHouse.setText(addressPosition?.house)
            editTextLandmark.setText(addressPosition?.landmark)
            editTextAddressType.setText(addressPosition?.addressType)
            latitude = addressPosition?.latitude
            longitude = addressPosition?.longitude
        }

        editTextAddress.setOnClickListener(this)
        buttonSave.setOnClickListener(this)
        buttonLocation.setOnClickListener(this)

        parameters.cayId = "";
        parameters.cayFee = "";
        authViewModel.getCayList(parameters)


        // add a hint to spinner
        // list first item will show as hint
        //cayStrList.add(0,"Select if it is a cay")

        // initialize an array adapter for spinner
        val adapter: ArrayAdapter<String> = object: ArrayAdapter<String>(
                context!!,
                android.R.layout.simple_spinner_dropdown_item,
                cayStrList
        ){
            override fun getDropDownView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
            ): View {
                val view: TextView = super.getDropDownView(
                        position,
                        convertView,
                        parent
                ) as TextView
                // set item text bold
                view.setTypeface(view.typeface, Typeface.BOLD)

                // make hint item color gray
                if(position == 0){
                    view.setTextColor(Color.LTGRAY)
                }

                return view
            }

            override fun isEnabled(position: Int): Boolean {
                // disable first item
                // first item is display as hint
                return position != 0
            }
        }

        // finally, data bind spinner with adapter
        spinner_cay_list.adapter = adapter

        // spinner on item selected listener
        spinner_cay_list.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
            ) {
                // by default spinner initial selected item is first item
                if (position != 0){
                    val cay = cayList?.get(position - 1)
                    //getAddress(cay?.latitude.toString(), cay?.longitude.toString())
                    cayIdx = position
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // another interface callback
            }
        }
    }

    private fun setHint(position: Int) {
        HintColorHelper.setUpHintColor(editTextAddress, inputLayoutAddress, position)
        HintColorHelper.setUpHintColor(editTextStreetName, inputLayoutStreetName, position)
        HintColorHelper.setUpHintColor(editTextAddressType, inputLayoutAddressType, position)
        HintColorHelper.setUpHintColor(editTextCountry, inputLayoutCountry, position)
        HintColorHelper.setUpHintColor(editTextCity, inputLayoutCity, position)
        HintColorHelper.setUpHintColor(editTextHouse, inputLayoutHouse, position)
    }

    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionManager.handleResult(this, requestCode, permissions, grantResults)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonLocation -> {
                if (latitude != "0" && longitude != "0") {
                    getAddress(latitude!!, longitude!!)
                } else {
                    requirePermission(activity!!, "This app need permissions to use this feature.You can grant them in app settings.") {
                        locationManager.checkLocationEnableAndStartUpdate(true)
                        locationManager.locationUpdateLiveData.observe(this, {
                            latitude = it.latitude.toString()
                            longitude = it.longitude.toString()
                            getAddress(latitude!!, longitude!!)
                        })
                    }
                }

            }
            R.id.editTextAddress -> {
                var strLat = latitude
                var strLng = longitude
                var zoom = "16"
                if (latitude == "0" && longitude == "0") {
                    when (session.user?.island) {
                        "New Providence" -> {
                            strLat = "25.0333332"
                            strLng = "-77.3999984"
                            zoom = "11"
                        }
                        "Grand Bahama" -> {
                            strLat = "26.659447"
                            strLng = "-78.52065"
                            zoom = "11"
                        }
                        "Exuma" -> {
                            strLat = "23.5333312"
                            strLng = "-75.83333"
                            zoom = "11"
                        }
                    }
                }

                val intent = Intent(activity!!, MapLocationActivity::class.java)
                intent.putExtra("latitude", strLat)
                intent.putExtra("longitude", strLng)
                intent.putExtra("zoom", zoom)
                startActivityForResult(intent, PLACE_PICKER_REQUEST)
            }

            R.id.buttonSave -> {
                if (isValidation()) {
                    if (session.isProtoType) {
                        navigator.loadActivity(HomeActivity::class.java).byFinishingAll().start()
                    } else {
                        showLoader(true)
                        parameters.address = getText(editTextAddress)
                        parameters.streetName = getText(editTextStreetName)
                        parameters.addressType = getText(editTextAddressType)
                        parameters.country = getText(editTextCountry)
                        parameters.city = getText(editTextCity)
                        //parameters.area = getText(editTextLocality)
                        parameters.house = getText(editTextHouse)
                        parameters.landmark = getText(editTextLandmark)
                        parameters.buildingName = getText(editTextLocality)
                        if (position.equals(ConstantApp.EDIT)) {
                            latitude = addressPosition?.latitude
                            longitude = addressPosition?.longitude
                        }
                        parameters.latitude = latitude
                        parameters.longitude = longitude

                        if (cayIdx != 0) {
                            val cay = cayList?.get(cayIdx - 1)
                            parameters.isCay = "1"
                            parameters.cayId = cay?.cayId.toString()
                            parameters.cayFee = cay?.cayFee.toString()
                        }

                        if (position.equals(ConstantApp.EDIT)) {
                            authViewModel.editAddress(parameters)
                        } else {
                            authViewModel.addAddress(parameters)
                        }

                    }

                }
            }
        }
    }

    private fun isValidation(): Boolean {
        try {
            validator.submit(editTextAddress, inputLayoutAddress)
                    .checkEmpty().errorMessage(R.string.enter_address)
                    .check()

            validator.submit(editTextStreetName, inputLayoutStreetName)
                    .checkEmpty().errorMessage("Please enter street name")
                    .check()

            validator.submit(editTextAddressType, inputLayoutAddressType)
                    .checkEmpty().errorMessage(R.string.enter_address_type)
                    .check()

            validator.submit(editTextCountry, inputLayoutCountry)
                    .checkEmpty().errorMessage(R.string.enter_country)
                    .check()

            validator.submit(editTextCity, inputLayoutCity)
                    .checkEmpty().errorMessage(R.string.enter_city)
                    .check()

            validator.submit(editTextHouse, inputLayoutHouse)
                    .checkEmpty().errorMessage(R.string.enter_house_no)
                    .check()


            return true

        } catch (e: ApplicationException) {
            e.message
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PLACE_PICKER_REQUEST) {
                if (data!!.getStringExtra(Common.LOCATION) != null) {
                    val result = data.getStringExtra(Common.LOCATION)
                    val latLng = data.getParcelableExtra<LatLng>(Common.ADDRESS_LAT_LONG)

                    if (result != null) {
                        if (result.isNotEmpty()) {
                            editTextAddress.setText(result)
                            if (latLng != null) {
                                latitude = latLng.latitude.toString()
                            }
                            if (latLng != null) {
                                longitude = latLng.longitude.toString()
                            }
                            getAddress(latitude!!, longitude!!)
                        }
                    }
                }
            }
        }
    }

    private fun getAddress(lati: String, longi: String) {
        Single.create(SingleOnSubscribe<Address> { emitter ->
            val geoCoder = Geocoder(activity, Locale.getDefault())
            try {
                val addressList = geoCoder.getFromLocation(lati.toDouble(), longi.toDouble(), 1)
                if (addressList != null && addressList.size > 0) {
                    address = addressList[0]
                }
            } catch (e: Exception) {
                navigator.showErrorMessage(getString(R.string.can_not_find_address))
            }
            emitter.onSuccess(address!!)
        }).observeOn(Schedulers.io())
                .subscribe(object : SingleObserver<Address> {
                    override fun onSuccess(address: Address) {

                        activity?.runOnUiThread {
                            editTextAddress.setText(address.getAddressLine(0))
                            editTextCountry.setText(address.countryName)
                            editTextCity.setText(address.locality)
                            editTextStreetName.setText(address.thoroughfare)
                            //editTextLocality.setText(address.thoroughfare)
                            //editTextHouse.setText(address.subThoroughfare)
                            editTextLandmark.setText(address.subLocality)

                            inputLayoutAddress.hint = getString(R.string.address)
                            inputLayoutStreetName.hint = "Street Name"
                            inputLayoutAddressType.hint = getString(R.string.address_type)
                            inputLayoutCountry.hint = getString(R.string.country)
                            inputLayoutCity.hint = getString(R.string.city)
                            inputLayoutLocality.hint = getString(R.string.locality)
                            inputLayoutHouse.hint = getString(R.string.house_no)
                            inputLayoutLandmark.hint = getString(R.string.landmark)
                            setHint(1)
                        }
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {

                    }
                })
    }

    object HintColorHelper {
        fun setUpHintColor(editText: EditText, textInputLayout: TextInputLayout, position: Int) {
            textInputLayout.setHintTextAppearance(0)
            val hint = textInputLayout.hint?.toString()
            if (hint != null && hint.isNotEmpty()) {
                val hintWithAsterisk = getHintWithAsterisk(hint)

                if (position == 1) {
                    editText.requestFocus()
                }
                if (!editText.hasFocus()) {
                    textInputLayout.hint = null
                    editText.hint = hintWithAsterisk
                }
                setOnFocuschangeListener(editText, textInputLayout, hintWithAsterisk)
            }
        }

        private fun setOnFocuschangeListener(editText: EditText, textInputLayout: TextInputLayout, hintWithAsterisk: SpannableStringBuilder) {
            editText.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    textInputLayout.hint = hintWithAsterisk
                    editText.hint = null
                } else if (editText.text.toString().isEmpty()) {
                    textInputLayout.hint = null
                    editText.hint = hintWithAsterisk
                }
            }
        }

        private fun getHintWithAsterisk(hint: String): SpannableStringBuilder {
            val asterisk = " *"
            val builder = SpannableStringBuilder()
            builder.append(hint)
            val start = builder.length
            builder.append(asterisk)
            val end = builder.length
            builder.setSpan(ForegroundColorSpan(Color.RED), start, end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            return builder
        }
    }

}