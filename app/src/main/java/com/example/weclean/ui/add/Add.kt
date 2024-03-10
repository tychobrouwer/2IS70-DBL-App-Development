package com.example.weclean.ui.add

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.KeyEvent
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.size
import com.example.weclean.Achievements
import com.example.weclean.Home
import com.example.weclean.R
import com.example.weclean.ui.map.Map
import com.example.weclean.ui.profile.Profile
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.util.Locale


class Add : AppCompatActivity() {
    private var permissionCode = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add)

        getCurrentLocation()

        // TODO: Here the communities should be fetched and added to the list
        val communities = ArrayList<String>()
        communities.add("community 1")
        communities.add("community 2")
        communities.add("community 3")

        // Get dropdown for selecting communities
        val selectCommunitySpinner = findViewById<Spinner>(R.id.select_community)
        // Create adapter with list of communities
        val selectCommunityAdapter = ArrayAdapter(
            this, R.layout.spinner_item, communities)

        // Set adapter for communities selector
        selectCommunityAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        selectCommunityAdapter.setNotifyOnChange(true)
        selectCommunitySpinner.adapter = selectCommunityAdapter

        // Add tag button
        val tagButton = findViewById<Button>(R.id.select_tags_button)
        // Tag value text input field
        val tagInput = findViewById<EditText>(R.id.select_tags_input)

        // On enter and confirm add tag to list
        tagInput.setOnKeyListener {_, keyCode, event ->
            when {
                ((keyCode == KeyEvent.KEYCODE_ENTER) && (event.action == KeyEvent.ACTION_DOWN)) -> {
                    // String value of tag text input
                    val tagText = tagInput.text.toString()

                    // If input is not empty, add tag and clear input
                    if (tagText.isNotEmpty()) {
                        addTagChip(tagText)
                        tagInput.setText("")
                    }

                    return@setOnKeyListener true
                }
                else -> false
            }
        }

        // Listener for add tag button
        tagButton.setOnClickListener {
            // String value of tag text input
            val tagText = tagInput.text.toString()

            // If input is not empty, add tag and clear input
            if (tagText.isNotEmpty()) {
                addTagChip(tagText)
                tagInput.setText("")
            }
        }

        // Parent view of navigation view
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView.selectedItemId = R.id.navigation_add

        // Switch activity based on pressing navigation buttons
        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(applicationContext, Home::class.java))
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_map -> {
                    startActivity(Intent(applicationContext, Map::class.java))
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_add -> return@OnNavigationItemSelectedListener true
                R.id.navigation_achievements -> {
                    startActivity(Intent(applicationContext, Achievements::class.java))
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_profile -> {
                    startActivity(Intent(applicationContext, Profile::class.java))
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }

    // Function to add tag with chip
    private fun addTagChip(chipText: String) {
        // Chip group view
        val tagChipGroup = findViewById<ChipGroup>(R.id.tag_chip_group)

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
        val chip = Chip(this)

        // Get number of tags already added
        val nTags = tagChipGroup.size

        // Set styling of the chip
        chip.text = chipText
        chip.isCloseIconVisible = true
        chip.chipStrokeWidth = 0F
        chip.chipBackgroundColor = ColorStateList.valueOf(tagColors[nTags])
        chip.setTextColor(getColorStateList(R.color.white))
        chip.closeIconTint = getColorStateList(R.color.white)
        chip.textSize = 16F
        chip.chipIconSize = 16F
        chip.textStartPadding = 2F
        chip.textEndPadding = 3F
        chip.closeIconEndPadding = 4F
        chip.chipEndPadding = 4F
        chip.setEnsureMinTouchTargetSize(false)

        // Set click listeners to remove chip
        chip.setOnCloseIconClickListener {
            tagChipGroup.removeView(chip)
        }
        chip.setOnClickListener {
            tagChipGroup.removeView(chip)
        }

        // Add chip to chip group view
        tagChipGroup.addView(chip)
    }

    private fun getCurrentLocation() {
        // If no access to location
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {

            // Request fine location permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                permissionCode
            )

            return
        }

        // Location provider
        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)

        // Get current location
        fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
            .addOnSuccessListener { location: Location? ->
                if (location == null)
                // TODO: Some error to handle "unable to get location"
                    println("Couldn't get location")
                else {
                    val geoCoder = Geocoder(this, Locale.getDefault())
                    val addresses = geoCoder.getFromLocation(location.latitude, location.longitude, 1)
                    if (addresses != null) {
                        val address = addresses[0].getAddressLine(0)
                        val city = addresses[0].locality

                        // Location text view
                        val locationInput = findViewById<TextView>(R.id.select_location)
                        locationInput.text = address.split(Regex(","))[0] + ", " + city
                    }
                }
            }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode) { permissionCode ->
            // If request code is permission granted
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Set the current map location
                getCurrentLocation()
            }
        }
    }
}