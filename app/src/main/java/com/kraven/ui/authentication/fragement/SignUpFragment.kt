package com.kraven.ui.authentication.fragement


import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.DatePicker
import androidx.lifecycle.ViewModelProviders
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.firebase.iid.FirebaseInstanceId
import com.kraven.R
import com.kraven.core.StatusCode
import com.kraven.core.Validator
import com.kraven.data.pojo.Parameters
import com.kraven.di.component.FragmentComponent
import com.kraven.exception.ApplicationException
import com.kraven.extensions.*
import com.kraven.ui.activity.HomeActivity
import com.kraven.ui.activity.IsolatedActivity
import com.kraven.ui.address.fragment.AddressFragment
import com.kraven.ui.authentication.model.CountriesFetcher
import com.kraven.ui.authentication.model.Country
import com.kraven.ui.authentication.viewmodel.AuthViewModel
import com.kraven.ui.base.BaseFragment
import com.kraven.utils.*
import kotlinx.android.synthetic.main.edit_profile_fragment.*
import kotlinx.android.synthetic.main.signup_fragment.*
import kotlinx.android.synthetic.main.signup_fragment.editTextCountryCode
import kotlinx.android.synthetic.main.signup_fragment.editTextDOB
import kotlinx.android.synthetic.main.signup_fragment.editTextEmail
import kotlinx.android.synthetic.main.signup_fragment.editTextLastName
import kotlinx.android.synthetic.main.signup_fragment.editTextMobileNumber
import kotlinx.android.synthetic.main.signup_fragment.inputLayoutCountryCode
import kotlinx.android.synthetic.main.signup_fragment.inputLayoutEmail
import kotlinx.android.synthetic.main.signup_fragment.inputLayoutFirstName
import kotlinx.android.synthetic.main.signup_fragment.inputLayoutLastName
import kotlinx.android.synthetic.main.signup_fragment.inputLayoutMobileNumber
import org.json.JSONException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import javax.inject.Inject


class SignUpFragment : BaseFragment(), View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {


    @Inject
    lateinit var validator: Validator

    //@Inject
    var parametes: Parameters? = null

    @Inject
    lateinit var parameters: Parameters

    var passParameters: Parameters? = null


    @Inject
    lateinit var locationManager: LocationManager

    private lateinit var authViewModel: AuthViewModel

    private var latitude: Double? = null
    private var longitude: Double? = null

    private var isLoginType: String? = null
    lateinit var mCountries: CountriesFetcher.CountryList
    private lateinit var callbackManager: CallbackManager
    var mGoogleApiClient: GoogleSignInClient? = null
    private var RC_SIGN_IN = 1
    private var selectedCountryId: String? = null

    override fun createLayout(): Int = R.layout.signup_fragment

    override fun inject(fragmentComponent: FragmentComponent) {
        fragmentComponent.inject(this)
    }

    override fun onStart() {
        super.onStart()
        requirePermission(activity!!, "This app need permissions to use this feature.You can grant them in app settings.") {
            locationManager.checkLocationEnableAndStartUpdate(true)
            locationManager.getLocationUpdate(object : LocationManager.LocationListener {
                override fun onLocationAvailable(latLng: LatLng) {
                    latitude = latLng.latitude
                    longitude = latLng.longitude
                }

                override fun onFail(status: LocationManager.LocationListener.Status) {

                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        LoginManager.getInstance().logOut()

        Log.d("JSR", "GoogleSignInOptions Builder  - 2")
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

        mGoogleApiClient = GoogleSignIn.getClient(activity!!, gso)

        mGoogleApiClient?.signOut()?.addOnCompleteListener {

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        callbackManager = CallbackManager.Factory.create()
        if (arguments != null) {
            passParameters = arguments!!.getParcelable(ConstantApp.PassValue.PARAMETERS)
            isLoginType = arguments!!.getString(ConstantApp.PassValue.IS_LOGIN_TYPE)
        }

        authViewModel = ViewModelProviders.of(this, viewModelFactory)[AuthViewModel::class.java]

        authViewModel.verifyEmailLiveData.observe(this,
                { responseBody ->

                    if (responseBody.code == StatusCode.CODE_SUCCESS) {
                        val parameters = Parameters()
                        parameters.countryCode = parametes?.countryCode
                        parameters.phone = parametes?.phone
                        authViewModel.verifyOtp(parameters)
                    } else {
                        showLoader(false)
                        showMessage(responseBody.message)
                    }
                }
                , { true })

        authViewModel.verifyOtpLiveData.observe(this,
                { responseBody ->
                    showLoader(false)
                    if (responseBody.code == StatusCode.CODE_SUCCESS) {
                        parametes?.otp = responseBody.data?.otp
                        showLoader(false)

                        val passParameters = Bundle()
                        passParameters.putParcelable(ConstantApp.PassValue.PARAMETERS, parametes)
                        navigator.load(VerificationFragment::class.java).setBundle(passParameters).replace(true)
                    } else {
                        showMessage(responseBody.message)
                    }
                }
                , { true })

        authViewModel.signUpLogInLiveData.observe(this,
                { responseBody ->
                    showLoader(false)
                    when {
                        responseBody.code == StatusCode.CODE_SUCCESS -> navigator.loadActivity(HomeActivity::class.java).byFinishingAll().start()
                        responseBody.code == StatusCode.CODE_ADD_ADDRESS -> navigator.load(AddressFragment::class.java).setBundle(Bundle().apply {
                            putBoolean(ConstantApp.PassValue.IS_CART, false)
                        }).replace(true)
                        responseBody.code == StatusCode.CODE_SOCIAL_ID_NOT_REGISTER -> {
                            editTextFirstName.setText(passParameters?.firstName)
                            editTextLastName.setText(passParameters?.lastName)
                            editTextEmail.setText(passParameters?.email)
                            editTextPassword.isEnabled = false
                            editTextConfirmPassword.isEnabled = false
                        }
                        else -> showMessage(responseBody.message)
                    }
                }
                ,
                { true })

    }


    override fun bindData() {
        if (passParameters != null) {
            editTextFirstName.setText(passParameters?.firstName)
            editTextLastName.setText(passParameters?.lastName)
            editTextEmail.setText(passParameters?.email)
            inputLayoutPassword.viewGone()
            inputLayoutConfirmPassword.viewGone()
            editTextPassword.isEnabled = false
        }
        if (locationPermission(activity!!)) {
            locationManager.getLocationUpdate(object : LocationManager.LocationListener {
                override fun onLocationAvailable(latLng: LatLng) {
                    latitude = latLng.latitude
                    longitude = latLng.longitude
                }

                override fun onFail(status: LocationManager.LocationListener.Status) {

                }
            })
        }

        toolbar.showToolbar(true)
        toolbar.setToolbarTitle("")
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)


        TextDecorator.decorate(checkBoxAgree, resources.getString(R.string.agree))
                .apply {
                    setTypeface(R.font.work_sans_bold, 8, finalLength)
                    setTextColor(R.color.light_blue, 8, finalLength)
                    makeTextClickable(object : ClickableSpan() {
                        override fun updateDrawState(ds: TextPaint) {
                            ds.isUnderlineText = false
                        }

                        override fun onClick(widget: View) {
                            widget.cancelPendingInputEvents()
                            val bundle = Bundle()
                            bundle.putString(ConstantApp.PassValue.TermsLink, getString(R.string.terms_and_conditions_customer))
                            bundle.putString(ConstantApp.PassValue.TermsHeder, getString(R.string.terms_condition))
                            navigator.loadActivity(IsolatedActivity::class.java).setPage(TermsAndConditionsFragment::class.java).addBundle(bundle).start()
                        }

                    }, "Terms & Conditions")
                }
                .build()

        editTextDOB.setOnClickListener(this)
        editTextCountryCode.setOnClickListener(this)
        buttonSignUp.setOnClickListener(this)

        textViewFacebookLogin.setOnClickListener(this)
        textViewGoogleLogin.setOnClickListener(this)

        selectedCountryId = Locale.getDefault().country
        editTextCountryCode.setText(getCountryCode(Locale.getDefault().country, activity!!))
        editTextMobileNumber.hint = formatNumber(getHintText(Locale.getDefault().country, activity!!), Locale.getDefault().country)
        editTextMobileNumber.addTextChangedListener(setPhoneNumberFormat(Locale.getDefault().country))
        //editTextMobileNumber.addTextChangedListener(mPhoneNumberWatcher)

        val inputFormatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())
        val outputFormatter: DateTimeFormatter =
            DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        val date: LocalDate = LocalDate.parse("01 Jan 2001", inputFormatter)
        val formattedDate: String = outputFormatter.format(date)
        editTextDOB.setText(formattedDate)

        radioMale1.isChecked = true
    }


    private fun checkWriteExternalPermission(): Boolean {
        val permission = android.Manifest.permission.ACCESS_FINE_LOCATION
        val res = requireContext().checkCallingOrSelfPermission(permission)
        return res == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionManager.handleResult(this@SignUpFragment, requestCode, permissions, grantResults)
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

            R.id.buttonSignUp -> {
                if (isValidation()) {
                    if (checkBoxAgree.isChecked) {

                            showLoader(true)
                            parametes = Parameters()

                            parametes?.socialId = passParameters?.socialId
                            parametes?.firstName = getText(editTextFirstName)
                            parametes?.lastName = getText(editTextLastName)
                            parametes?.email = getText(editTextEmail)
                            parametes?.password = getText(editTextPassword)

                            if (radioMale1.isChecked) {
                                parametes?.gender = "Male"
                            } else if (radioFemale1.isChecked) {
                                parametes?.gender = "Female"
                            } else if (radioRatherNotSay1.isChecked) {
                                parametes?.gender = "Rather Not Say"
                            }

                            val inputFormatter: DateTimeFormatter =
                                DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                            val date: LocalDate = LocalDate.parse(editTextDOB.text, inputFormatter)
                            val outputFormatter: DateTimeFormatter =
                                DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())
                            val formattedDate: String = outputFormatter.format(date)
                            parametes?.dob = formattedDate

                            parametes?.countryCode = getCountryCodeDial(selectedCountryId, activity!!)
                            if (getText(editTextMobileNumber)?.startsWith(getLeadingDigits(selectedCountryId, activity!!))!!) {
                                parametes?.phone = getText(editTextMobileNumber)?.replace(getLeadingDigits(selectedCountryId, activity!!), "")?.replace("-", "")?.replace(" ", "")
                            } else {
                                parametes?.phone = getText(editTextMobileNumber)?.replace("-", "")?.replace(" ", "")
                            }
                            parametes?.latitude = latitude.toString()
                               parametes?.longitude = longitude.toString()
                            if (getText(editTextReferralCode)?.isNotEmpty()!!) {
                                parametes?.referralCode = getText(editTextReferralCode)
                            }
                            parametes?.deviceType = "A"
                            parametes?.referralCode = getText(editTextReferralCode)
                            parametes?.deviceId = if (session.deviceId.isBlank()) FirebaseInstanceId.getInstance().token else session.deviceId
                            parametes?.signupType = if (isLoginType.equals(ConstantApp.FACEBOOK)) "Facebook" else if (isLoginType.equals(ConstantApp.GOOGLE)) "Google" else "Normal"
                            parametes?.countryShortCode = selectedCountryId
                            if (getText(editTextReferralCode)?.isNotEmpty()!!) {
                                //parametes?.referralCode = getText(editTextReferralCode)
                                authViewModel.verifyEmail(Parameters(email = getText(editTextEmail), referralCode = getText(editTextReferralCode)))
                            } else {
                                authViewModel.verifyEmail(Parameters(email = getText(editTextEmail)))
                            }

                    } else {
                        navigator.showErrorMessage(getString(R.string.accept_term_condition))
                    }
                }
            }

            R.id.textViewFacebookLogin -> {
                LoginManager.getInstance().logOut()
                showLoader(true)
                LoginManager.getInstance().logInWithReadPermissions(this, listOf("email") as Collection<String>?)

                LoginManager.getInstance().registerCallback(callbackManager,
                        object : FacebookCallback<LoginResult> {
                            override fun onSuccess(loginResult: LoginResult) {
                                val request = GraphRequest.newMeRequest(
                                        loginResult.accessToken
                                ) { `object`, response ->
                                    try {
                                        inputLayoutPassword.viewGone()
                                        inputLayoutConfirmPassword.viewGone()
                                        editTextPassword.isEnabled = false

                                        isLoginType = ConstantApp.FACEBOOK
                                        parameters.socialId = `object`.getString("id")
                                        parameters.deviceType = "A"
                                        parameters.countryShortCode = selectedCountryId
                                        parametes?.deviceId = if (session.deviceId.isBlank()) FirebaseInstanceId.getInstance().token else session.deviceId
                                        passParameters = Parameters()
                                        passParameters?.socialId = `object`.getString("id")
                                        passParameters?.deviceType = "A"
                                        passParameters?.email = `object`.getString("email")
                                        passParameters?.firstName = `object`.getString("first_name")
                                        passParameters?.lastName = `object`.getString("last_name")
                                        passParameters?.deviceId = if (session.deviceId.isBlank()) FirebaseInstanceId.getInstance().token else session.deviceId

                                        authViewModel.logIn(parameters)

                                    } catch (e: JSONException) {
                                        DebugLog.d("Hlink e" + e.message)
                                        e.printStackTrace()
                                    }
                                }
                                val parameter = Bundle()
                                parameter.putString("fields", "id,first_name,last_name,email")
                                request.parameters = parameter
                                request.executeAsync()
                                // App code
                            }

                            override fun onCancel() {
                                showLoader(false)
                            }

                            override fun onError(exception: FacebookException) {
                                showLoader(false)
                                DebugLog.d("exception = " + exception.message)

                            }
                        })
            }

            R.id.textViewGoogleLogin -> {
                showLoader(true)
                val signInIntent = mGoogleApiClient?.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }
        }
    }

    private fun isValidation(): Boolean {
        try {
            validator.submit(editTextFirstName, inputLayoutFirstName)
                    .checkEmpty().errorMessage(R.string.enter_first_name)
                    .matchPatter(ConstantApp.Validation.NAME_VALIDATION).errorMessage(R.string.enter_valid_name)
                    .check()

            validator.submit(editTextLastName, inputLayoutLastName)
                    .checkEmpty().errorMessage(R.string.enter_last_name)
                    .matchPatter(ConstantApp.Validation.NAME_VALIDATION).errorMessage(R.string.enter_valid_name)
                    .check()

            validator.submit(editTextEmail, inputLayoutEmail)
                    .checkEmpty().errorMessage(R.string.enter_email_address)
                    .matchPatter(Patterns.EMAIL_ADDRESS.pattern()).errorMessage(R.string.enter_valid_email)
                    .check()

            validator.submit(editTextCountryCode, inputLayoutCountryCode)
                    .checkEmpty().errorMessage(R.string.enter_country_code)
                    .check()

            validator.submit(editTextMobileNumber, inputLayoutMobileNumber)
                    .checkEmpty().errorMessage(R.string.enter_mobile_number)
                    .checkMinDigits(7).errorMessage(R.string.enter_valid_mobile_number)
                    .check()


            if (editTextPassword.isEnabled) {
                validator.submit(editTextPassword, inputLayoutPassword)
                        .checkEmpty().errorMessage(R.string.enter_password)
                        .matchPatter("^(?=.*\\d).{8,25}\$").errorMessage(R.string.enter_valid_password)
                        .check()

                validator.submit(editTextConfirmPassword, inputLayoutConfirmPassword)
                        .matchString(getText(editTextPassword)!!).errorMessage(R.string.password_not_match)
                        .check()
            }

            val inputFormatter: DateTimeFormatter =
                DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
            val date: LocalDate = LocalDate.parse(editTextDOB.text, inputFormatter)
            val date1: LocalDate = LocalDate.now()
            if (date.year >= date1.year - 12) {
                Log.d("JSR", "Your age must be bigger than 12")
                navigator.showErrorMessage("Your age must be bigger than 12")
                throw ApplicationException("Your age must be bigger than 12")
            }

            return true
        } catch (e: ApplicationException) {
            e.message
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("OnActivityResult ","True")

        callbackManager.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 10) {
                val getCode = data?.getParcelableExtra<Country>(ConstantApp.PassValue.COUNTRY_CODE)
                editTextCountryCode.setText("+" + getCode?.countryCode)
                editTextMobileNumber.clearText()
                editTextMobileNumber.hint = formatNumber(getHintText(getCode?.id, activity!!), getCode?.id!!)
                /*if (getCode.leadingDigits != null) {
                    editTextMobileNumber.setText(getCode.leadingDigits)
                    editTextMobileNumber.setSelection(editTextMobileNumber.text?.length!!)
                }*/

                selectedCountryId = getCode.id!!
                /*editTextMobileNumber.removeTextChangedListener(mPhoneNumberWatcher)
                mPhoneNumberWatcher = PhoneNumberFormattingTextWatcher(selectedCountryId)
                editTextMobileNumber.addTextChangedListener(mPhoneNumberWatcher)*/

                setPhoneNumberFormat(getCode.id!!)
            } else if (requestCode == RC_SIGN_IN) {
                /*val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
                handleSignInResult(result)*/

                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                handleSignInResult(task)
            }
        }else{
            showLoader(false)
        }
    }

    private fun handleSignInResult(result: Task<GoogleSignInAccount>) {

        try {
            val acct = result.getResult(ApiException::class.java)
            inputLayoutPassword.viewGone()
            inputLayoutConfirmPassword.viewGone()
            editTextPassword.isEnabled = false

            isLoginType = ConstantApp.GOOGLE
            parameters.socialId = acct?.id
            parameters.deviceType = "A"
            parameters.countryShortCode = selectedCountryId
            parametes?.deviceId = if (session.deviceId.isBlank()) FirebaseInstanceId.getInstance().token else session.deviceId
            passParameters = Parameters()
            passParameters?.deviceId = if (session.deviceId.isBlank()) FirebaseInstanceId.getInstance().token else session.deviceId
            passParameters?.socialId = acct?.id
            passParameters?.deviceType = "A"
            passParameters?.email = acct?.email
            passParameters?.firstName = acct?.givenName
            passParameters?.lastName = acct?.familyName

            authViewModel.logIn(parameters)
        }catch (e: ApiException) {
            e.printStackTrace()
            showLoader(false)
        }
       /* if (result != null) {
            if (result.isSuccess) {
                inputLayoutPassword.viewGone()
                inputLayoutConfirmPassword.viewGone()
                editTextPassword.isEnabled = false

                val acct = result.signInAccount
                isLoginType = ConstantApp.GOOGLE
                parameters.socialId = acct?.id
                parameters.deviceType = "A"
                parameters.countryShortCode = selectedCountryId
                parametes?.deviceId = if (session.deviceId.isBlank()) FirebaseInstanceId.getInstance().token else session.deviceId
                passParameters = Parameters()
                passParameters?.deviceId = if (session.deviceId.isBlank()) FirebaseInstanceId.getInstance().token else session.deviceId
                passParameters?.socialId = acct?.id
                passParameters?.deviceType = "A"
                passParameters?.email = acct?.email
                passParameters?.firstName = acct?.givenName
                passParameters?.lastName = acct?.familyName

                authViewModel.logIn(parameters)
            } else {
                showLoader(false)
            }
        }*/
    }


    override fun onConnectionFailed(p0: ConnectionResult) {

    }
}