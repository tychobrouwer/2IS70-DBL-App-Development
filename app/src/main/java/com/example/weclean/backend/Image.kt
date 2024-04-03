package com.example.weclean.backend

import java.io.Serializable

interface Image : Serializable {
    // Image ID in the database
    var imageId: String
}
class ImageImpl : Image, Serializable {
    override var imageId: String = ""
}

