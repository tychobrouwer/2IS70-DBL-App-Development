package com.example.weclean.backend

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weclean.ui.home.Home
import com.example.weclean.databinding.ActivitySignupBinding
import com.example.weclean.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import android.content.Context
import android.content.SharedPreferences

class User {

    //variables for sign up activity and firebase authentication
    private lateinit var firebaseAuth: FirebaseAuth
    private val db = Firebase.firestore

    private fun createUser(username: String, emailID: String): HashMap<String, Any> {

        return hashMapOf(
            "username" to username,
            "email" to emailID
        )
    }

    fun addToDatabase(username: String, emailID: String) {

        firebaseAuth = FirebaseAuth.getInstance()

        // add user to the database
        val user = createUser(username, emailID)

        // add user to Users/...
        db.collection("Users").add(user)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }

    }

}