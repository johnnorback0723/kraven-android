package com.kraven.extensions

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.kraven.ui.base.BaseFragment


inline fun <reified T : Any> Fragment.extraNotNull(key: String, default: T? = null) = lazy {
    val value = arguments?.get(key) ?: throw IllegalArgumentException("Missing Argument")
    requireNotNull(if (value is T) value else default) { key }
}

inline fun <reified T : ViewModel> BaseFragment.getViewModel() :Lazy<T> = lazy {
    androidx.lifecycle.ViewModelProvider(this, viewModelFactory)[T::class.java]
}