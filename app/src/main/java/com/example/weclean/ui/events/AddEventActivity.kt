package com.example.weclean.ui.events

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import com.example.weclean.R
import com.example.weclean.backend.FireBase
import com.example.weclean.backend.Location
import com.example.weclean.backend.LocationImpl
import com.example.weclean.ui.home.EventData
import com.example.weclean.ui.home.Home
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.Locale


class AddEventActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    var eventData : EventData = EventData(null)

    private var communities = ArrayList<Pair<String, String>>()

    private val fireBase = FireBase()

    private var longitude = 0.0;
    private var latidude = 0.0;

    // Uri of the photo taken
    private var photoUri: Uri? = null
    // Boolean to check if image is added to the littering data
    private var imageAdded = false

    companion object {
        private var permissionCode = 101
        private const val CAMERA_PERMISSION_CODE = 1
        private const val CAMERA = 2
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_event)

        eventData.numppl = 0;

        // Set OnClickListener for the cancel button
        val buttonClick = findViewById<Button>(R.id.cancelButton)
        buttonClick.setOnClickListener {
            finish()
        }

        setCommunities()

        val selectCommunitySpinner = findViewById<Spinner>(R.id.selectCommunity)
        // Create adapter with list of communities
        val selectCommunityAdapter = ArrayAdapter(
            this, R.layout.spinner_item, communities.map { it.first })

        // Set adapter for communities selector
        selectCommunityAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        selectCommunityAdapter.setNotifyOnChange(true)

        // Configure and set the dropdown
        selectCommunitySpinner.adapter = selectCommunityAdapter
        selectCommunitySpinner.onItemSelectedListener = this

        val name = findViewById<EditText>(R.id.eventName)
        name.addTextChangedListener {
            eventData.name = name.text.toString()

        }



        val date = findViewById<EditText>(R.id.dateAndTime)
        date.setOnClickListener{
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
                // The user has selected a date. Show the TimePickerDialog.
                val timePickerDialog = TimePickerDialog(this, { _, hour, minute ->
                    // The user has selected a time. You can now create a Calendar or Date object
                    // using the selected year, month, day, hour, and minute, and then fetch data
                    // based on this date and time.

                    val selectedCalendar = Calendar.getInstance()
                    selectedCalendar.set(year, month, day, hour, minute)

                    val selectedDate = selectedCalendar.time

                    eventData.date = selectedDate

                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false)

                timePickerDialog.show()

            }, year, month, day)

            datePickerDialog.show()


        }

        //I have no idea what any of this does
////        val autocompleteFragment =
////            supportFragmentManager.findFragmentById(R.id.Location)
////                    as AutocompleteSupportFragment
////
////        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))
////
////        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
////            override fun onPlaceSelected(place: Place) {
////                val placeFields: List<Place.Field> = listOf(Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS)
////
////                // Construct a request object, passing the place ID and fields array.
////                val request = FetchPlaceRequest.builder(place.id, placeFields).build()
////
////                // Issue the request to the Places Geo Data API.
////                val placesClient = Places.createClient(this)
////                placesClient.fetchPlace(request).addOnSuccessListener { response ->
////                    val place = response.place
////                    Log.i(TAG, "Place found: ${place.name}")
////                    Log.i(TAG, "Latitude: ${place.latLng?.latitude}")
////                    Log.i(TAG, "Longitude: ${place.latLng?.longitude}")
////                    Log.i(TAG, "Address: ${place.address}")
////
////                    // You can extract address components (city, country, etc.) from the place.addressComponents field
////                    // and set them in your LocationImpl object.
////                }.addOnFailureListener { exception ->
////                    if (exception is ApiException) {
////                        val apiException = exception as ApiException
////                        Log.e(TAG, "Place not found: " + apiException.statusCode)
////                    }
////                }
////            }
//
//            override fun onError(status: Status) {
//                Log.i(TAG, "An error occurred: $status")
//            }
//        })



        val long = findViewById<EditText>(R.id.longitude)
        long.addTextChangedListener {
            longitude = long.text.toString().toDouble()

        }

        val lat = findViewById<EditText>(R.id.latitude)
        lat.addTextChangedListener {
            latidude = lat.text.toString().toDouble()

        }

        val desc = findViewById<EditText>(R.id.description)
        desc.addTextChangedListener {
            eventData.desc = desc.text.toString()

        }

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
        val geocoder = Geocoder(this, Locale.getDefault())
        val locat = LocationImpl(geocoder)

        val button = findViewById<Button>(R.id.addButton)
        button.setOnClickListener {
            locat.updateLocation(latidude, longitude)
            eventData.loc = locat
            sendEntryToFirebase()


        }

    }

    private fun setCommunities() {
        runBlocking {
            launch {
                // Get communities the user is in
                val communitiesResult = fireBase.getDocument("Users", fireBase.currentUserId())

                if (communitiesResult == null) {
                    Toast.makeText(this@AddEventActivity, "Error getting user", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Get the community ids the user is in
                val userCommunityIds = communitiesResult.get("communityIds") as? ArrayList<*> ?: emptyList()
                for (community in userCommunityIds) {
                    // Get the community data
                    val communityResult =
                        fireBase.getDocument("Community", community as String) ?: return@launch

                    // Add the community to the list
                    communities.add(Pair(communityResult.getString("name")!!, communityResult.id))
                }
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        eventData.community =  communities[position].second
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Toast.makeText(this, "No community selected", Toast.LENGTH_SHORT).show()
        eventData.community = "No community"
    }

    private fun sendEntryToFirebase() {
        // Current logged in user
        val userId = fireBase.currentUserId()

        // If no user is logged in or user is empty
        if (userId.isEmpty()) {
            Toast.makeText(this, "Unable to get user", Toast.LENGTH_SHORT).show()
            return
        } else if (photoUri == null || !imageAdded) {
            Toast.makeText(this, "Please add an image", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a unique id for the image
        val imageFirebaseId = "${userId},${System.currentTimeMillis()}"
        eventData.image.imageId = imageFirebaseId

        runBlocking {
            // Add image to the FireBase storage
            val resultImage = fireBase.addFileWithUri(imageFirebaseId, photoUri!!)

            if (resultImage == null) {
                Toast.makeText(this@AddEventActivity, "Error uploading image", Toast.LENGTH_SHORT).show()
                return@runBlocking
            }

            // Upload the littering data to the FireBase database
            val resultLitteringData = fireBase.addDocument(
                "LitteringData", eventData.createEventData())

            if (resultLitteringData == null) {
                Toast.makeText(this@AddEventActivity, "Error creating littering entry", Toast.LENGTH_SHORT).show()
                return@runBlocking
            }


            Toast.makeText(this@AddEventActivity, "Event created", Toast.LENGTH_SHORT).show()
            startActivity(Intent(applicationContext, Home::class.java))
        }
    }
}