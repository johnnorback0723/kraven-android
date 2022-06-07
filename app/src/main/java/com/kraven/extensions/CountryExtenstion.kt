package com.kraven.extensions

import android.text.TextWatcher
import androidx.fragment.app.FragmentActivity
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.kraven.ui.authentication.fragement.PhoneNumberFormatter
import com.kraven.ui.authentication.model.Country
import java.util.*


val phoneNumberFormat = PhoneNumberFormatter()


fun formatNumber(number: String, country: String): String {
    val pnu = PhoneNumberUtil.getInstance()
    val pn = pnu.parse(number, country)
    val pnE164 = pnu.format(pn, PhoneNumberUtil.PhoneNumberFormat.NATIONAL)

    //return pnE164.replace("^0*".toRegex(), "")
    return pnE164
}

fun getCountryCode(country: String?, activity: FragmentActivity): String {
    val listCountry = Country.readJsonOfCountries(activity)
    for (locale in listCountry) {
        if (locale.id.equals(country)) {
            return "+" + locale.countryCode.toString()
        }
    }
    return ""
}

fun getCountryCodeDial(country: String?, activity: FragmentActivity): String {
    val listCountry = Country.readJsonOfCountries(activity)
    for (locale in listCountry) {
        if (locale.id.equals(country)) {
            return locale.dial.toString()
        }
    }
    return ""
}


fun getLeadingDigits(country: String?, activity: FragmentActivity): String {
    val listCountry = Country.readJsonOfCountries(activity)
    for (locale in listCountry) {
        if (locale.id.equals(country)) {
            return "(" + locale.leadingDigits.toString() + ")"
        }
    }
    return ""
}

fun getHintText(country: String?, activity: FragmentActivity): String {
    val listCountry = Country.readJsonOfCountries(activity)
    for (locale in listCountry) {
        if (locale.id.equals(country)) {
            return locale.exampleNumber.toString()
        }
    }
    return ""
}

fun setPhoneNumberFormat(countryId: String): TextWatcher? {
    phoneNumberFormat.countryCode = countryId
    return phoneNumberFormat
}