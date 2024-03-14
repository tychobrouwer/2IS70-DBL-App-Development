package com.example.weclean.backend

class Community {

    fun createCommunity(cName: String, email: String, location: String, users : Array<String>): HashMap<String, Any> {

        return hashMapOf(
            "communityName" to cName,
            "communityEmail" to email,
            "communityLocation" to location,
            "listOfUsers" to users
        )
    }
}