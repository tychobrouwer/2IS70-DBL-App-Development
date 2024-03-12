package com.example.weclean.backend

data class Community(
    var communityId: String,
    var communityMembers: ArrayList<User>,
    //var cleanUpEvents: ArrayList<CleanUpEvent>
)
