package com.example.weclean.backend

import java.io.Serializable

/**
 * Interface for the tags
 *
 */
interface Tags : Serializable {
    abstract fun addTag(tag: String): ArrayList<String>
    abstract fun removeTag(tag: String): ArrayList<String>

    var tags : ArrayList<String>
}

class TagsImpl : Tags, Serializable {
    // List of current tags
    override var tags = ArrayList<String>()

    /**
     * Remove a tag from the tags list
     *
     * @param tag
     * @return
     */
    override fun removeTag(tag: String): ArrayList<String> {
        tags.remove(tag)

        return tags
    }

    /**
     * Add a tag from the tags list
     *
     * @param tag
     * @return
     */
    override fun addTag(tag: String): ArrayList<String> {
        tags.add(tag)

        return tags
    }
}