package com.kraven.ui.authentication.model


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kraven.ui.authentication.model.Country

import java.util.ArrayList

/**
 * Created by hlink54 on 5/9/16.
 */
class CountryWrapper {
    /**
     * @return The countries
     */
    /**
     * @param countries The countries
     */
    @SerializedName("countries")
    @Expose
    var countries: List<Country> = ArrayList()
    @SerializedName("nationality")
    @Expose
    val nationalitys: List<String> = ArrayList()


}
