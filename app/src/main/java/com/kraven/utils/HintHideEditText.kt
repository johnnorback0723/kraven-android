package com.kraven.utils

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.widget.EditText

import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatEditText

class HintHideEditText : AppCompatEditText {

    private var savedHint: CharSequence? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        validateHint(focused)
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
    }

    fun getSavedHint(): CharSequence? {
        return savedHint
    }

    fun setSavedHint(savedHint: CharSequence) {
        this.savedHint = savedHint
        validateHint(hasFocus())
    }

    fun setSavedHint(@StringRes resid: Int) {
        setSavedHint(context.resources.getText(resid))
    }

    private fun validateHint(focused: Boolean) {
        if (focused) {
            savedHint = hint
        }
        hint = if (focused) "" else savedHint
    }

}