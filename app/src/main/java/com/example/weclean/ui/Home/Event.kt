package com.example.weclean.ui.Home

import java.io.Serializable
import java.sql.Date

class Event(val  loc: String?,
            val img: String?,
            val desc: String?,
            val date: Date?,
            val numppl: Int?,
            val id: Int?
) : Serializable {
     //TODO: make a proper class with methods once database in implemented



}