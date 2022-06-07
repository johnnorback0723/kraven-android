package com.kraven.ui.authentication.fragement


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
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
import javax.inject.Inject


class VerificationFragmentEdit : BaseFragment(), View.OnClickListener {

    @Inject
    lateinit var validator: Validator

    private var otp: String? = null
    private var countryCode: String? = null
    private var phone: String? = null


    private lateinit var authViewModel: AuthViewModel

    override fun createLayout(): Int = R.layout.verification_framgnet

    override fun inject(fragmentComponent: FragmentComponent) {
        fragmentComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            otp = arguments?.getString("otp")!!
            countryCode = arguments!!.getString("countryCode")
            phone = arguments!!.getString("phone")
        }

        authViewModel = ViewModelProviders.of(this, viewModelFactory)[AuthViewModel::class.java]


        authViewModel.verifyOtpLiveData.observe(this,
                { responseBody ->
                    showLoader(false)
                    //Toast.makeText(activity, responseBody.data?.otp, Toast.LENGTH_SHORT).show()
                    showMessage(responseBody.message)
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
                                val parameters = Parameters()
                                parameters.countryCode = countryCode
                                parameters.phone = phone
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
                        if (otp.equals(getText(editTextCode))) {
                            showLoader(true)
                            val resultIntent = Intent()
                            activity!!.setResult(Activity.RESULT_OK, resultIntent)
                            navigator.finish()
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