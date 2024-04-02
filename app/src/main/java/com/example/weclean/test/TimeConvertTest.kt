package com.example.weclean.backend

import org.junit.Test
import org.junit.Assert.assertEquals

class TimeConvertTestTest {

    @Test
    fun testNow() {
        val currentTime = System.currentTimeMillis()
        val result = dayStringFormat(currentTime)
        assertEquals("Now", result)
    }

    @Test
    fun testJustNow() {
        val timeStamp = System.currentTimeMillis() - 30000 // 30 seconds ago
        val result = dayStringFormat(timeStamp)
        assertEquals("Just now", result)
    }

    @Test
    fun testMinutesAgo() {
        val timeStamp = System.currentTimeMillis() - 180000 // 3 minutes ago
        val result = dayStringFormat(timeStamp)
        assertEquals("3 Minutes ago", result)
    }

    @Test
    fun testHoursAgo() {
        val timeStamp = System.currentTimeMillis() - 7200000 // 2 hours ago
        val result = dayStringFormat(timeStamp)
        assertEquals("2 Hours ago", result)
    }

    @Test
    fun testDaysAgo() {
        val timeStamp = System.currentTimeMillis() - 172800000 // 2 days ago
        val result = dayStringFormat(timeStamp)
        assertEquals("2 Days ago", result)
    }

    //TEST FOR PAD TIME INT FUNCTION:

    @Test
    fun testSingleDigit() {
        val number = 5
        val result = padTimeInt(number)
        assertEquals("05", result)
    }

    @Test
    fun testDoubleDigit() {
        val number = 12
        val result = padTimeInt(number)
        assertEquals("12", result)
    }

    @Test
    fun testZero() {
        val number = 0
        val result = padTimeInt(number)
        assertEquals("00", result)
    }

}