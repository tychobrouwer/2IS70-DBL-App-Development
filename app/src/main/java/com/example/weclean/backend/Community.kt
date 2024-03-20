package com.example.weclean.backend

import android.content.ContentValues
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
import com.example.weclean.backend.User
import com.google.firebase.firestore.DocumentReference

private val user1 = User()

class Community {

    //variables for sign up activity and firebase authentication
    private lateinit var binding:ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth
    //private val db = FirebaseFirestore.getInstance()
    private val db = Firebase.firestore

    private fun createCommunity(cName: String, email: String, location: String, cCode: Int, userIds: ArrayList<String>): HashMap<String, Any> {

        return hashMapOf(
            "communityName" to cName,
            "communityEmail" to email,
            "communityLocation" to location,
            "communityCode" to cCode,
            "userIds" to userIds
        )
    }


    private fun addCommunityWithUserToDatabase(cName: String, email: String, location: String, cCode : Int, userID : String) {

        firebaseAuth = FirebaseAuth.getInstance()

        val users = ArrayList<String>()
        users.add(userID)

        //create the community
        val community = createCommunity(cName, email, location, cCode, users);

        db.collection("Community").add(community).addOnSuccessListener {
            Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!")
        }.addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }
    }

    fun getUserDocumentId(cName: String, email: String, location: String, userConfirmEmail : String, cCode : Int) {
        db.collection("Users")
            .whereEqualTo("email", userConfirmEmail)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val userID = document.id // This retrieves the document ID
                    addCommunityWithUserToDatabase(cName, email, location, cCode, userID)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting user document: ", exception)
            }
    }
}