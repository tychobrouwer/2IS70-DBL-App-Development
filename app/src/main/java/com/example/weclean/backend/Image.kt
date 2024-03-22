package com.example.weclean.backend

interface Image {
    // Image ID in the database
    var imageId: String
}
class ImageImpl : Image {
    override var imageId: String = ""
}