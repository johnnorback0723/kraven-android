package com.kraven.ui.authentication.fragement

import android.os.Bundle
import android.util.Patterns
import androidx.lifecycle.ViewModelProviders
import com.kraven.R
import com.kraven.core.StatusCode
import com.kraven.core.Validator
import com.kraven.data.pojo.Parameters
import com.kraven.di.component.FragmentComponent
import com.kraven.exception.ApplicationException
import com.kraven.extensions.getText
import com.kraven.ui.authentication.viewmodel.AuthViewModel
import com.kraven.ui.base.BaseFragment
import kotlinx.android.synthetic.main.forgot_paswword_fragment.*
import javax.inject.Inject

class ForgotPasswordFragment : BaseFragment() {

    @Inject
    lateinit var validator: Validator

    @Inject
    lateinit var parameters: Parameters

    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authViewModel = ViewModelProviders.of(this, viewModelFactory)[AuthViewModel::class.java]

        authViewModel.liveData.observe(this,
                { responseBody ->
                    showLoader(false)
                    showMessage(responseBody.message)
                    if (responseBody.code == StatusCode.CODE_SUCCESS) {
                        editTextEmail.setText("")
                    }
                }
                , { true })
    }


    override fun createLayout(): Int = R.layout.forgot_paswword_fragment

    override fun inject(fragmentComponent: FragmentComponent) {
        fragmentComponent.inject(this)
    }

    override fun bindData() {
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle("")
        toolbar.setToolbarButton(true)
        toolbar.setDrawerLock(true)
        toolbar.setButtonTextVisibility(false)


        buttonSend.setOnClickListener {
            try {
                validator.submit(editTextEmail, inputLayoutEmail)
                        .checkEmpty().errorMessage(R.string.enter_email_address)
                        .matchPatter(Patterns.EMAIL_ADDRESS.pattern()).errorMessage(R.string.enter_valid_email)
                        .check()

                if (session.isProtoType) {
                    navigator.goBack()
                } else {
                    showLoader(true)
                    parameters.email = getText(editTextEmail)
                    authViewModel.forgotPassword(parameters)
                    hideKeyBoard()
                }


            } catch (e: ApplicationException) {
                e.message
            }

        }
    }
}