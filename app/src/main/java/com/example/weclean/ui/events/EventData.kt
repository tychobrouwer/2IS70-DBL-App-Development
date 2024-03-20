package com.example.weclean.ui.events

import java.io.Serializable

class EventData(
    val loc: String,
    val img: String,
    val desc: String,
    val date: java.util.Date,
    val numppl: Int,
    val id: Int
) : Serializable {
     //TODO: make a proper class with methods once database in implemented



}