package com.kraven.model.google

import com.google.gson.annotations.SerializedName

data class ReverseGeocodedAddress(
    @field:SerializedName("status")
    var status: String? = null,
    @field:SerializedName("results")
    var results: List<Results>? = null
)

data class Results(
        @field:SerializedName("formatted_address")
    var formattedAddress: String? = null,
        @field:SerializedName("geometry")
    var geometry: Geometry? = null,
        @field:SerializedName("place_id")
    var placeId: String? = null,
        @field:SerializedName("address_components")
    var addressComponents: List<AddressComponents>? = null,
        @field:SerializedName("types")
    var types: List<String>? = null
)