package com.example.weclean.ui.map

import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
import androidx.fragment.app.Fragment
import com.example.weclean.R
import com.example.weclean.backend.LitteringData
import com.example.weclean.backend.dayStringFormat
import com.example.weclean.backend.FireBase
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.runBlocking
import java.util.Locale

class LitteringDetails : Fragment() {
    private val db = Firebase.firestore
    private val dbStore = FirebaseStorage.getInstance()
    private val fireBase = FireBase()

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

        runBlocking { updateFields() }
    }

    private suspend fun updateFields() {
        val litteringDataResult = fireBase.getDocument("LitteringData", litteringId)

        if (litteringDataResult == null) {
            Toast.makeText(activity, "Error getting littering information", Toast.LENGTH_SHORT).show()
            return
        }

        // Latitude and longitude of the entry
        val entryLatitude = litteringDataResult.getGeoPoint("geoPoint")!!.latitude
        val entryLongitude = litteringDataResult.getGeoPoint("geoPoint")!!.longitude

        // Construct LitteringData object for the entry from database
        litteringData = LitteringData(geocoder)
        litteringData.timeStamp = litteringDataResult.getDate("date")!!.time
        litteringData.community = litteringDataResult.getString("community")!!
        litteringData.imageId = litteringDataResult.getString("imageId")!!
        litteringData.description = litteringDataResult.getString("description")!!
            .replace("_newline", "\n")
        litteringData.updateLocation(entryLatitude, entryLongitude)
        litteringData.tags = (litteringDataResult.get("tags") as ArrayList<*>).map { it as String } as ArrayList<String>
        litteringData.id = litteringDataResult.id

        view.findViewById<TextView>(R.id.littering_location).text = litteringData.getAddressLine()
        view.findViewById<TextView>(R.id.littering_time).text = dayStringFormat(litteringData.timeStamp)
        view.findViewById<TextView>(R.id.littering_description).text = litteringData.description

        for (tag in litteringData.tags) {
            addTagChip(view, tag)
        }

        // Load image from Firebase Storage
        val imageView = view.findViewById<ImageView>(R.id.littering_image)

        val imageBytes = fireBase.getFileBytes(litteringData.imageId, 1024 * 1024)

        if (imageBytes == null) {
            Toast.makeText(activity, "Failed to load image", Toast.LENGTH_SHORT).show()
            return
        }

        val bmp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        imageView.setImageBitmap(bmp)
    }

    /**
     * Add chip to the ChipGroup and update litteringData
     *
     * @param chipText
     */
    private fun addTagChip(view: View, chipText: String) {
        // Chip group view
        val tagChipGroup = view.findViewById<ChipGroup>(R.id.tag_chip_group)

        // Random colors for chips
        val tagColors = intArrayOf(
            Color.rgb(220, 0, 220),
            Color.rgb(0, 191, 220),
            Color.rgb(220, 69, 0),
            Color.rgb(30, 144, 220),
            Color.rgb(34, 139, 34),
            Color.rgb(176, 48, 96),
            Color.rgb(0, 220, 0),
            Color.rgb(220, 220, 0),
            Color.rgb(47, 79, 79))

        // Create new material chip
        val chip = Chip(view.context)

        // Get number of tags already added
        val nTags = tagChipGroup.size

        // Set styling of the chip
        chip.text = chipText
        chip.isCloseIconVisible = true
        chip.chipStrokeWidth = 0F
        chip.chipBackgroundColor = ColorStateList.valueOf(tagColors[nTags])
        chip.setTextColor(view.context.getColorStateList(R.color.white))
        chip.closeIconTint = view.context.getColorStateList(R.color.white)
        chip.textSize = 16F
        chip.chipIconSize = 16F
        chip.textStartPadding = 2F
        chip.textEndPadding = 3F
        chip.closeIconEndPadding = 4F
        chip.chipEndPadding = 4F
        chip.setEnsureMinTouchTargetSize(false)

        // Add chip to chip group view
        tagChipGroup.addView(chip)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_littering_details, container, false)
    }
}