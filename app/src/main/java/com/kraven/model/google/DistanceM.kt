package com.kraven.model.google

import com.google.gson.annotations.SerializedName
import com.kraven.delivery.model.google.Directions

class DistanceM {
    @SerializedName("destination_addresses")
    var destinationAddresses: List<String>? = null
    @SerializedName("origin_addresses")
    var originAddresses: List<String>? = null
    @SerializedName("error_message")
    var error_message: String? = null
    @SerializedName("rows")
    var rows: List<Row>? = null
    @SerializedName("status")
    var status: String? = null
}


class Row {

    @SerializedName("elements")
    var elements: List<Element>? = null

}

class Element {

    @SerializedName("distance")
    var distance: Directions.Routes.Legs.Distance? = null
    @SerializedName("duration")
    var duration: Directions.Routes.Legs.Duration? = null
    @SerializedName("status")
    var status: String? = null

}
