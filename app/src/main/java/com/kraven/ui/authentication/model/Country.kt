package com.kraven.ui.authentication.model


import android.os.Parcelable
import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kraven.R
import com.kraven.utils.DebugLog
import kotlinx.android.parcel.Parcelize


import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.StringWriter
import java.util.ArrayList


/**
 * Created by  on 18/6/16.
 */
@Parcelize
data class Country(@SerializedName("code")
                   @Expose
                   var code: String? = null,

                   @SerializedName("id")
                   @Expose
                   var id: String? = null,

                   @SerializedName("name")
                   @Expose
                   var name: String? = null,

                   @SerializedName("dial")
                   @Expose
                   var dial: String? = null,

                   @SerializedName("exampleNumber")
                   @Expose
                   var exampleNumber: String? = null,

                   @SerializedName("leadingDigits")
                   @Expose
                   var leadingDigits: String? = null,

                   @SerializedName("countryCode")
                   @Expose
                   var countryCode: String? = null) : Parcelable {

    
    companion object {
        fun readJsonOfCountries(activity: FragmentActivity): List<Country> {
            var countries: List<Country> = ArrayList()

            val file_name = "country_code_exno.json"
            val json_string = activity.assets.open(file_name).bufferedReader().use {
                it.readText()
            }
            val countryWrapper = Gson().fromJson(json_string, CountryWrapper::class.java)
            if (countryWrapper != null) {
                countries = countryWrapper.countries
                DebugLog.e(":::::COUNTRY LIST IS NOT NULL:::::" + countries.size)
            } else
                DebugLog.e(":::::COUNTRY LIST IS NULL:::::")

            return countries
        }

    }

}