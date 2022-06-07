package com.kraven.core

import androidx.annotation.StringRes
import com.google.android.material.textfield.TextInputLayout
import android.util.Patterns
import android.widget.EditText
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.kraven.exception.ApplicationException


import java.util.ArrayList

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Validator @Inject constructor() {

    lateinit var subject: String
    lateinit var editText: EditText
    internal var messageBuilders: MutableList<MessageBuilder> = ArrayList()
    private var textInputLayout: TextInputLayout? = null
    private var textInputLayoutAnimation: TextInputLayout? = null

    fun submit(s: EditText): Validator {
        subject = s.text.toString()
        editText = s
        messageBuilders = ArrayList()
        return this
    }

    fun submit(s: EditText, textInputLayout: TextInputLayout): Validator {
        subject = s.text.toString()
        editText = s
        this.textInputLayout = textInputLayout
        this.textInputLayoutAnimation = null
        messageBuilders = ArrayList()
        return this
    }

    fun submit(s: EditText, textInputLayout: TextInputLayout, textInputLayoutAnimation: TextInputLayout): Validator {
        subject = s.text.toString()
        editText = s
        this.textInputLayout = textInputLayout
        this.textInputLayoutAnimation = textInputLayoutAnimation
        messageBuilders = ArrayList()
        return this
    }

    fun checkEmpty(): MessageBuilder {
        return MessageBuilder(Type.EMPTY)
    }

    fun checkEmptyWithoutTrim(): MessageBuilder {
        return MessageBuilder(Type.EMPTY_WITHOUT_TRIM)
    }

    fun checkValidEmail(): MessageBuilder {
        return MessageBuilder(Type.EMAIL)
    }

    fun checkMaxDigits(max: Int): MessageBuilder {
        return MessageBuilder(Type.MAX_LENGTH, max)
    }

    fun checkMinDigits(min: Int): MessageBuilder {
        return MessageBuilder(Type.MIN_LENGTH, min)
    }

    fun matchString(s: String): MessageBuilder {
        return MessageBuilder(Type.MATCH, s)
    }

   fun matchPatter(patter: String): MessageBuilder {
        return MessageBuilder(Type.PATTERN_MATCH, patter)
    }

    @Throws(ApplicationException::class)
    fun check(): Boolean {
        for (builder in messageBuilders) {
            try {
                when (builder.type) {
                    Validator.Type.EMPTY -> isEmpty(subject, builder.message, true)
                    Validator.Type.EMPTY_WITHOUT_TRIM -> isEmpty(subject, builder.message, false)
                    Validator.Type.EMAIL -> isValidEmail(subject, builder.message)
                    Validator.Type.MAX_LENGTH -> checkMaxDigits(subject, builder.validLength, builder.message)
                    Validator.Type.MIN_LENGTH -> checkMinDigits(subject, builder.validLength, builder.message)
                    Validator.Type.MATCH -> match(subject, builder.match, builder.message)
                    Validator.Type.PATTERN_MATCH -> matchPatter(subject, builder.match, builder.message)
                }
                if (textInputLayout != null) {
                    textInputLayout!!.isErrorEnabled = false
                }
            } catch (e: ApplicationException) {
                editText.requestFocus()
                if (textInputLayout != null) {
                    if (!textInputLayout!!.isErrorEnabled)
                        textInputLayout!!.isErrorEnabled = true
                    textInputLayout!!.error = e.localizedMessage
                    YoYo.with(Techniques.Shake)
                            .playOn(if (textInputLayoutAnimation == null) textInputLayout else textInputLayoutAnimation)
                }
                e.type = ApplicationException.Type.VALIDATION
                throw e
            }

        }
        return true
    }

    @Throws(ApplicationException::class)
    internal fun isEmpty(subject: String?, errorMessage: String?, byTrimming: Boolean) {
        var subjects: String? = subject ?: throw ApplicationException(errorMessage!!)
        if (byTrimming)
            subjects = subject.trim { it <= ' ' }
        if (subjects!!.isEmpty())
            throw ApplicationException(errorMessage!!)
    }

    @Throws(ApplicationException::class)
    internal fun isValidEmail(subject: String, errorMessage: String?) {
        if (!subject.matches(Patterns.EMAIL_ADDRESS.pattern().toRegex()))
            throw ApplicationException(errorMessage!!)
    }

    @Throws(ApplicationException::class)
    internal fun checkMinDigits(subject: String, min: Int, errorMessage: String?) {
        if (subject.length < min)
            throw ApplicationException(errorMessage!!)
    }

    @Throws(ApplicationException::class)
    internal fun checkMaxDigits(subject: String, max: Int, errorMessage: String?) {
        if (subject.length > max)
            throw ApplicationException(errorMessage!!)
    }

    @Throws(ApplicationException::class)
    internal fun match(subject: String, toMatch: String?, errorMessage: String?) {
        if (toMatch == null || subject != toMatch)
            throw ApplicationException(errorMessage!!)
    }

    @Throws(ApplicationException::class)
    internal fun matchPatter(subject: String?, pattern: String?, errorMessage: String?) {
        if (subject == null || !subject.matches(pattern?.toRegex()!!))
            throw ApplicationException(errorMessage!!)
    }

    inner class MessageBuilder {

        val type: Type
        var validLength: Int = 0
        var message: String? = null
            private set
        var match: String? = null

        constructor(type: Type) {
            this.type = type
        }

        constructor(type: Type, validLength: Int) {
            this.type = type
            this.validLength = validLength
        }

        constructor(type: Type, match: String) {
            this.type = type
            this.match = match
        }

        fun errorMessage(message: String): Validator {
            this.message = message
            messageBuilders.add(this)
            return this@Validator
        }

        fun errorMessage(@StringRes message: Int): Validator {
            this.message = editText.resources.getString(message)
            messageBuilders.add(this)
            return this@Validator
        }
    }

    enum class Type {
        EMPTY, EMAIL, MIN_LENGTH, MAX_LENGTH, MATCH, PATTERN_MATCH, EMPTY_WITHOUT_TRIM
    }
}
