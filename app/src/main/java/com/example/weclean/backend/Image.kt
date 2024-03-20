package com.example.weclean.backend


interface Image {
    var imageId: String
}
class ImageImpl : Image {
    override var imageId: String = ""
}