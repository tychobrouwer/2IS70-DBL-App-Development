package com.example.weclean.backend

import java.util.Calendar

/**
 * Convert the timestamp to a readable format
 *
 * @param timeStamp
 * @return String of the time ago
 */
fun dayStringFormat(timeStamp: Long): String {
    // Time constants
    val oneMinute = 60000L
    val oneHour = 3600000L
    val oneDay = 86400000L
    val oneWeek = 604800000L
    val oneMonth = 2592000000L
    val oneYear = 31536000000L

    // Get the current time
    val currentTime = System.currentTimeMillis()
    val difference = currentTime - timeStamp

    // Get the current calendar instance
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeStamp

    val time = padTimeInt(calendar.get(Calendar.HOUR_OF_DAY)) + ":" +
            padTimeInt(calendar.get(Calendar.MINUTE))

    val timeDate = padTimeInt(calendar.get(Calendar.MONTH)) + "/" +
            padTimeInt(calendar.get(Calendar.DAY_OF_MONTH)) + " at " +
            padTimeInt(calendar.get(Calendar.HOUR_OF_DAY)) + ":" +
            padTimeInt(calendar.get(Calendar.MINUTE))

    val shortDate = padTimeInt(calendar.get(Calendar.MONTH)) + "/" +
            padTimeInt(calendar.get(Calendar.DAY_OF_MONTH))

    val longDate = padTimeInt(calendar.get(Calendar.MONTH)) + "/" +
            padTimeInt(calendar.get(Calendar.DAY_OF_MONTH)) + "/" +
            calendar.get(Calendar.YEAR).toString()

    return when (difference) {
        in -10*oneYear..-oneYear -> longDate
        in -oneYear..-oneMonth -> shortDate
        in -oneMonth..-2*oneDay -> timeDate
        in -2*oneDay..-oneDay -> {
            "Tomorrow at $time"
        }
        in -oneDay..-oneHour -> {
            "Today at $time"
        }
        in -oneHour..0 -> "Now"
        in 0..oneMinute -> "Just now"
        in oneMinute..oneHour -> "${difference / oneMinute} Minutes ago"
        in oneHour..oneDay -> "${difference / oneHour} Hours ago"
        in oneDay..oneWeek -> "${difference / oneDay} Days ago"
        in oneWeek..oneMonth -> timeDate
        in oneMonth..oneYear -> shortDate
        else -> longDate
    }
}

fun padTimeInt(toPad: Int): String {
    return toPad.toString().padStart(2, '0')
}