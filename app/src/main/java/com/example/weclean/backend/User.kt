package com.example.weclean.backend

import android.content.ContentValues.TAG
import android.util.Log
import kotlinx.coroutines.runBlocking
import java.util.Date

class User (private val fireBase: FireBase) {

    /**
     * Create a user object
     *
     * @param username
     * @param dateOfBirth
     * @param country
     * @return Hashmap of the user object
     */
    fun createUser(username: String, timeStamp: Long, country: String): HashMap<String, Any> {

        return hashMapOf(
            "username" to username,
            "dob" to Date(timeStamp),
            "country" to country,
        )
    }
}