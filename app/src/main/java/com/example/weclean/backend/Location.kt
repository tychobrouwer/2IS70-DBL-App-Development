package com.example.weclean.backend

import android.location.Geocoder
import android.widget.TextView
import com.example.weclean.R
import java.util.Locale

/**
 * Interface for the location
 *
 */
interface Location {
    fun updateLocation(latitude: Double, longitude: Double)

    var latitude: Double
    var longitude: Double
    var address: String
    var streetAddress: String
    var city: String
    var country : String
}

/**
 * Implementation of the location
 *
 * @param geocoder
 */
class LocationImpl(geocoder: Geocoder
) : Location {
    private val geocoder : Geocoder

    // Current latitude and longitude of the location
    override var latitude: Double = 0.0
    override var longitude: Double = 0.0
    // Address information
    override var address: String = ""
    // Street address, street + number
    override var streetAddress: String = ""
    // City and country information
    override var city: String = ""
    override var country: String = ""

    init {
        this.geocoder = geocoder
    }

    /**
     * Update the location with the latitude and longitude
     *
     * @param latitude
     * @param longitude
     */
    override fun updateLocation(latitude: Double, longitude: Double) {
        this.latitude = latitude
        this.longitude = longitude

        setAddressFromLatLng()
    }

    /**
     * Set the address information using the android Geocoder
     *
     */
    private fun setAddressFromLatLng() {
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        if (addresses != null) {
            address = addresses[0].getAddressLine(0)
            streetAddress = address.split(Regex(","))[0]
            city = addresses[0].locality
            country = addresses[0].countryName
        }
    }
}