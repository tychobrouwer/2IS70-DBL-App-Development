package com.example.weclean.backend

import android.location.Geocoder
import com.google.firebase.firestore.GeoPoint
import java.io.Serializable
import java.util.Date

/**
 * Interface for the littering data
 *
 * @param geocoder
 */
class LitteringData(
    geocoder: Geocoder,
) : Location, Tags, Image, Serializable {
    private val location : Location = LocationImpl(geocoder)
    private val tagsData : Tags = TagsImpl()
    private val image : Image = ImageImpl()

    // Community of the littering data
    var community = ""
    // Description of the littering data
    var description = ""
    // Time stamp of the littering data (epoch)
    var timeStamp = System.currentTimeMillis()
    // Firebase id of the littering data
    var id = ""
    // Creator of the littering data
    var creator = ""

    /**
     * Compose the address line to display to the user
     *
     * @return String of the address line
     */
    fun getAddressLine() : String {
        return location.streetAddress + ", " + location.city
    }

    /**
     * Creates a hashmap of the important data to be stored in the database
     *
     * @param creator
     * @return HashMap of the littering data
     */
    fun createLitteringData(creator : String) : HashMap<String, Any> {
        return hashMapOf(
            "geoPoint" to GeoPoint(latitude, longitude),
            "description" to description.replace("\n", "_newline"),
            "imageId" to imageId,
            "tags" to tags,
            "date" to Date(timeStamp),
            "community" to community,
            "creator" to creator
        )
    }

    override fun updateLocation(latitude: Double, longitude: Double) {
        location.updateLocation(latitude, longitude)
    }

    override var latitude: Double
        get() = location.latitude
        set(value) { location.latitude = value }
    override var longitude: Double
        get() = location.longitude
        set(value) { location.longitude = value }

    override var address = location.address
    override var streetAddress = location.streetAddress
    override var city = location.city
    override var country = location.country

    override fun addTag(tag: String): ArrayList<String> {
        return tagsData.addTag(tag)
    }

    override fun removeTag(tag: String): ArrayList<String> {
        return tagsData.removeTag(tag)
    }

    override var tags: ArrayList<String>
        get() = tagsData.tags
        set(value) { tagsData.tags = value }

    override var imageId: String
        get() = image.imageId
        set(value) { image.imageId = value }
}