package com.example.weclean.backend

interface Tags {
    abstract fun addTag(tag: String): ArrayList<String>
    abstract fun removeTag(tag: String): ArrayList<String>

    var tags : ArrayList<String>
}

class TagsImpl : Tags {
    override var tags = ArrayList<String>()

    override fun removeTag(tag: String): ArrayList<String> {
        tags.remove(tag)

        return tags
    }

    override fun addTag(tag: String): ArrayList<String> {
        tags.add(tag)

        return tags
    }
}