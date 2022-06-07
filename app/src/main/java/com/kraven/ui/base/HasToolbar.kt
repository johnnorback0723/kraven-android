package com.kraven.ui.base

import androidx.annotation.ColorRes
import androidx.annotation.NonNull
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar

/**
 * Created by hlink21 on 20/12/16.
 */

interface HasToolbar {

     fun setToolbar(toolbar: Toolbar)

     fun showToolbar(isVisible: Boolean)

     fun showToolbarElevation(isVisible: Boolean)

     fun setToolbarTitle(@NonNull title: String)

     fun setToolbarButton(isBack: Boolean)

     fun setButtonVisibility(isVisible: Boolean)

     fun setToolbarColor(@ColorRes color: Int)

     fun setToolbarTextColorWhite(isWhite: Boolean)

     fun setToolbarTextColorWhiteClose(isWhite: Boolean)

     fun setToolbarProfile(isProfile: Boolean)

     fun setToolbarExpanded(isExp: Boolean, isExpt: Boolean)

     fun showAppBar(isVisible: Boolean)

     fun setDrawerLock(isLock: Boolean)

     fun onError(throwable: Throwable)

     fun setButtonTextVisibility(isVisible: Boolean)
}
