package com.example.weclean.backend

fun dayStringFormat(timeStamp: Long): String {
    val ONE_SECOND = 1000L
    val ONE_MINUTE = 60000L
    val ONE_HOUR = 3600000L
    val ONE_DAY = 86400000L
    val ONE_MONTH = 2592000000L
    val ONE_YEAR = 31536000000L

    val currentTime = System.currentTimeMillis()
    val difference = currentTime - timeStamp
    return when {
        difference < ONE_MINUTE -> {
            val timeAgo = difference / ONE_SECOND
            "Just Now"
        }
        difference < ONE_HOUR -> {
            val timeAgo = difference / ONE_MINUTE
            "$timeAgo Minutes Ago"
        }
        difference < ONE_DAY -> {
            val timeAgo = difference / ONE_HOUR
            "$timeAgo Hours Ago"
        }
        difference < ONE_MONTH -> {
            val timeAgo = difference / ONE_DAY
            "$timeAgo Days Ago"
        }
        difference < ONE_YEAR -> {
            val timeAgo = difference / ONE_MONTH
            "$timeAgo Months Ago"
        }
        else -> {
            val timeAgo = difference / ONE_YEAR
            "$timeAgo Years Ago"
        }
    }
}