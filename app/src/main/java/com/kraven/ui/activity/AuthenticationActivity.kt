package com.kraven.ui.activity


import android.os.Bundle
import androidx.core.os.bundleOf
import com.eggheadgames.siren.*
import com.kraven.R
import com.kraven.data.URLFactory
import com.kraven.di.component.ActivityComponent
import com.kraven.extensions.viewGone
import com.kraven.ui.authentication.fragement.SelectIslandFragment
import com.kraven.ui.authentication.fragement.SplashFragment
import com.kraven.ui.base.BaseActivity
import com.kraven.utils.ConstantApp


class AuthenticationActivity : BaseActivity() {


    override fun findFragmentPlaceHolder(): Int = R.id.mainContainer

    override fun findContentView(): Int = R.layout.activity_authentication

    override fun inject(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        load(SplashFragment::class.java).replace(false)
        //load(SelectIslandFragment::class.java).setBundle(bundleOf(ConstantApp.PassValue.NEW_ISLAND to true)).replace(false)

    }


}