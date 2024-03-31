package com.example.weclean.ui.events

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import com.example.weclean.R
import com.example.weclean.backend.EventData
import com.example.weclean.backend.FireBase
import com.example.weclean.ui.login.LoginActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.Calendar

class AddEvent : AppCompatActivity(), AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    // FireBase class to communicate with the database
    private val fireBase = FireBase()

    // Adapter for the communities selector
    private lateinit var selectCommunityAdapter: ArrayAdapter<String>

    // Object for storing the new event data entry
    private var eventData = EventData()

    // List of communities the user is in
    private var communities = ArrayList<String>()
    private var communitiesName = ArrayList<String>()

    // Uri of the photo taken
    private var photoUri: Uri? = null
    // Boolean to check if image is added to the event data
    private var imageAdded = false

    // Calendar object for the date and time of the event
    private var date = Calendar.getInstance()

    companion object {
        private const val CAMERA_PERMISSION_CODE = 1
        private const val CAMERA = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_event)

        // Set the default values for the event data
        eventData.numPeople = 0;

        // Set OnClickListener for the cancel button
        val buttonClick = findViewById<Button>(R.id.cancelButton)
        buttonClick.setOnClickListener {
            finish()
        }

        // Get the spinner for the communities selector
        val selectCommunitySpinner = findViewById<Spinner>(R.id.selectCommunity)
        // Create adapter with list of communities
        selectCommunityAdapter = ArrayAdapter(
            this, R.layout.spinner_item, communitiesName)

        // Set adapter for communities selector
        selectCommunityAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        selectCommunityAdapter.setNotifyOnChange(true)

        // Configure and set the dropdown
        selectCommunitySpinner.adapter = selectCommunityAdapter
        selectCommunitySpinner.onItemSelectedListener = this

        // Get the list of communities the user is in
        getCommunities()

        // Set the event name data field and listener
        val nameView = findViewById<EditText>(R.id.eventName)
        nameView.addTextChangedListener {
            eventData.name = nameView.text.toString()
        }

        // Set the event description data field and listener
        val descriptionView = findViewById<EditText>(R.id.description)
        descriptionView.addTextChangedListener {
            eventData.description = descriptionView.text.toString()
        }

        // Set the event location data field and listener
        val locationView = findViewById<EditText>(R.id.location)
        locationView.addTextChangedListener {
            eventData.location = locationView.text.toString()
        }

        // Set date and time picker for the event
        val dateAndTimeView = findViewById<TextView>(R.id.dateAndTime)
        dateAndTimeView.setOnClickListener {
            // Create a date picker dialog
            val datePicker = DatePickerDialog(
                this,
                this,
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH))

            // Show the date picker dialog
            datePicker.show()
        }

        // Confirm add event button
        val addEventButton = findViewById<Button>(R.id.addEventButton)
        addEventButton.setOnClickListener { sendEntryToFirebase() }

        //Add camera button
        val camera = findViewById<ImageButton>(R.id.AddCamera)
        //Listener for camera button
        camera.setOnClickListener {
            if(ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
            ){
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
            } else{
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE
                )
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 2 && resultCode == RESULT_OK) {
            imageAdded = true
        }
    }

    /**
     * Get the communities the user is in
     *
     */
    private fun getCommunities() {
        runBlocking {
            launch {
                // Current logged in user
                val userId = fireBase.currentUserId()

                // If no user is logged in or user is empty
                if (userId.isNullOrEmpty()) {
                    Toast.makeText(this@AddEvent, "Unable to get user", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@AddEvent, LoginActivity::class.java))

                    return@launch
                }

                // Get communities the user is in
                val communitiesResult = fireBase.getDocument("Users", userId)

                if (communitiesResult == null || communitiesResult.data == null) {
                    Toast.makeText(this@AddEvent, "Error getting user", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Get the community ids the user is in
                val userCommunityIds = communitiesResult.get("communityAdminIds") as ArrayList<*>

                if (userCommunityIds.isEmpty()) {
                    Toast.makeText(this@AddEvent, "User is not an admin of a community", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                for (community in userCommunityIds) {
                    // Get the community data
                    val communityResult =
                        fireBase.getDocument("Community", community as String) ?: return@launch
                    if (communityResult.data == null) continue

                    // Add the community to the list
                    communitiesName.add(communityResult.getString("name")!!)
                    communities.add(communityResult.id)
                }

                // Update the adapter
                selectCommunityAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        eventData.community =  communities[position]
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Toast.makeText(this, "No community selected", Toast.LENGTH_SHORT).show()
        eventData.community = "No community"
    }

    /**
     * Send event entry to the Firebase database
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
        eventData.imageId = imageFirebaseId

        runBlocking {
            // Add image to the FireBase storage
            val resultImage = fireBase.addFileWithUri(imageFirebaseId, photoUri!!)

            if (resultImage == null) {
                Toast.makeText(this@AddEvent, "Error uploading image", Toast.LENGTH_SHORT).show()
                return@runBlocking
            }

            // Upload the event data to the FireBase database
            val resultEventData = fireBase.addDocument(
                "Events", eventData.createEventData(userId))

            if (resultEventData == null) {
                Toast.makeText(this@AddEvent, "Error creating event entry", Toast.LENGTH_SHORT).show()
                return@runBlocking
            }

            // Add event entry id to the user
            val resultUser = fireBase.addToArray(
                "Users", userId, "eventIds", resultEventData.id)

            if (!resultUser) {
                Toast.makeText(this@AddEvent, "Error updating user", Toast.LENGTH_SHORT).show()
                return@runBlocking
            }

            // Add event entry id to the community
            val resultCommunity = fireBase.addToArray(
                "Community", eventData.community, "eventIds", resultEventData.id)

            if (!resultCommunity) {
                Toast.makeText(this@AddEvent, "Error updating community", Toast.LENGTH_SHORT).show()
                return@runBlocking
            }

            Toast.makeText(this@AddEvent, "Event entry added", Toast.LENGTH_SHORT).show()
            startActivity(Intent(applicationContext, EventsActivity::class.java))
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        // Set the date of the event from the date picker
        date.set(Calendar.YEAR, year)
        date.set(Calendar.MONTH, month)
        date.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        // Create a time picker dialog
        val timePicker = TimePickerDialog(
            this,
            this,
            date.get(Calendar.HOUR_OF_DAY),
            date.get(Calendar.MINUTE),
            true)

        // Show the time picker dialog
        timePicker.show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        // Set the time of the event from the time picker
        date.set(Calendar.HOUR_OF_DAY, hourOfDay)
        date.set(Calendar.MINUTE, minute)

        eventData.timeStamp = date.timeInMillis

        // Set the date and time field string
        val dateAndTimeView = findViewById<TextView>(R.id.dateAndTime)
        dateAndTimeView.text = getString(
            R.string.date_time_format,
            date.get(Calendar.MONTH) + 1,
            date.get(Calendar.DAY_OF_MONTH),
            date.get(Calendar.YEAR),
            date.get(Calendar.HOUR_OF_DAY),
            date.get(Calendar.MINUTE))
    }
}