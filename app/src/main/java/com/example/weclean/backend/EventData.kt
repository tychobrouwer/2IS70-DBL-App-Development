package com.example.weclean.backend

import java.util.Date

class EventData : Image {
    private val image : Image = ImageImpl()

    // Name of the event data
    var name = ""
    // Community of the event data
    var community = ""
    // Community of the event data
    var communityName = ""
    // Description of the event data
    var description = ""
    // Time stamp of the event data (epoch)
    var timeStamp = System.currentTimeMillis()
    // Location of the event data
    var location = ""
    // Number of people of the event data
    var numPeople = 0
    // Firebase id of the event data
    var id = ""

    /**
     * Creates a hashmap of the important data to be stored in the database
     *
     * @return HashMap of the event data
     */
    fun createEventData(creator: String) : HashMap<String, Any> {
        return hashMapOf(
            "description" to description.replace("\n", "_newline"),
            "imageId" to imageId,
            "date" to Date(timeStamp),
            "community" to community,
            "name" to name,
            "location" to location,
            "userIds" to listOf(creator),
        )
    }

    override var imageId: String
        get() = image.imageId
        set(value) { image.imageId = value }
}