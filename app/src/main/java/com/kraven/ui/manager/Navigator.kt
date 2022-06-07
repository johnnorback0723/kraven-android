package com.kraven.ui.manager

import com.kraven.ui.base.BaseActivity
import com.kraven.ui.base.BaseFragment


/**
 * Created by hlink21 on 29/5/17.
 */

interface Navigator {
    fun <T : BaseFragment> load(tClass: Class<T>): FragmentActionPerformer<T>

    fun loadActivity(aClass: Class<out BaseActivity>): ActivityBuilder

    fun <T : BaseFragment> loadActivity(aClass: Class<out BaseActivity>, pageTClass: Class<T>): ActivityBuilder

    fun showErrorMessage(message: String)

    fun toggleLoader(show: Boolean,message:String)

    fun goBack()

    fun finish()
}
