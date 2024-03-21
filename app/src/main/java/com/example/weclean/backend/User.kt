package com.example.weclean.backend

import android.content.ContentValues.TAG
import android.util.Log
import kotlinx.coroutines.runBlocking

class User {

    //variables for sign up activity and firebase authentication
    private val fireBase = FireBase()

    private fun createUser(username: String, emailID: String, country: String): HashMap<String, Any> {

        return hashMapOf(
            "username" to username,
            "email" to emailID,
            "country" to country,
        )
    }

    fun addToDatabase(uid: String, username: String, emailID: String, country: String) {
        // add user to the database
        val user = createUser(username, emailID, country)

        // add user to Users/...
        runBlocking {
            val result = fireBase.addDocumentWithName("Users", uid, user)

            if (result == null) {
                Log.d(TAG, "Error creating user")
            } else {
                Log.d(TAG, "User created successfully")
            }
        }
    }
}