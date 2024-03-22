package com.example.weclean.backend

import android.content.ContentValues.TAG
import android.util.Log
import kotlinx.coroutines.runBlocking

class User {

    // FireBase class instance to communicate with the database
    private val fireBase = FireBase()

    /**
     * Create a user object
     *
     * @param username
     * @param emailID
     * @param dateOfBirth
     * @param country
     * @return Hashmap of the user object
     */
    private fun createUser(username: String, emailID: String, dateOfBirth: String, country: String): HashMap<String, Any> {

        return hashMapOf(
            "username" to username,
            "email" to emailID,
            "dob" to dateOfBirth,
            "country" to country,
        )
    }

    /**
     * Add a user to the database
     *
     * @param uid
     * @param username
     * @param emailID
     * @param dob
     * @param country
     */
    fun addToDatabase(uid: String, username: String, emailID: String, dob : String, country: String) {
        // add user to the database
        val user = createUser(username, emailID, dob, country)

        // add user to Users/...
        runBlocking {
            val result = fireBase.addDocumentWithName("Users", uid, user)

            if (!result) {
                Log.d(TAG, "Error creating user")
            } else {
                Log.d(TAG, "User created successfully")
            }
        }
    }
}