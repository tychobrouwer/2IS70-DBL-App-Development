package com.example.weclean.ui.home

import com.example.weclean.backend.Image
import com.example.weclean.backend.ImageImpl
import com.example.weclean.backend.Location
import com.google.firebase.firestore.GeoPoint
import java.io.Serializable
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar

class EventData(
    var loc: Location?,
    @Transient
    var image: Image = ImageImpl(),

    var desc: String = "",
    var date: Date = GregorianCalendar(1970, Calendar.JANUARY, 1).time,
    var numppl: Int = 0,
    var name: String = "",
    var community: String = "No Community"
) : Serializable {
    fun createEventData():  HashMap<String, Any> {
        return hashMapOf(
            "Community" to community,
            "loc" to (loc?.let { GeoPoint(it.latitude, it.longitude) } ?: GeoPoint(0.0, 0.0)),
            "img" to image.imageId,
            "date" to date,
            "desc" to desc,
        )
    }
    //TODO: make a proper class with methods once database in implemented



}