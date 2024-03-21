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
import com.example.weclean.ui.profile.CreateCommunity
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Filter
import kotlinx.coroutines.runBlocking

private val user1 = User()

class Community {

    //variables for sign up activity and firebase authentication
    private var fireBase = FireBase()

    private lateinit var firebaseAuth: FirebaseAuth
    private val db = Firebase.firestore

    private fun createCommunity(cName: String,
                                email: String,
                                location: String,
                                cCode: Int,
                                userIds: ArrayList<String>,
                                adminIds: ArrayList<String>)
    : HashMap<String, Any> {
        return hashMapOf(
            "name" to cName,
            "contactEmail" to email,
            "location" to location,
            "code" to cCode,
            "userIds" to userIds,
            "adminIds" to adminIds
        )
    }

    suspend fun addCommunityToDatabase(name: String, contactEmail: String, location: String, code : Int) : Boolean {
        val adminId = fireBase.currentUserId()
        val community = createCommunity(name, contactEmail, location, code, arrayListOf(adminId), arrayListOf(adminId))

        val result = fireBase.addDocument("Community", community)

        if (result == null) {
            Log.d(TAG, "Error creating community")

            return false
        }

        fireBase.addToArray("Users", adminId, "communityIds", result.id)

        return true
    }

    suspend fun addUserToCommunity(communityCode: String) : Boolean {
        val userId = fireBase.currentUserId()

        val communityData = fireBase.getDocumentsWithFilter(
            "Community",
            Filter.equalTo("Code",communityCode)
        ) ?: return false

        val communityId = communityData!!.documents[0].id
        val userResult = fireBase.addToArray("Users", userId, "Communities", communityId)
        val communityResult = fireBase.addToArray("Community", communityId, "userIds", userId)

        return !(!userResult || !communityResult)
    }
}