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
import com.example.weclean.backend.FireBase
import com.example.weclean.backend.dayStringFormat
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Locale

class LitteringDetails : Fragment() {
    // FireBase class instance to communicate with the database
    private val fireBase = FireBase()

    // Littering ID to show details of
    private var litteringId: String = ""

    // Geocoder to get address from latitude and longitude
    private lateinit var geocoder : Geocoder
    // Littering data object
    private lateinit var litteringData: LitteringData
    // View of the fragment
    private lateinit var view : View

    /**
     * Create a new instance of the LitteringDetails fragment with the littering ID
     *
     * @param litteringId
     * @return LitteringDetails fragment
     */
    fun newInstance(litteringId: String): LitteringDetails {
        // Create new fragment with littering ID
        val fragment = LitteringDetails()
        fragment.litteringId = litteringId

        return fragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Geocoder to get address from latitude and longitude
        geocoder = Geocoder(activity as AppCompatActivity, Locale.getDefault())
        // Littering data object
        litteringData = LitteringData(geocoder)

        this.view = view

        // Update littering entry fields in the fragment
        updateFields()
    }

    private fun updateFields() {
        runBlocking {
            launch {

                // Get littering data from database
                val litteringDataResult = fireBase.getDocument("LitteringData", litteringId)

                // If the littering data is null, show error message
                if (litteringDataResult == null) {
                    Toast.makeText(
                        activity,
                        "Error getting littering information",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
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
                litteringData.tags =
                    (litteringDataResult.get("tags") as ArrayList<*>).map { it as String } as ArrayList<String>
                litteringData.id = litteringDataResult.id

                // Update the fields in the fragment
                view.findViewById<TextView>(R.id.littering_location).text =
                    litteringData.getAddressLine()
                view.findViewById<TextView>(R.id.littering_time).text =
                    dayStringFormat(litteringData.timeStamp)
                view.findViewById<TextView>(R.id.littering_description).text =
                    litteringData.description

                // Add tags to the chip group
                for (tag in litteringData.tags) {
                    addTagChip(view, tag)
                }

                // Load image from Firebase Storage
                val imageView = view.findViewById<ImageView>(R.id.littering_image)

                // Get image bytes from Firebase Storage
                val imageBytes = fireBase.getFileBytes(litteringData.imageId, 1024 * 1024)

                if (imageBytes == null) {
                    Toast.makeText(activity as AppCompatActivity, "Failed to load image", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Set image view with the image bytes
                val bmp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                imageView.setImageBitmap(bmp)
            }
        }
    }

    /**
     * Add chip to the ChipGroup
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
        chip.textEndPadding = 4F
        chip.closeIcon = null
        chip.isClickable = false
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