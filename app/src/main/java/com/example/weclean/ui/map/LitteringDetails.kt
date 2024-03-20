package com.example.weclean.ui.map

import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.weclean.R
import com.example.weclean.backend.LitteringData
import com.example.weclean.backend.dayStringFormat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Filter
import java.util.Locale

class LitteringDetails : Fragment() {
    private val db = Firebase.firestore

    private var litteringId: String = ""
    private lateinit var geocoder : Geocoder
    private lateinit var litteringData: LitteringData
    private lateinit var view : View

    fun newInstance(litteringId: String): LitteringDetails {
        val fragment = LitteringDetails()
        fragment.litteringId = litteringId

        return fragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        geocoder = Geocoder(activity as AppCompatActivity, Locale.getDefault())
        litteringData = LitteringData(geocoder)

        this.view = view

        db.collection("LitteringData")
            .where(Filter.equalTo(FieldPath.documentId(), litteringId))
            .get()
            .addOnSuccessListener { documents ->
                val document = documents.first() ?: return@addOnSuccessListener

                // Latitude and longitude of the entry
                val entryLatitude = document.getGeoPoint("geoPoint")!!.latitude
                val entryLongitude = document.getGeoPoint("geoPoint")!!.longitude

                // Construct LitteringData object for the entry from database
                litteringData = LitteringData(geocoder)
                litteringData.timeStamp = document.getDate("date")!!.time
                litteringData.community = document.getString("community")!!
                litteringData.description = document.getString("description")!!
                    .replace("_newline", "\n")
                litteringData.updateLocation(entryLatitude, entryLongitude)
                litteringData.id = document.id

                updateFields()
            }
    }

    private fun updateFields() {
        view.findViewById<TextView>(R.id.littering_location).text = litteringData.getAddressLine()
        view.findViewById<TextView>(R.id.littering_time).text = dayStringFormat(litteringData.timeStamp)
        view.findViewById<TextView>(R.id.littering_description).text = litteringData.description
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_littering_details, container, false)
    }
}