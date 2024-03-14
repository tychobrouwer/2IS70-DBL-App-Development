package com.example.weclean.backend

/**
 * Interface for the tags
 *
 */
interface Tags {
    abstract fun addTag(tag: String): ArrayList<String>
    abstract fun removeTag(tag: String): ArrayList<String>

    var tags : ArrayList<String>
}

/**
 * Implementation of the tags
 *
 */
class TagsImpl : Tags {
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