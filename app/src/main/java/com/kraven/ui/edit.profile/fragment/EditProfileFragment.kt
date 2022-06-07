package com.kraven.ui.edit.profile.fragment

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.DatePicker
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProviders
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.maps.model.LatLng

import com.kraven.R
import com.kraven.core.StatusCode
import com.kraven.core.Validator
import com.kraven.data.URLFactory
import com.kraven.data.pojo.Parameters
import com.kraven.data.pojo.User
import com.kraven.di.component.FragmentComponent
import com.kraven.exception.ApplicationException
import com.kraven.extensions.*
import com.kraven.ui.activity.IsolatedActivity
import com.kraven.ui.address.fragment.AddressListFragment
import com.kraven.ui.authentication.fragement.CountryCodeFragment
import com.kraven.ui.authentication.fragement.VerificationFragmentEdit
import com.kraven.ui.authentication.model.Country
import com.kraven.ui.authentication.viewmodel.AuthViewModel
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.home.fragment.HomeNewFragment
import com.kraven.ui.notification.fragment.NotificationFragment
import com.kraven.utils.*

import kotlinx.android.synthetic.main.edit_profile_fragment.*

import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import javax.inject.Inject


class EditProfileFragment : BaseFragment(), View.OnClickListener {


    var imageFilePath: String? = null

    @Inject
    lateinit var validator: Validator

    @Inject
    lateinit var parameters: Parameters
    private var mContext: Context? = null

    @Inject
    lateinit var locationManager: LocationManager
    private var currentLatLng: LatLng? = null
    lateinit var updateUserInfo: UpdateUserInfo
    private lateinit var authViewModel: AuthViewModel

    private var selectedCountryId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authViewModel = ViewModelProviders.of(this, viewModelFactory)[AuthViewModel::class.java]

        authViewModel.editUserLiveData.observe(this, { responseBody ->
            showLoader(false)
            showMessage(responseBody.message)
            setUserData(responseBody.data!!)
        }, { true })


        authViewModel.verifyOtpLiveData.observe(this,
                { responseBody ->
                    showLoader(false)
                    if (responseBody.code == StatusCode.CODE_SUCCESS) {
                        /* val jsonObject = JSONObject(responseBody.toString())
                         val otp = jsonObject.getString("otp")*/
                        showLoader(false)

                        navigator.loadActivity(IsolatedActivity::class.java).setPage(VerificationFragmentEdit::class.java).addBundle(Bundle().apply {
                            putString("otp", responseBody.data?.otp)
                            putString("countryCode", getText(editTextCountryCode))
                            putString("phone", getText(editTextMobileNumber))
                        }).forResult(101).start()
                    } else {
                        showMessage(responseBody.message)
                    }
                }
                , { true })
    }

    interface UpdateUserInfo {
        fun updateUser()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        try {
            if (context is UpdateUserInfo)
                updateUserInfo = context
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement updateUserInfo")
        }
    }


    override fun createLayout(): Int = R.layout.edit_profile_fragment

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun bindData() {

        if (locationPermission(activity!!)) {
            locationManager.locationUpdateLiveData.observe(this.viewLifecycleOwner, androidx.lifecycle.Observer {
                currentLatLng = it
                locationManager.locationUpdateLiveData.removeObservers(this)
            })
        }
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle(getString(R.string.edit_profile))
        toolbar.setToolbarTextColorWhite(true)
        toolbar.setButtonTextVisibility(false)

        //showLoader(true)
        nestedScrollView.viewShow()
        setUserData(session.user!!)


        textViewChangePassword.setOnClickListener(this)
        textViewManageAddress.setOnClickListener(this)
        textViewNotification.setOnClickListener(this)
        editTextCountryCode.setOnClickListener(this)
        editTextDOB.setOnClickListener(this)
        buttonUpdate.setOnClickListener(this)
        textViewSavedCards.setOnClickListener(this)

        imageViewUser.setOnClickListener {
            PermissionManager.Builder()
                    .permission(PermissionManager.PermissionEnum.WRITE_EXTERNAL_STORAGE)
                    .askAgain(true)
                    .callback(object : PermissionManager.FullCallback {
                        override fun result(permissionsGranted: ArrayList<PermissionManager.PermissionEnum>,
                                            permissionsDenied: ArrayList<PermissionManager.PermissionEnum>,
                                            permissionsDeniedForever: ArrayList<PermissionManager.PermissionEnum>,
                                            permissionsAsked: ArrayList<PermissionManager.PermissionEnum>) {
                            if (permissionsDeniedForever.size >= 1) {
                                commandDialogYesNo(getString(R.string.app_name),
                                        getString(R.string.need_permission)
                                        , object : DialogInterfaceYesNo {
                                    override fun onClickYes() {
                                        val intent = Intent()
                                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                        val uri = Uri.fromParts("package", activity!!.packageName, null)
                                        intent.data = uri
                                        startActivity(intent)
                                    }

                                    override fun onClickNo() {

                                    }
                                })

                            } else if (permissionsGranted.size >= 1) {
                                val imageChooserDialog = ImageChooserDialog.newInstance()
                                imageChooserDialog.setImageCallBack(object : ImageChooserDialog.ImageCallBack {
                                    override fun sendImage(imagePicker: ImagePicker) {
                                        imageFilePath = imagePicker.imagePath
                                        Glide.with(activity!!)
                                                .load(imageFilePath)
                                                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                                                .into(imageViewUser)
                                    }
                                })
                                imageChooserDialog.show(fragmentManager!!, "image Picker")
                            }
                        }

                    })
                    .ask(this)
        }
    }

    private fun setUserData(data: User) {
        //  showLoader(false)

        if (session.user!!.signupType == "Google" || session.user!!.signupType == "Facebook") {
            textViewChangePassword.viewGone()
        }
        session.user = data
        selectedCountryId = if (data.countryShortCode != null && data.countryShortCode.isNotEmpty()) data.countryShortCode else Locale.getDefault().country
        Glide.with(activity!!)
                .load(data.profileImage)
                .apply(RequestOptions().placeholder(R.drawable.ic_user))
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .into(imageViewUser)

        editTextFirstNameEdit.setText(data.firstName)
        editTextLastName.setText(data.lastName)
        editTextEmail.setText(data.email)

        if (data.dob == "" || data.dob == "00 000 0000") {
            data.dob = "01 Jan 2001"
        }

        val inputFormatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())
        val outputFormatter: DateTimeFormatter =
            DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        val date: LocalDate = LocalDate.parse(data.dob, inputFormatter)
        val formattedDate: String = outputFormatter.format(date)
        editTextDOB.setText(formattedDate)

        textViewName.text = String.format(Locale.US,"%s ", data.firstName, data.lastName)

        updateUserInfo.updateUser()

        editTextMobileNumber.setText(formatNumber(data.phone, selectedCountryId!!))
        editTextCountryCode.setText(getCountryCode(selectedCountryId, activity!!))
        editTextMobileNumber.hint = formatNumber(getHintText(selectedCountryId, activity!!), selectedCountryId!!)
        editTextMobileNumber.addTextChangedListener(setPhoneNumberFormat(selectedCountryId!!))

        when (data.gender) {
            getString(R.string.male) -> {
                radioMale.isChecked = true
            }
            getString(R.string.female) -> {
                radioFemale.isChecked = true
            }
            getString(R.string.rathernotsay) -> {
                radioRatherNotSay.isChecked = true
            }
            else -> {
                radioRatherNotSay.isChecked = true
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionManager.handleResult(this@EditProfileFragment, requestCode, permissions, grantResults)
    }

    override fun onClick(v: View?) {
        var cal = Calendar.getInstance()
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val date: LocalDate = LocalDate.of(year, monthOfYear, dayOfMonth)
                val outputFormatter: DateTimeFormatter =
                    DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                val formattedDate: String = outputFormatter.format(date)

                editTextDOB.setText(formattedDate)
            }
        }

        when (v?.id) {
            R.id.textViewSavedCards->{
            navigator.load(SavedCardsFragment::class.java).replace(true)
            }
            R.id.editTextDOB -> {
                val inputFormatter: DateTimeFormatter =
                    DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                val date: LocalDate = LocalDate.parse(editTextDOB.text, inputFormatter)
                DatePickerDialog(activity!!,
                    dateSetListener,
                    date.year,
                    date.monthValue,
                    date.dayOfMonth).show()
            }
            R.id.editTextCountryCode -> {
                navigator.loadActivity(IsolatedActivity::class.java).setPage(CountryCodeFragment::class.java).forResult(10).start()
            }
            R.id.textViewChangePassword -> {
                navigator.load(ChangePasswordFragment::class.java).replace(true)
            }
            R.id.textViewManageAddress -> {
                navigator.load(AddressListFragment::class.java).replace(true)
            }

            R.id.textViewNotification -> {
                navigator.load(NotificationFragment::class.java).replace(true)
            }
            R.id.buttonUpdate -> {
                try {
                    validator.submit(editTextFirstNameEdit, inputLayoutFirstName)
                            .checkEmpty().errorMessage(R.string.enter_first_name)
                            .matchPatter(ConstantApp.Validation.NAME_VALIDATION).errorMessage(R.string.enter_valid_name)
                            .check()

                    validator.submit(editTextLastName, inputLayoutLastName)
                            .checkEmpty().errorMessage(R.string.enter_last_name)
                            .matchPatter(ConstantApp.Validation.NAME_VALIDATION).errorMessage(R.string.enter_valid_name)
                            .check()

                    validator.submit(editTextEmail, inputLayoutEmail)
                            .checkEmpty().errorMessage(R.string.enter_email_address)
                            .checkValidEmail().errorMessage(R.string.enter_valid_email)
                            .check()
                    validator.submit(editTextCountryCode, inputLayoutCountryCode)
                            .checkEmpty().errorMessage(R.string.enter_country_code)
                            .check()
                    validator.submit(editTextMobileNumber, inputLayoutMobileNumber)
                            .checkEmpty().errorMessage(R.string.enter_mobile_number)
                            .checkMinDigits(7).errorMessage(R.string.enter_valid_mobile_number)
                            .check()

                    val inputFormatter: DateTimeFormatter =
                        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                    val date: LocalDate = LocalDate.parse(editTextDOB.text, inputFormatter)
                    val date1: LocalDate = LocalDate.now()
                    if (date.year >= date1.year - 12) {
                        Log.d("JSR", "Your age must be bigger than 12")
                        navigator.showErrorMessage("Your age must be bigger than 12")
                        throw ApplicationException("Your age must be bigger than 12")
                    }


                    if (session.user?.countryCode != getText(editTextCountryCode) || formatNumber(session.user?.phone!!, selectedCountryId!!) != getText(editTextMobileNumber)) {
                        showLoader(true)
                        val parameters = Parameters()
                        parameters.countryCode = getText(editTextCountryCode)
                        parameters.phone = getText(editTextMobileNumber)
                        authViewModel.verifyOtp(parameters)
                    } else {
                        showLoader(true)
                        if (imageFilePath != null) {
                            uploadDocument(imageFilePath!!, AWSUtil.FOLDER_USER)
                        } else {
                            val parameters = Parameters()
                            parameters.firstName = getText(editTextFirstNameEdit)
                            parameters.lastName = getText(editTextLastName)
                            parameters.email = getText(editTextEmail)
                            parameters.latitude = currentLatLng?.latitude.toString()
                            parameters.longitude = currentLatLng?.longitude.toString()

                            if (radioMale.isChecked) {
                                parameters.gender = "Male"
                            } else if (radioFemale.isChecked) {
                                parameters.gender = "Female"
                            } else if (radioRatherNotSay.isChecked) {
                                parameters.gender = "Rather Not Say"
                            }


                            val outputFormatter: DateTimeFormatter =
                                DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())
                            val formattedDate: String = outputFormatter.format(date)
                            parameters.dob = formattedDate

                            authViewModel.editUser(parameters)
                        }
                    }

                } catch (e: ApplicationException) {
                    e.message
                }

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 10) {
                val getCode = data?.getParcelableExtra<Country>(ConstantApp.PassValue.COUNTRY_CODE)
                editTextCountryCode.setText("+" + getCode?.countryCode)
                editTextMobileNumber.clearText()
                editTextMobileNumber.hint = formatNumber(getHintText(getCode?.id, activity!!), getCode?.id!!)
                if (getCode.leadingDigits != null) {
                    editTextMobileNumber.setText(getCode.leadingDigits)
                    editTextMobileNumber.setSelection(editTextMobileNumber.text?.length!!)
                }
                selectedCountryId = getCode.id!!
                setPhoneNumberFormat(getCode.id!!)

            } else if (requestCode == 101) {
                if (imageFilePath != null) {
                    uploadDocument(imageFilePath!!, AWSUtil.FOLDER_USER)
                } else {
                    val parameters = Parameters()
                    parameters.countryShortCode = selectedCountryId
                    parameters.firstName = getText(editTextFirstNameEdit)
                    parameters.lastName = getText(editTextLastName)
                    parameters.email = getText(editTextEmail)
                    parameters.countryCode = getCountryCodeDial(selectedCountryId, activity!!)

                    if (getText(editTextMobileNumber)?.startsWith(getLeadingDigits(selectedCountryId, activity!!))!!) {
                        parameters.phone = getText(editTextMobileNumber)?.replace(getLeadingDigits(selectedCountryId, activity!!), "")?.replace("-", "")?.replace(" ", "")
                    } else {
                        parameters.phone = getText(editTextMobileNumber)?.replace("-", "")?.replace(" ", "")
                    }
                    /*parameters.countryCode = getText(editTextCountryCode)
                    parameters.phone = getText(editTextMobileNumber)*/
                    parameters.latitude = currentLatLng?.latitude.toString()
                    parameters.longitude = currentLatLng?.longitude.toString()

                    if (radioMale.isChecked) {
                        parameters.gender = "Male"
                    } else if (radioFemale.isChecked) {
                        parameters.gender = "Female"
                    } else if (radioRatherNotSay.isChecked) {
                        parameters.gender = "Rather Not Say"
                    }

                    val outputFormatter: DateTimeFormatter =
                        DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())
                    val inputFormatter: DateTimeFormatter =
                        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                    val date: LocalDate = LocalDate.parse(editTextDOB.text, inputFormatter)
                    val formattedDate: String = outputFormatter.format(date)
                    parameters.dob = formattedDate

                    authViewModel.editUser(parameters)
                }
            }
        }
    }


    private fun uploadDocument(imageFilePath: String, folderName: String) {
        val transferUtility = AWSUtil.getTransferUtility()

        val observer: TransferObserver
        val fileName: String = getFileName()

        observer = transferUtility.upload(if (URLFactory.IS_LOCAL) AWSUtil.MY_BUCKET_LOCAL else AWSUtil.MY_BUCKET, folderName + fileName, File(imageFilePath), CannedAccessControlList.PublicReadWrite)

        observer.setTransferListener(object : TransferListener {
            override fun onStateChanged(id: Int, state: TransferState) {
                if (state == TransferState.COMPLETED) {
                    val parameters = Parameters()
                    parameters.countryShortCode = selectedCountryId
                    if (editTextFirstNameEdit != null) {
                        parameters.firstName = getText(editTextFirstNameEdit)
                        parameters.lastName = getText(editTextLastName)
                        parameters.email = getText(editTextEmail)
                        parameters.countryShortCode = selectedCountryId
                        if (getText(editTextMobileNumber)?.startsWith(getLeadingDigits(selectedCountryId, activity!!))!!) {
                            parameters.phone = getText(editTextMobileNumber)?.replace(getLeadingDigits(selectedCountryId, activity!!), "")?.replace("-", "")?.replace(" ", "")
                        } else {
                            parameters.phone = getText(editTextMobileNumber)?.replace("-", "")?.replace(" ", "")
                        }
                        parameters.latitude = currentLatLng?.latitude.toString()
                        parameters.longitude = currentLatLng?.longitude.toString()
                        parameters.profileImage = fileName

                        if (radioMale.isChecked) {
                            parameters.gender = "Male"
                        } else if (radioFemale.isChecked) {
                            parameters.gender = "Female"
                        } else if (radioRatherNotSay.isChecked) {
                            parameters.gender = "Rather Not Say"
                        }

                        parameters.dob = getText(editTextDOB)

                        authViewModel.editUser(parameters)
                    } else {
                        showLoader(false)
                        showMessage("Something went wrong")
                    }

                } else if (state == TransferState.CANCELED) {
                    showLoader(false)
                    transferUtility.cancel(observer.id)
                }
            }

            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {}

            override fun onError(id: Int, ex: Exception) {
                ex.printStackTrace()
            }
        })
    }

    private fun getFileName(): String {
        return Random().nextInt(Math.abs(System.currentTimeMillis().toInt())).toString() + ".jpg"
    }

    override fun onBackActionPerform(): Boolean {
        navigator.load(HomeNewFragment::class.java).clearHistory(null).replace(false)
        return false
    }


}