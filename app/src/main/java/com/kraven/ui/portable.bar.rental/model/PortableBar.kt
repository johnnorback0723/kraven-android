package com.kraven.ui.portable.bar.rental.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PortableBar(@SerializedName("bar_image")
                       @Expose
                       var barImage: String? = null) {
}