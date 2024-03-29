package com.example.weclean.backend

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.Filter

class Community {

    // FireBase class instance to communicate with the database
    private var fireBase = FireBase()

    /**
     * Create a community object
     *
     * @param cName
     * @param email
     * @param location
     * @param cCode
     * @param userIds
     * @param adminIds
     * @return Hashmap of the community object
     */
    fun createCommunity(cName: String,
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

    /**
     * Add a community to the database
     *
     * @param name
     * @param contactEmail
     * @param location
     * @param code
     * @return Boolean if adding community to the database was successful
     */
    suspend fun addToDatabase(name: String, contactEmail: String, location: String, code : Int) : Boolean {
        val adminId = fireBase.currentUserId() ?: return false
        val community = createCommunity(name, contactEmail, location, code, arrayListOf(adminId), arrayListOf(adminId))

        val result = fireBase.addDocument("Community", community)

        if (result == null) {
            Log.d(TAG, "Error creating community")

            return false
        }

        fireBase.addToArray("Users", adminId, "communityIds", result.id)

        return true
    }

    /**
     * Add a user to a community with a community code
     *
     * @param communityCode
     * @return Boolean if adding user to community was successful
     */
    suspend fun addUserWithCode(communityCode: String) : Boolean {
        val userId = fireBase.currentUserId() ?: return false

        val communityData = fireBase.getDocumentsWithFilter(
            "Community",
            Filter.equalTo("Code",communityCode)
        ) ?: return false

        val communityId = communityData.documents[0].id
        val userResult = fireBase.addToArray("Users", userId, "Communities", communityId)
        val communityResult = fireBase.addToArray("Community", communityId, "userIds", userId)

        return !(!userResult || !communityResult)
    }
}