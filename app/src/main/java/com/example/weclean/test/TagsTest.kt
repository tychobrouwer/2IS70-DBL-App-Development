package com.example.weclean.backend

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class TagsImplTest {

    private lateinit var tagsImpl: TagsImpl

    @Before
    fun setUp() {
        tagsImpl = TagsImpl()
    }

    @Test
    fun testAddTag () {
        tagsImpl.addTag("Tag1")
        assertTrue(tagsImpl.tags.contains("Tag1"))
    }

    @Test
    fun testRemoveTag () {
        tagsImpl.tags.add("Tag2")
        tagsImpl.removeTag("Tag2")
        assertFalse(tagsImpl.tags.contains("Tag2"))
    }

    @Test
    fun testMultipleTags () {
        tagsImpl.addTag("Tag1")
        tagsImpl.addTag("Tag2")
        assertEquals(2, tagsImpl.tags.size)
        assertTrue(tagsImpl.tags.contains("Tag1"))
        assertTrue(tagsImpl.tags.contains("Tag2"))
    }

    @Test
    fun testRemoveMultipleTags () {
        tagsImpl.tags.add("Tag1")
        tagsImpl.tags.add("Tag2")
        tagsImpl.removeTag("Tag1")
        assertEquals(1, tagsImpl.tags.size)
        assertFalse(tagsImpl.tags.contains("Tag1"))
        assertTrue(tagsImpl.tags.contains("Tag2"))
    }

    @Test
    fun testAddTagList () {
        val updatedList = tagsImpl.addTag("Tag1")
        assertEquals(1, updatedList.size)
        assertTrue(updatedList.contains("Tag1"))
    }

    @Test
    fun removeTagList () {
        tagsImpl.tags.add("Tag1")
        val updatedList = tagsImpl.removeTag("Tag1")
        assertEquals(0, updatedList.size)
        assertFalse(updatedList.contains("Tag1"))
    }
}