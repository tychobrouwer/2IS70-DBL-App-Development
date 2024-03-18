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

    fun createCommunity(cName: String, email: String, location: String): HashMap<String, Any> {

        return hashMapOf(
            "communityName" to cName,
            "communityEmail" to email,
            "communityLocation" to location
        )
    }

    fun addCommunityWithUserToDatabase(cName: String, email: String, location: String, userConfirmEmail : String) {

        firebaseAuth = FirebaseAuth.getInstance()

        //create the community
        val community = createCommunity(cName, email, location);

        db.collection("Community").add(community).addOnSuccessListener { documentReference ->
            Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!")
            // Retrieve the ID of the added community document
            val communityId = documentReference.id

            // Add the user to this community
            getUserData(communityId, userConfirmEmail)

        }.addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }
    }

    private fun getUserData (communityId: String, userEmail: String) {
        db.collection("Community").document("No Community")
            .collection("Users").whereEqualTo("email", userEmail).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val userData = document.data
                    addUserToCommunitySuccess(communityId, userData, userEmail)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting user document: ", exception)
            }
    }

    private fun addUserToCommunitySuccess(communityId: String, userData: Map<String, Any>, userEmail: String) {
        db.collection("Community").document(communityId)
            .collection("Users").add(userData)
            .addOnSuccessListener {
                Log.d(TAG, "User added successfully to the community")
                removeUserFromStandardCommunity(userEmail)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding user to the community", e)
                // Handle failure, if needed
            }
    }

    private fun removeUserFromStandardCommunity(userEmail: String) {
        db.collection("Community").document("No Community")
            .collection("Users").whereEqualTo("email", userEmail).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    deleteUserDocument(document.reference)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting user document: ", exception)
            }
    }

    private fun deleteUserDocument(documentReference: DocumentReference) {
        documentReference.delete()
            .addOnSuccessListener {
                Log.d(TAG, "User document deleted successfully")
                // Handle success, if needed
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error deleting user document", e)
                // Handle failure, if needed
            }
    }
}