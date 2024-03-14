package com.example.weclean.backend

class User {

    fun createUser(firstName: String, lastName: String, emailID: String): HashMap<String, Any> {

        return hashMapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "email" to emailID
        )
    }

}