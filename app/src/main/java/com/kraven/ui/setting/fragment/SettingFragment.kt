package com.kraven.ui.setting.fragment

import android.os.Bundle
import com.kraven.R
import com.kraven.di.component.FragmentComponent
import com.kraven.ui.authentication.fragement.TermsAndConditionsFragment
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.home.fragment.HomeNewFragment
import com.kraven.utils.ConstantApp
import kotlinx.android.synthetic.main.setting_fragment.*

class SettingFragment : BaseFragment() {
    override fun createLayout(): Int = R.layout.setting_fragment

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun bindData() {
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle(getString(R.string.support))
        toolbar.setToolbarTextColorWhite(false)
        toolbar.setButtonTextVisibility(false)

        textViewSupport.setOnClickListener {
            navigator.load(SupportUsFragment::class.java).replace(true)
        }

        textViewFaq.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(ConstantApp.PassValue.TermsLink, getString(R.string.faqs_customer))
            bundle.putString(ConstantApp.PassValue.TermsHeder, getString(R.string.faq_s))
            navigator.load(TermsAndConditionsFragment::class.java).setBundle(bundle).replace(true)
        }

        textViewCondition.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(ConstantApp.PassValue.TermsLink, getString(R.string.terms_and_conditions_customer))
            bundle.putString(ConstantApp.PassValue.TermsHeder, getString(R.string.terms_condition))
            navigator.load(TermsAndConditionsFragment::class.java).setBundle(bundle).replace(true)

        }

        textViewPolicy.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(ConstantApp.PassValue.TermsLink, getString(R.string.privacy_policy_customer))
            bundle.putString(ConstantApp.PassValue.TermsHeder, getString(R.string.privacy_policy))
            navigator.load(TermsAndConditionsFragment::class.java).setBundle(bundle).replace(true)
        }
    }

    override fun onBackActionPerform(): Boolean {
        navigator.load(HomeNewFragment::class.java).clearHistory(null).replace(false)
        return false
    }
}