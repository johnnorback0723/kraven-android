package com.kraven.ui.authentication.fragement


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.core.os.bundleOf
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
import com.google.android.gms.tasks.Task
import com.google.firebase.iid.FirebaseInstanceId
import com.kraven.R
import com.kraven.core.StatusCode
import com.kraven.core.Validator
import com.kraven.data.pojo.Parameters
import com.kraven.data.pojo.User
import com.kraven.di.component.FragmentComponent
import com.kraven.exception.ApplicationException
import com.kraven.extensions.getText
import com.kraven.ui.activity.HomeActivity
import com.kraven.ui.address.fragment.AddressFragment
import com.kraven.ui.authentication.viewmodel.AuthViewModel
import com.kraven.ui.base.BaseFragment
import com.kraven.utils.ConstantApp
import com.kraven.utils.DebugLog
import kotlinx.android.synthetic.main.login_fragment_dummy.*
import org.json.JSONException
import javax.inject.Inject


class LoginFragment : BaseFragment(), View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    @Inject
    lateinit var validator: Validator

    @Inject
    lateinit var parameters: Parameters

    var passParameters = Parameters()

    private lateinit var authViewModel: AuthViewModel
    private lateinit var callbackManager: CallbackManager
    var mGoogleApiClient: GoogleSignInClient? = null

    private var RC_SIGN_IN = 1
    private var isLoginType: String? = null

    override fun createLayout(): Int = R.layout.login_fragment_dummy

    override fun inject(fragmentComponent: FragmentComponent) {
        fragmentComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        callbackManager = CallbackManager.Factory.create()

        authViewModel = ViewModelProviders.of(this, viewModelFactory)[AuthViewModel::class.java]

        authViewModel.signUpLogInLiveData.observe(this,
                { responseBody ->
                    showLoader(false)
                    when (responseBody.code) {
                        StatusCode.CODE_SUCCESS ->{
                            if (responseBody.data!!.islandId != null && responseBody.data.islandId?.isNotEmpty()!! && responseBody.data.islandId != "0") {
                                navigator.loadActivity(HomeActivity::class.java).byFinishingAll().start()
                            }else {
                                navigator.load(SelectIslandFragment::class.java).setBundle(bundleOf(ConstantApp.PassValue.NEW_ISLAND to true)).replace(false)
                            }
                        }

                        StatusCode.CODE_ADD_ADDRESS -> navigator.load(AddressFragment::class.java).setBundle(Bundle().apply {
                            putBoolean(ConstantApp.PassValue.IS_CART, false)
                        }).replace(true)
                        StatusCode.CODE_SOCIAL_ID_NOT_REGISTER ->
                            navigator.load(SignUpFragment::class.java).setBundle(Bundle().apply
                            {
                                putParcelable(ConstantApp.PassValue.PARAMETERS, passParameters)
                                putString(ConstantApp.PassValue.IS_LOGIN_TYPE, isLoginType)
                            }).replace(true)
                        StatusCode.CODE_INVALID_REQUEST -> {
                            if (responseBody.message.contains("You've tried too many attempts")) {
                                commandDialog(getString(R.string.app_name), responseBody.message,
                                        object : DialogInterface {
                                            override fun onClickOk() {

                                            }
                                        })
                            } else {
                                showMessage(responseBody.message)
                            }
                        }
                        else -> {
                            showMessage(responseBody.message)
                        }
                    }
                }
                ,
                { true })
    }

    override fun onResume() {
        super.onResume()

        try {
            LoginManager.getInstance().logOut()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        Log.d("JSR", "GoogleSignInOptions Builder  - 1")
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //.requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()


        mGoogleApiClient = GoogleSignIn.getClient(activity!!, gso)

        mGoogleApiClient?.signOut()?.addOnCompleteListener {

        }


    }

    override fun bindData() {
        authViewModel = ViewModelProviders.of(this, viewModelFactory)[AuthViewModel::class.java]

        toolbar.showToolbar(false)
        toolbar.setDrawerLock(true)

        /*TextDecorator.decorate(textViewDontHaveAccount, resources.getString(R.string.dont_acccount))
                .apply {
                    setTypeface(R.font.work_sans_bold, getString(R.string.sign_up))
                    setTextColor(android.R.color.white, getString(R.string.sign_up))
                    makeTextClickable(object : ClickableSpan() {
                        override fun updateDrawState(ds: TextPaint) {
                            ds.isUnderlineText = true
                        }

                        override fun onClick(widget: View) {
                            navigator.load(SignUpFragment::class.java).replace(true)
                        }

                    }, getString(R.string.sign_up))
                }
                .build()*/

        buttonLogin.setOnClickListener(this)
        textViewForgotPassword.setOnClickListener(this)
        textViewFacebookLogin.setOnClickListener(this)
        textViewGoogleLogin.setOnClickListener(this)
        buttonSignUp.setOnClickListener(this)

    }

    override fun onClick(v: View?) {

        when (v!!.id) {
            R.id.buttonSignUp -> {
                navigator.load(SignUpFragment::class.java).replace(true)
            }
            R.id.textViewForgotPassword -> {
                navigator.load(ForgotPasswordFragment::class.java).replace(true)
            }
            R.id.buttonLogin -> {

                if (isValidation()) {
                    if (session.isProtoType) {
                        navigator.loadActivity(HomeActivity::class.java).byFinishingAll().start()
                        val user = User()
                        user.email = getText(editTextEmail)!!
                        session.user = user
                        session.userSession = "123456"
                        session.setAddressCode = "1"
                    } else {
                        showLoader(true)
                        parameters = Parameters()
                        parameters.email = getText(editTextEmail)!!
                        parameters.password = getText(editTextPassword)!!
                        parameters.deviceType = "A"
                        parameters.deviceId = if (session.deviceId.isBlank()) FirebaseInstanceId.getInstance().token else session.deviceId
                        authViewModel.logIn(parameters)
                    }
                }
            }

            R.id.textViewFacebookLogin -> {
                showLoader(true)
                LoginManager.getInstance().logInWithReadPermissions(this, listOf("email") as Collection<String>?)

                LoginManager.getInstance().registerCallback(callbackManager,
                        object : FacebookCallback<LoginResult> {
                            override fun onSuccess(loginResult: LoginResult) {
                                val request = GraphRequest.newMeRequest(
                                    loginResult.accessToken
                                ) { `object`, response ->
                                    try {
                                        Log.d("JSR", "${`object`}")
                                        isLoginType = ConstantApp.FACEBOOK
                                        parameters.socialId = `object`.getString("id")
                                        parameters.deviceType = "A"
                                        parameters.email = `object`.getString("email")
                                        parameters.firstName = `object`.getString("first_name")
                                        parameters.lastName = `object`.getString("last_name")
                                        parameters.deviceId = if (session.deviceId.isBlank()) FirebaseInstanceId.getInstance().token else session.deviceId

                                        passParameters.socialId = `object`.getString("id")
                                        passParameters.deviceType = "A"
                                        passParameters.email = `object`.getString("email")
                                        passParameters.firstName = `object`.getString("first_name")
                                        passParameters.lastName = `object`.getString("last_name")
                                        passParameters.deviceId = if (session.deviceId.isBlank()) FirebaseInstanceId.getInstance().token else session.deviceId


                                        authViewModel.logIn(parameters)

                                    } catch (e: JSONException) {
                                        DebugLog.d("Hlink e" + e.message)
                                        e.printStackTrace()
                                    }
                                }
                                val parameters = Bundle()
                                parameters.putString("fields", "id,name,email")
                                request.parameters = parameters
                                request.executeAsync()

                                /* val request = GraphRequest.newMeRequest(
                                        loginResult.accessToken
                                ) { `object`, response ->
                                    try {
                                        Log.d("JSR", "${`object`}")
                                        isLoginType = ConstantApp.FACEBOOK
                                        parameters.socialId = `object`.getString("id")
                                        parameters.deviceType = "A"
                                        parameters.email = `object`.getString("email")
                                        parameters.firstName = `object`.getString("first_name")
                                        parameters.lastName = `object`.getString("last_name")
                                        parameters.deviceId = if (session.deviceId.isBlank()) FirebaseInstanceId.getInstance().token else session.deviceId

                                        passParameters.socialId = `object`.getString("id")
                                        passParameters.deviceType = "A"
                                        passParameters.email = `object`.getString("email")
                                        passParameters.firstName = `object`.getString("first_name")
                                        passParameters.lastName = `object`.getString("last_name")
                                        passParameters.deviceId = if (session.deviceId.isBlank()) FirebaseInstanceId.getInstance().token else session.deviceId


                                        authViewModel.logIn(parameters)

                                    } catch (e: JSONException) {
                                        DebugLog.d("Hlink e" + e.message)
                                        e.printStackTrace()
                                    }
                                } */
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
            validator.submit(editTextEmail, inputLayoutEmail)
                    .checkEmpty().errorMessage(R.string.enter_email_address)
                    .matchPatter(Patterns.EMAIL_ADDRESS.pattern()).errorMessage(R.string.enter_valid_email)
                    .check()

            validator.submit(editTextPassword, inputLayoutPassword)
                    .checkEmpty().errorMessage(R.string.enter_password)
                    .check()

            return true
        } catch (e: ApplicationException) {
            e.message
        }
        return false
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {


            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)

            /*val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            handleSignInResult(result)*/
        }
    }

    private fun handleSignInResult(result: Task<GoogleSignInAccount>) {

        try {
            var acct = result.getResult(ApiException::class.java)

            //val acct = result.signInAccount
            isLoginType = ConstantApp.GOOGLE
            parameters.socialId = acct?.id

            Log.d("Hlink", "acctdisplayName${acct?.displayName}")
            Log.d("Hlink", "acctdisplayName${acct?.givenName}")

            parameters.deviceType = "A"
            parameters.email = acct?.email
            parameters.firstName = acct?.givenName
            parameters.lastName = acct?.familyName
            parameters.deviceId = if (session.deviceId.isBlank()) FirebaseInstanceId.getInstance().token else session.deviceId
            passParameters.socialId = acct?.id
            passParameters.deviceType = "A"
            passParameters.email = acct?.email
            passParameters.firstName = acct?.givenName
            passParameters.lastName = acct?.familyName
            passParameters.deviceId = if (session.deviceId.isBlank()) FirebaseInstanceId.getInstance().token else session.deviceId
            authViewModel.logIn(parameters)


        } catch (e: ApiException) {
            e.printStackTrace()
            showLoader(false)
        }

        /*if (result != null) {
            if (result.isSuccess) {
                val acct = result.signInAccount
                isLoginType = ConstantApp.GOOGLE
                parameters.socialId = acct?.id

                Log.d("Hlink","acctdisplayName${acct?.displayName}")
                Log.d("Hlink","acctdisplayName${acct?.givenName}")

                parameters.deviceType = "A"
                parameters.email = acct?.email
                parameters.firstName = acct?.givenName
                parameters.lastName = acct?.familyName
                parameters.deviceId = if (session.deviceId.isBlank()) FirebaseInstanceId.getInstance().token else session.deviceId
                passParameters.socialId = acct?.id
                passParameters.deviceType = "A"
                passParameters.email = acct?.email
                passParameters.firstName = acct?.givenName
                passParameters.lastName = acct?.familyName
                passParameters.deviceId = if (session.deviceId.isBlank()) FirebaseInstanceId.getInstance().token else session.deviceId
                authViewModel.logIn(parameters)
            } else {
                showLoader(false)
            }
        }*/
    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }



}