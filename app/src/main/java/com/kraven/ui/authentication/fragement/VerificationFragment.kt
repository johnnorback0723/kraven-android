package com.kraven.ui.authentication.fragement


import android.R.attr.data
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProviders
import com.facebook.FacebookSdk.getApplicationContext
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.firebase.iid.FirebaseInstanceId
import com.kraven.R
import com.kraven.core.StatusCode
import com.kraven.core.Validator
import com.kraven.data.pojo.Parameters
import com.kraven.di.component.FragmentComponent
import com.kraven.exception.ApplicationException
import com.kraven.extensions.getText
import com.kraven.ui.address.fragment.AddressFragment
import com.kraven.ui.authentication.viewmodel.AuthViewModel
import com.kraven.ui.base.BaseFragment
import com.kraven.utils.ConstantApp
import com.kraven.utils.TextDecorator
import kotlinx.android.synthetic.main.verification_framgnet.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject


class VerificationFragment : BaseFragment(), View.OnClickListener{

    @Inject
    lateinit var validator: Validator
    var mSmsBroadcastReceiver: MySMSBroadcastReceiver? = null
    private val REQ_USER_CONSENT = 2

    lateinit var parameters: Parameters

    private lateinit var authViewModel: AuthViewModel

    override fun createLayout(): Int = R.layout.verification_framgnet

    override fun inject(fragmentComponent: FragmentComponent) {
        fragmentComponent.inject(this)
    }

    private fun startSmsUserConsent() {
        val client = SmsRetriever.getClient(requireContext())
        //We can add sender phone number or leave it blank
        // I'm adding null here
        client.startSmsUserConsent(null).addOnSuccessListener { Toast.makeText(getApplicationContext(), "On Success", Toast.LENGTH_LONG).show() }.addOnFailureListener { Toast.makeText(getApplicationContext(), "On OnFailure", Toast.LENGTH_LONG).show() }
    }
     override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_USER_CONSENT) {
            if (resultCode == RESULT_OK && data != null) {
                //That gives all message to us.
                // We need to get the code from inside with regex
                val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show()
                //textViewMessage.setText(String.format("%s - %s", getString(R.string.received_message), message))
                if (message != null) {
                    getOtpFromMessage(message)
                }
            }
        }
    }

     override fun onStart() {
        super.onStart()
         registerBroadcastReceiver()
    }

    override fun onStop() {
        super.onStop()
        requireContext().unregisterReceiver(mSmsBroadcastReceiver)
    }
    override fun onResume() {
        super.onResume()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(intent: Intent) {
        startActivityForResult(intent, REQ_USER_CONSENT)
    }
    private fun registerBroadcastReceiver() {
        mSmsBroadcastReceiver = MySMSBroadcastReceiver()
       /* mSmsBroadcastReceiver!!.smsBroadcastReceiverListener = object : MySMSBroadcastReceiver.SmsBroadcastReceiverListener {
            override  fun onSuccess(intent: Intent?) {
                startActivityForResult(intent, REQ_USER_CONSENT)
            }

            override fun onFailure() {}
        }
*/
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        requireActivity().registerReceiver(mSmsBroadcastReceiver, intentFilter)
    }

    private fun getOtpFromMessage(message: String) {
        // This will match any 6 digit number in the message
        val pattern = Pattern.compile("(\\d{4})")
        val matcher = pattern.matcher(message)
        if (matcher.find()) {
            editTextCode.setText(matcher.group(0))
        }
       /* val pattern: Pattern = Pattern.compile("(|^)\\d{6}")
        val matcher: Matcher = pattern.matcher(message)
        if (matcher.find()) {
            editTextCode.setText(matcher.group(0))
        }*/
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            parameters = arguments?.getParcelable(ConstantApp.PassValue.PARAMETERS)!!
        }
        startSmsUserConsent();


        authViewModel = ViewModelProviders.of(this, viewModelFactory)[AuthViewModel::class.java]

        authViewModel.signUpLogInLiveData.observe(this,
                { responseBody ->
                    showLoader(false)
                    if (responseBody.code == StatusCode.CODE_SUCCESS) {
                        navigator.load(SelectIslandFragment::class.java).setBundle(bundleOf(ConstantApp.PassValue.NEW_ISLAND to true)).replace(false)
                        //navigator.loadActivity(HomeActivity::class.java).byFinishingAll().start()
                        /* navigator.load(AddressFragment::class.java).setBundle(Bundle().apply {
                             putBoolean(ConstantApp.PassValue.IS_CART, false)
                             putBoolean("signUp", true)
                         }).replace(true)*/
                    }
                }
                , { true })

        authViewModel.verifyOtpLiveData.observe(this,

                { responseBody ->
                    showLoader(false)
                    when (responseBody.code) {
                        StatusCode.CODE_SUCCESS -> {
                            showMessage(responseBody.message)
                        }
                        else -> showMessage(responseBody.message)
                    }
                }
                , { true })
    }

    override fun bindData() {

        toolbar.showToolbar(true)
        toolbar.setToolbarTitle("")
        toolbar.setToolbarButton(true)
        buttonSend.setOnClickListener(this)
        toolbar.setButtonTextVisibility(false)

        TextDecorator.decorate(textViewNotReceived, getString(R.string.not_received))
                .apply {
                    setTypeface(R.font.work_sans_bold, 22, finalLength)
                    setTextColor(R.color.light_blue)
                    makeTextClickable(object : ClickableSpan() {
                        override fun updateDrawState(ds: TextPaint) {
                            ds.isUnderlineText = false
                        }

                        override fun onClick(textView: View) {
                            if (session.isProtoType) {
                                showMessage("Otp send successfully")
                            } else {
                                showLoader(true)
                                authViewModel.verifyOtp(parameters)
                            }
                        }

                    }, "Resend")
                }
                .build()
    }

    override fun onClick(v: View?) {
        hideKeyBoard()
        when (v?.id) {
            R.id.buttonSend -> {
                try {
                    validator.submit(editTextCode, inputLayoutCode)
                            .checkEmpty().errorMessage(R.string.enter_code)
                            .check()

                    if (session.isProtoType) {
                        navigator.load(AddressFragment::class.java).setBundle(Bundle().apply {
                            putBoolean(ConstantApp.PassValue.IS_CART, false)
                        }).replace(true)
                    } else {
                        if (parameters.otp.equals(getText(editTextCode))) {
                            showLoader(true)
                            val parametesr = Parameters()
                            parametesr.firstName = parameters.firstName
                            parametesr.lastName = parameters.lastName
                            parametesr.email = parameters.email
                            parametesr.gender = parameters.gender
                            parametesr.dob = parameters.dob
                            parametesr.countryShortCode = parameters.countryShortCode
                            if (parameters.signupType.equals("Normal")) {
                                parametesr.password = parameters.password
                            }
                            parametesr.countryCode = parameters.countryCode
                            parametesr.phone = parameters.phone
                            parametesr.latitude = parameters.latitude
                            parametesr.longitude = parameters.longitude
                            parametesr.deviceType = "A"
                            if (parameters.referralCode!!.isNotEmpty()) {
                                parametesr.referralCode = parameters.referralCode
                            }
                            parametesr.socialId = parameters.socialId
                            parametesr.signupType = parameters.signupType
                            parametesr.deviceId = if (session.deviceId.isBlank()) FirebaseInstanceId.getInstance().token else session.deviceId

                            authViewModel.signUp(parametesr)
                        } else {
                            showMessage(getString(R.string.enter_valid_otp))
                        }
                    }

                } catch (e: ApplicationException) {
                    e.message
                }
            }
        }
    }


}