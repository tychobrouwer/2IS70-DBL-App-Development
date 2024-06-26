package com.example.weclean.ui.add

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.KeyEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.size
import androidx.core.widget.addTextChangedListener
import com.example.weclean.R
import com.example.weclean.backend.FireBase
import com.example.weclean.backend.LitteringData
import com.example.weclean.ui.events.AddEvent
import com.example.weclean.ui.events.EventsActivity
import com.example.weclean.ui.home.Home
import com.example.weclean.ui.login.LoginActivity
import com.example.weclean.ui.map.Map
import com.example.weclean.ui.profile.Profile
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.Locale

class Add : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    companion object {
        private var permissionCode = 101
        private const val CAMERA_PERMISSION_CODE = 1
        private const val CAMERA = 2
    }

    // Geocoder used for getting location information
    private lateinit var geocoder : Geocoder
    // Object for storing the new littering data entry
    private lateinit var litteringData : LitteringData

    // Adapter for the spinner of communities
    private lateinit var selectCommunityAdapter: ArrayAdapter<String>

    // List of communities the user is in
    private var communities = ArrayList<String>()
    private var communitiesName = ArrayList<String>()

    // Uri of the photo taken
    private var photoUri: Uri? = null
    // Boolean to check if image is added to the littering data
    private var imageAdded = false

    // FireBase class instance to communicate with the database
    private val fireBase = FireBase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add)

        // Initialize geocoder and littering data objects
        geocoder = Geocoder(this, Locale.getDefault())
        litteringData = LitteringData(geocoder)

        // Get dropdown for selecting communities
        val selectCommunitySpinner = findViewById<Spinner>(R.id.select_community)
        // Create adapter with list of communities
        selectCommunityAdapter = ArrayAdapter(
            this, R.layout.spinner_item, communitiesName)

        // Set adapter for communities selector
        selectCommunityAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        selectCommunityAdapter.setNotifyOnChange(true)

        // Configure and set the dropdown
        selectCommunitySpinner.adapter = selectCommunityAdapter
        selectCommunitySpinner.onItemSelectedListener = this

        // Get the current location of the user
        getCurrentLocation()
        // Set the communities the user is in
        setCommunities()

        // Description text input field
        val descriptionInput = findViewById<EditText>(R.id.description)
        descriptionInput.addTextChangedListener {
            // Set description of littering data object to changed value
            litteringData.description = descriptionInput.text.toString()
        }

        //Add camera button
        val camera = findViewById<ImageButton>(R.id.AddCamera)
        //Listener for camera button
        camera.setOnClickListener {
            if(ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA)
                ==PackageManager.PERMISSION_GRANTED
            ){
                openCamera()
            } else{
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE
                )
            }
        }

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

        // Confirm button to send data to Firebase
        val addLitteringButton = findViewById<Button>(R.id.add_littering_button)
        addLitteringButton.setOnClickListener { sendEntryToFirebase() }

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
                R.id.navigation_events -> {
                    startActivity(Intent(applicationContext, EventsActivity::class.java))
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

    /**
     * Set the communities the user is in
     *
     */
    private fun setCommunities() {
        runBlocking {
            launch {
                // Get the current user id
                val userId = fireBase.currentUserId() ?: return@launch

                // Get communities the user is in
                val communitiesResult = fireBase.getDocument("Users", userId)

                if (communitiesResult == null) {
                    Toast.makeText(this@Add, "Error getting user", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Get the community ids the user is in
                val userCommunityIds = communitiesResult.get("communityIds") as? ArrayList<*> ?: emptyList()
                for (community in userCommunityIds) {
                    // Get the community data
                    val communityResult =
                        fireBase.getDocument("Community", community as String) ?: return@launch

                    // If community data is empty continue
                    if (communityResult.data == null) continue

                    // Add the community to the list
                    communitiesName.add(communityResult.getString("name")!!)
                    communities.add(communityResult.id)
                }

                // If user is not in any community
                if (communities.isEmpty()) {
                    communities.add("")
                    communitiesName.add("No community")
                }

                // Update the spinner adapter
                selectCommunityAdapter.notifyDataSetChanged()
            }
        }
    }

    /**
     * Send littering entry to the Firebase database
     *
     */
    private fun sendEntryToFirebase() {
        // Current logged in user
        val userId = fireBase.currentUserId()

        // If no user is logged in or user is empty
        if (userId.isNullOrEmpty()) {
            Toast.makeText(this, "Unable to get user", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))

            return
        } else if (photoUri == null || !imageAdded) {
            Toast.makeText(this, "Please add an image", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a unique id for the image
        val imageFirebaseId = "${userId},${System.currentTimeMillis()}"
        litteringData.imageId = imageFirebaseId

        runBlocking {
            // Add image to the FireBase storage
            val resultImage = fireBase.addFileWithUri(imageFirebaseId, photoUri!!)

            if (resultImage == null) {
                Toast.makeText(this@Add, "Error uploading image", Toast.LENGTH_SHORT).show()
                return@runBlocking
            }

            // Upload the littering data to the FireBase database
            val resultLitteringData = fireBase.addDocument(
                "LitteringData", litteringData.createLitteringData(userId))

            if (resultLitteringData == null) {
                Toast.makeText(this@Add, "Error creating littering entry", Toast.LENGTH_SHORT).show()
                return@runBlocking
            }

            litteringData.id = resultLitteringData.id

            // Add littering entry id to the user
            val resultUser = fireBase.addToArray(
                "Users", userId, "litteringEntries", litteringData.id)

            if (!resultUser) {
                Toast.makeText(this@Add, "Error updating user", Toast.LENGTH_SHORT).show()
                return@runBlocking
            }

            if (!communitiesName.contains("No community")) {
                // Add littering entry id to the community
                val resultCommunity = fireBase.addToArray(
                    "Community", litteringData.community, "litteringEntries", resultLitteringData.id)

                if (!resultCommunity) {
                    Toast.makeText(this@Add, "Error updating community", Toast.LENGTH_SHORT).show()
                    return@runBlocking
                }
            }

            Toast.makeText(this@Add, "Littering entry added", Toast.LENGTH_SHORT).show()

            startActivity(Intent(this@Add, Map::class.java))
        }
    }

    /**
     * Add chip to the ChipGroup and update litteringData
     *
     * @param chipText
     */
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
            // Remove tag to littering data object
            litteringData.removeTag(chipText)

            // Remove tag from view
            tagChipGroup.removeView(chip)
        }
        chip.setOnClickListener {
            // Remove tag to littering data object
            litteringData.removeTag(chipText)

            // Remove tag from view
            tagChipGroup.removeView(chip)
        }

        // Add chip to chip group view
        tagChipGroup.addView(chip)

        // Add tag to littering data object
        litteringData.addTag(chipText)
    }

    /**
     * Get the current location of the user and update litteringData object
     *
     */
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
                if (location == null) {
                    Toast.makeText(this, "Could not get location", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                // Update location of the littering data object
                litteringData.updateLocation(location.latitude, location.longitude)

                // Update location input field
                val locationInput = findViewById<TextView>(R.id.select_location)
                locationInput.text = litteringData.getAddressLine()
            }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 2 && resultCode == RESULT_OK) {
            imageAdded = true
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
        // Set community of litteringData object
        litteringData.community =  communities[position]
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        Toast.makeText(this, "No community selected", Toast.LENGTH_SHORT).show()
    }

    /**
     * Open the camera to take a photo
     *
     */
    private fun openCamera() {
        val photoFile = File.createTempFile(
            "JPEG_${System.currentTimeMillis()}_",
            ".jpg", /* suffix */
            getExternalFilesDir(Environment.DIRECTORY_PICTURES),
        )

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Get the uri for the photo file
        photoUri = FileProvider.getUriForFile(
            applicationContext,
            "$packageName.fileprovider",
            photoFile)

        // Add the uri to the intent
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        // Start the camera activity
        startActivityForResult(intent, CAMERA)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_CODE && permissions.isNotEmpty() && grantResults.isNotEmpty()) {
            openCamera()
        } else {
            Toast.makeText(this,"Permission needs to be accepted", Toast.LENGTH_LONG).show()
        }
    }
}
