package com.kraven.ui.edit.profile.fragment

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.kraven.R
import com.kraven.core.StatusCode
import com.kraven.core.Validator
import com.kraven.data.pojo.Parameters
import com.kraven.di.component.FragmentComponent
import com.kraven.exception.ApplicationException
import com.kraven.extensions.getText
import com.kraven.ui.authentication.fragement.ForgotPasswordFragment
import com.kraven.ui.authentication.viewmodel.AuthViewModel
import com.kraven.ui.base.BaseFragment
import kotlinx.android.synthetic.main.change_password_fragment.*
import javax.inject.Inject

class ChangePasswordFragment : BaseFragment() {

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
                        editTextOldPassword.setText("")
                        editTextNewPassword.setText("")
                        editTextConfirmPassword.setText("")
                    }
                }
                , { true })
    }

    override fun createLayout(): Int = R.layout.change_password_fragment

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun bindData() {
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle("")
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)

        textViewForgotPassword.setOnClickListener {
            navigator.load(ForgotPasswordFragment::class.java).replace(true)
        }

        buttonUpdate.setOnClickListener {
            try {
                validator.submit(editTextOldPassword, inputLayoutOldPassword)
                        .checkEmpty().errorMessage(R.string.please_enter_old_password)
                        .check()

                validator.submit(editTextNewPassword, inputLayoutNewPassword)
                        .checkEmpty().errorMessage(R.string.please_enter_new_password)
                        .matchPatter("^(?=.*\\d).{8,25}\$").errorMessage(R.string.enter_valid_password)
                        .check()

                validator.submit(editTextConfirmPassword, inputLayoutConfirmPassword)
                        .matchString(getText(editTextNewPassword)!!).errorMessage(R.string.password_not_match)
                        .checkEmpty().errorMessage(R.string.please_enter_confirm_password)
                        .check()

                if (session.isProtoType) {
                    showMessage(getString(R.string.password_successfully_changed))
                } else {
                    showLoader(true)
                    parameters.oldPassword = getText(editTextOldPassword)
                    parameters.newPassword = getText(editTextNewPassword)
                    authViewModel.changePassword(parameters)
                    hideKeyBoard()
                }

            } catch (e: ApplicationException) {
                e.message
            }
        }
    }
}