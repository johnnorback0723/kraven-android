package com.kraven.ui.setting.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.lifecycle.ViewModelProviders
import com.kraven.R
import com.kraven.core.StatusCode
import com.kraven.core.Validator
import com.kraven.data.pojo.Parameters
import com.kraven.di.component.FragmentComponent
import com.kraven.exception.ApplicationException
import com.kraven.extensions.*
import com.kraven.ui.activity.IsolatedActivity
import com.kraven.ui.authentication.fragement.CountryCodeFragment
import com.kraven.ui.authentication.model.Country
import com.kraven.ui.authentication.viewmodel.AuthViewModel
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.home.fragment.HomeNewFragment
import com.kraven.utils.ConstantApp

import kotlinx.android.synthetic.main.support_us_fragment.*
import java.util.*
import javax.inject.Inject


class SupportUsFragment : BaseFragment() {

    private val COUNTRY_CODE_REQUEST = 2

    @Inject
    lateinit var validator: Validator

    @Inject
    lateinit var parameters: Parameters

    private lateinit var authViewModel: AuthViewModel
    private var selectedCountryId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authViewModel = ViewModelProviders.of(this, viewModelFactory)[AuthViewModel::class.java]

        authViewModel.liveData.observe(this,
                { responseBody ->
                    showLoader(false)
                    showMessage(responseBody.message)
                    if (responseBody.code == StatusCode.CODE_SUCCESS) {
                        editTextTitle.text!!.clear()
                        editTextMessage.text!!.clear()
                    }
                }
                , { true })
    }

    override fun createLayout(): Int = R.layout.support_us_fragment

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun bindData() {
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle(getString(R.string.contact_us))
        toolbar.setToolbarTextColorWhite(false)
        toolbar.setButtonTextVisibility(false)

        editTextName.setText(session.user?.firstName + " " + session.user?.lastName)
        editTextEmail.setText(session.user?.email)
        editTextTitle.setOnClickListener {
            navigator.loadActivity(IsolatedActivity::class.java).setPage(SelectTitleFragment::class.java).forResult(101).start()
        }

        buttonSend.setOnClickListener {
            if (isValidation()) {
                if (session.isProtoType) {
                    showMessage(getString(R.string.request_successfully_sent))
                    editTextEmail.text!!.clear()
                    editTextTitle.text!!.clear()
                    editTextMessage.text!!.clear()
                } else {
                    showLoader(true)
                    parameters.name = getText(editTextName)
                    parameters.title = getText(editTextTitle)
                    parameters.email = getText(editTextEmail)
                    parameters.message = getText(editTextMessage)

                    if (getText(editTextMobileNumber)?.startsWith(getLeadingDigits(selectedCountryId, activity!!))!!) {
                        parameters.phone = getCountryCodeDial(selectedCountryId, activity!!) + getText(editTextMobileNumber)?.replace(getLeadingDigits(selectedCountryId, activity!!), "")?.replace("-", "")?.replace(" ", "")
                    } else {
                        parameters.phone = getCountryCodeDial(selectedCountryId, activity!!) + getText(editTextMobileNumber)?.replace("-", "")?.replace(" ", "")
                    }

                    authViewModel.contactUs(parameters)
                }
            }
        }
        editTextCountryCode.setOnClickListener {
            navigator.loadActivity(IsolatedActivity::class.java).setPage(CountryCodeFragment::class.java).forResult(COUNTRY_CODE_REQUEST).start()
        }
        selectedCountryId = if(session.user?.countryShortCode!=null && session.user?.countryShortCode!!.isNotEmpty())session.user?.countryShortCode else  Locale.getDefault().country
        editTextCountryCode.setText(getCountryCode(selectedCountryId, activity!!))

        if (session.user?.phone != null && session.user?.phone!!.isNotEmpty()) {
            editTextMobileNumber.setText(formatNumber(session.user?.phone!!, selectedCountryId!!))
        } else {
            editTextMobileNumber.hint = formatNumber(getHintText(selectedCountryId, activity!!), selectedCountryId!!)
        }
        editTextMobileNumber.addTextChangedListener(setPhoneNumberFormat(selectedCountryId!!))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == 101) {
            editTextTitle.setText(data!!.getStringExtra("title"))
        } else if (requestCode == COUNTRY_CODE_REQUEST) {

            if(data?.getParcelableExtra<Country>(ConstantApp.PassValue.COUNTRY_CODE)!=null){
                val getCode = data.getParcelableExtra<Country>(ConstantApp.PassValue.COUNTRY_CODE)
                editTextCountryCode.setText(
                        "+" + getCode?.countryCode)
                editTextMobileNumber.clearText()
                editTextMobileNumber.hint = formatNumber(getHintText(getCode?.id, activity!!), getCode?.id!!)
                if (getCode.leadingDigits != null) {
                    editTextMobileNumber.setText(getCode.leadingDigits)
                    editTextMobileNumber.setSelection(editTextMobileNumber.text?.length!!)
                }
                selectedCountryId = getCode.id!!
                setPhoneNumberFormat(getCode.id!!)
            }

        }

    }

    private fun isValidation(): Boolean {
        try {


            validator.submit(editTextName, inputLayoutName)
                    .checkEmpty().errorMessage(R.string.enter_name)
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

            validator.submit(editTextTitle, inputLayoutTitle)
                    .checkEmpty().errorMessage(R.string.enter_title)
                    .check()

            validator.submit(editTextMessage, inputLayoutMessage)
                    .checkEmpty().errorMessage(R.string.enter_message)
                    .check()

            return true

        } catch (e: ApplicationException) {
            e.message
        }
        return false
    }

    override fun onBackActionPerform(): Boolean {
        navigator.load(HomeNewFragment::class.java).clearHistory(null).replace(false)
        return false
    }
}