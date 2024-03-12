package com.example.weclean.backend

class User {

    fun createUser(firstName: String, lastName: String, emailID: String): HashMap<String, Any> {

        return hashMapOf(
            "first" to firstName,
            "last" to lastName,
            "email" to emailID
        )
    }

}