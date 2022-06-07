package com.kraven.ui.manager

import com.kraven.ui.base.BaseFragment


object FragmentFactory {

    fun <T : BaseFragment> getFragment(aClass: Class<T>): T? {

        try {
            return aClass.newInstance()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

        return null
    }
}
