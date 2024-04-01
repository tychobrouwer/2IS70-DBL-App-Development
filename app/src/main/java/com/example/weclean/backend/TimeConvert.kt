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

    // Time formatting
    val time = padTimeInt(calendar[Calendar.HOUR_OF_DAY]) + ":" +
            padTimeInt(calendar[Calendar.MINUTE])

    // Time + date formatting
    val timeDate = padTimeInt(calendar[Calendar.MONTH]) + "/" +
            padTimeInt(calendar[Calendar.DAY_OF_MONTH]) + " at " +
            padTimeInt(calendar[Calendar.HOUR_OF_DAY]) + ":" +
            padTimeInt(calendar[Calendar.MINUTE])

    // Short date formatting (without year)
    val shortDate = padTimeInt(calendar[Calendar.MONTH]) + "/" +
            padTimeInt(calendar[Calendar.DAY_OF_MONTH])

    // Long date formatting (with year)
    val longDate = padTimeInt(calendar[Calendar.MONTH]) + "/" +
            padTimeInt(calendar[Calendar.DAY_OF_MONTH]) + "/" +
            calendar[Calendar.YEAR].toString()

    // Return the time ago or the formatted time
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

/**
 * Pad the time integer to have a leading zero
 *
 * @param toPad
 * @return String of the padded time
 */
fun padTimeInt(toPad: Int): String {
    return toPad.toString().padStart(2, '0')
}