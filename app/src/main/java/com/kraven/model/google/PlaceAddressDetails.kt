package com.kraven.model.google

import com.google.gson.annotations.SerializedName
import com.kraven.delivery.model.google.Directions


data class PlaceAddressDetails(
        @field:SerializedName("result")
    var result: Result? = null,
        @field:SerializedName("status")
    var status: String? = null,
        @field:SerializedName("html_attributions")
    var htmlAttributions: List<String>? = null
)

data class Result(
        @field:SerializedName("adr_address")
    var adrAddress: String? = null,
        @field:SerializedName("formatted_address")
    var formattedAddress: String? = null,
        @field:SerializedName("geometry")/*,
        var location: LatLng? = null*/
    var geometry: Geometry? = null,
        @field:SerializedName("icon")
    var icon: String? = null,
        @field:SerializedName("id")
    var id: String? = null,
        @field:SerializedName("name")
    var name: String? = null,
        @field:SerializedName("place_id")
    var placeId: String? = null,
        @field:SerializedName("reference")
    var reference: String? = null,
        @field:SerializedName("scope")
    var scope: String? = null,
        @field:SerializedName("url")
    var url: String? = null,
        @field:SerializedName("utc_offset")
    var utcOffset: Int = 0,
        @field:SerializedName("vicinity")
    var vicinity: String? = null,
        @field:SerializedName("address_components")
    var addressComponents: List<AddressComponents>? = null,
        @field:SerializedName("photos")
    var photos: List<Photos>? = null,
        @field:SerializedName("types")
    var types: List<String>? = null
)

data class Geometry(
        @field:SerializedName("location")
    var location: Location? = null,
        @field:SerializedName("viewport")
    var viewport: Viewport? = null,
        @field:SerializedName("location_type")
    var locationType: String? = null,
        @field:SerializedName("bounds")
    var bounds: Directions.Routes.Bounds? = null
)

data class Location(
    @field:SerializedName("lat")
    var lat: Double = 0.toDouble(),
    @field:SerializedName("lng")
    var lng: Double = 0.toDouble()
)

data class Viewport(
        @field:SerializedName("northeast")
    var northeast: Directions.Routes.Bounds.Northeast? = null,
        @field:SerializedName("southwest")
    var southwest: Directions.Routes.Bounds.Southwest? = null
)

data class AddressComponents(
    @field:SerializedName("long_name")
    var longName: String? = null,
    @field:SerializedName("short_name")
    var shortName: String? = null,
    @field:SerializedName("types")
    var types: List<String>? = null
)

data class Photos(
    @field:SerializedName("height")
    var height: Int = 0,
    @field:SerializedName("photo_reference")
    var photoReference: String? = null,
    @field:SerializedName("width")
    var width: Int = 0,
    @field:SerializedName("html_attributions")
    var htmlAttributions: List<String>? = null
)