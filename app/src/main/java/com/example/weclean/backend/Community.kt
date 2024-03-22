package com.example.weclean.backend

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.Filter

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

    suspend fun addToDatabase(name: String, contactEmail: String, location: String, code : Int) : Boolean {
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

    suspend fun addUserWithCode(communityCode: String) : Boolean {
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