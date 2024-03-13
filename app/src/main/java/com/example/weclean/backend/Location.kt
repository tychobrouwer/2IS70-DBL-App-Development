package com.example.weclean.backend

import android.location.Geocoder
import android.widget.TextView
import com.example.weclean.R
import java.util.Locale

interface Location {
    abstract fun updateLocation(latitude: Double, longitude: Double)

    var latitude: Double
    var longitude: Double
    var address: String
    var city: String
    var country : String
}
class LocationImpl(geocoder: Geocoder,
                   override var latitude: Double,
                   override var longitude: Double
) : Location {
    private val geocoder : Geocoder

    override var address: String = ""
    override var city: String = ""
    override var country: String = ""

    init {
        this.geocoder = geocoder

        setAddressFromLatLng()
    }

    override fun updateLocation(latitude: Double, longitude: Double) {
        this.latitude = latitude
        this.longitude = longitude

        setAddressFromLatLng()
    }

    private fun setAddressFromLatLng() {
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        if (addresses != null) {
            this.address = addresses[0].getAddressLine(0)
            this.city = addresses[0].locality
            this.country = addresses[0].countryName
        }
    }
}