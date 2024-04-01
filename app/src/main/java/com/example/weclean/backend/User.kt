package com.example.weclean.backend

class User {

    /**
     * Create a user object
     *
     * @param username
     * @return Hashmap of the user object
     */
    fun createUser(username: String): HashMap<String, Any> {
        return hashMapOf(
            "username" to username,
            "communityAdminIds" to arrayListOf<String>(),
            "communityIds" to arrayListOf<String>(),
            "eventIds" to arrayListOf<String>(),
            "litteringEntries" to arrayListOf<String>()
        )
    }
}