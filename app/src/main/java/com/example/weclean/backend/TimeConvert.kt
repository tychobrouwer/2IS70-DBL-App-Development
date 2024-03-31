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
    val ONE_MINUTE = 60000L
    val ONE_HOUR = 3600000L
    val ONE_DAY = 86400000L
    val ONE_WEEK = 604800000L
    val ONE_MONTH = 2592000000L
    val ONE_YEAR = 31536000000L

    // Get the current time
    val currentTime = System.currentTimeMillis()
    val difference = currentTime - timeStamp

    // Get the current calendar instance
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeStamp

    return when (difference) {
        in -10*ONE_YEAR..-ONE_YEAR -> {
            calendar.get(Calendar.MONTH).toString() + "/" +
                    calendar.get(Calendar.DAY_OF_MONTH) + "/" +
                    calendar.get(Calendar.YEAR).toString()
        }
        in -ONE_YEAR..-ONE_MONTH -> {
            calendar.get(Calendar.MONTH).toString() + "/" +
                    calendar.get(Calendar.DAY_OF_MONTH) + "/" +
                    calendar.get(Calendar.YEAR).toString()
        }
        in -ONE_MONTH..-2*ONE_DAY -> {
            calendar.get(Calendar.MONTH).toString() + "/" +
                    calendar.get(Calendar.DAY_OF_MONTH) + " at " +
                    calendar.get(Calendar.HOUR_OF_DAY).toString() + ":" +
                    padTimeInt(calendar.get(Calendar.MINUTE))
        }
        in -2*ONE_DAY..-ONE_DAY -> {
            "Yesterday at " + calendar.get(Calendar.HOUR_OF_DAY).toString() + ":" +
                    padTimeInt(calendar.get(Calendar.MINUTE))
        }
        in -ONE_DAY..-ONE_HOUR -> {
            "Today at " + calendar.get(Calendar.HOUR_OF_DAY).toString() + ":" +
                    padTimeInt(calendar.get(Calendar.MINUTE))
        }
        in -ONE_HOUR..0 -> "Now"
        in 0..ONE_MINUTE -> "Just now"
        in ONE_MINUTE..ONE_HOUR -> "${difference / ONE_MINUTE} Minutes ago"
        in ONE_HOUR..ONE_DAY -> "${difference / ONE_HOUR} Hours ago"
        in ONE_DAY..ONE_WEEK -> "${difference / ONE_DAY} Days ago"
        in ONE_WEEK..ONE_YEAR -> {
            calendar.get(Calendar.MONTH).toString() + "/" +
                    calendar.get(Calendar.DAY_OF_MONTH) + " " +
                    calendar.get(Calendar.HOUR_OF_DAY).toString() + ":" +
                    padTimeInt(calendar.get(Calendar.MINUTE))
        }
        else -> {
            calendar.get(Calendar.MONTH).toString() + "/" +
                    calendar.get(Calendar.DAY_OF_MONTH) + "/" +
                    calendar.get(Calendar.YEAR).toString()
        }
    }
}

fun padTimeInt(toPad: Int): String {
    return toPad.toString().padStart(2, '0')
}