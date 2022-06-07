package com.kraven.location

/**
 * Created by Hlink on 27/6/18.
 */
class LocationException(
    val message: String? = "Error",
    val status: NewLocationManager.Status = NewLocationManager.Status.OTHER
) {
}