package com.example.weclean.backend

import android.location.Geocoder

abstract class LitteringData(
    geocoder: Geocoder,
    latitude: Double,
    longitude: Double
) : Location, Tags, Image {
    val Location : Location = LocationImpl(geocoder, latitude, longitude)
    val Tags : Tags = TagsImpl()
    val Image : Image = ImageImpl()

    var timeStamp: Long = System.currentTimeMillis();
}