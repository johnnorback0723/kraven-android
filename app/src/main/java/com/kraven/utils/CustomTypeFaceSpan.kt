package com.kraven.utils

import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.TypefaceSpan

class CustomTypefaceSpan(val newTypeface: Typeface) : TypefaceSpan("regular") {

    override fun updateDrawState(textPaint: TextPaint) {
        applyCustomTypeFace(textPaint)
    }

    override fun updateMeasureState(textPaint: TextPaint) {
        applyCustomTypeFace(textPaint)
    }

    private fun applyCustomTypeFace(paint: Paint) {
        val oldTypeface = paint.typeface
        val oldStyle = oldTypeface?.style ?: 0

        val fake = oldStyle and newTypeface.style.inv()
        if (fake and Typeface.BOLD != 0) {
            paint.isFakeBoldText = true
        }

        if (fake and Typeface.ITALIC != 0) {
            paint.textSkewX = -0.25f
        }

        paint.typeface = newTypeface
    }
}