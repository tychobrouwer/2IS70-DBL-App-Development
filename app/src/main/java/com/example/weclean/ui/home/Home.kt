package com.example.weclean.ui.home

import androidx.fragment.app.Fragment
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import com.example.weclean.ui.add.Add
import com.example.weclean.ui.map.Map
import com.example.weclean.ui.profile.Profile
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weclean.R
import com.example.weclean.backend.FireBase
import com.example.weclean.backend.dayStringFormat
import com.example.weclean.ui.events.EventDetails
import com.example.weclean.ui.events.EventsActivity
import com.example.weclean.ui.events.openEventDetails
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import com.example.weclean.backend.EventData
import com.example.weclean.ui.login.LoginActivity

class Home : AppCompatActivity() {

    private val db = Firebase.firestore
    private val fireBase = FireBase()
    private var events = ArrayList<EventData>()

    // User ID
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_home)


        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView.selectedItemId = R.id.navigation_home

        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> return@OnNavigationItemSelectedListener true
                R.id.navigation_map -> {
                    startActivity(Intent(applicationContext, Map::class.java))
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_add -> {
                    startActivity(Intent(applicationContext, Add::class.java))
                    return@OnNavigationItemSelectedListener true
                }
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

        getEvents()


        val adapter = EventAdapter(events)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        //Frame creation TODO: make a separate function using loop for each frame
        val frameLayout1 = findViewById<FrameLayout>(R.id.frame1)
        val frameLayout2 = findViewById<FrameLayout>(R.id.frame2)

        val inflater = layoutInflater
        val itemLayout1 = inflater.inflate(R.layout.item_cleanedup, frameLayout1, false)
        val itemLayout2 = inflater.inflate(R.layout.item_cleanedup, frameLayout2, false)

        val eventold1 = events[0]
        val eventold2 = events[1]

        // Set the text and image
        //TODO: one data is retrievable put proper information there
        updateFields(itemLayout1, eventold1)

        updateFields(itemLayout2, eventold2)

        itemLayout1.setOnClickListener {
            (this as AppCompatActivity).openEventDetails(true, eventold1)
        }

        itemLayout2.setOnClickListener {
            (this as AppCompatActivity).openEventDetails(true, eventold2)
        }

        // Finally, add the inflated item_layout to your FrameLayout
        frameLayout1.addView(itemLayout1)
        frameLayout2.addView(itemLayout2)

    }

    private fun getEvents() {
        runBlocking {
            launch {
                // Current logged in user
                val userId = fireBase.currentUserId()

                // If no user is logged in or user is empty
                if (userId.isNullOrEmpty()) {
                    Toast.makeText(this as AppCompatActivity, "Unable to get user", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this as AppCompatActivity, LoginActivity::class.java))

                    return@launch
                }

                // Get user data
                val userData = fireBase.getDocument("Users", userId) ?: return@launch
                if (userData.data == null) return@launch

                // Get the communities the user is a part of
                val communities = userData.get("communityIds") as ArrayList<*>? ?: return@launch

                // Get the events for each community
                for (community in communities) {
                    // Get the community data
                    val communityData = fireBase.getDocument("Community", community as String) ?: continue

                    if (communityData.data == null) continue

                    // Get the event IDs for the community
                    val eventsToAdd = communityData.get("eventIds") as ArrayList<*>? ?: continue

                    // Add the events to the list
                    for (event in eventsToAdd) {
                        // Get the event data
                        val eventDataResult = fireBase.getDocument("Events", event as String) ?: continue

                        if (eventDataResult.data == null) continue

                        // Create the event data object
                        val eventData = EventData()
                        eventData.id = eventDataResult.id
                        eventData.name = eventDataResult.getString("name")!!
                        eventData.timeStamp = eventDataResult.getDate("date")!!.time
                        eventData.imageId = eventDataResult.getString("imageId")!!
                        eventData.description = eventDataResult.getString("description")!!
                            .replace("_newline", "\n")
                        eventData.location = eventDataResult.getString("location")!!
                        eventData.community = eventDataResult.getString("community")!!
                        eventData.communityName = communityData.getString("name")!!
                        eventData.numPeople = (eventDataResult.get("userIds") as List<*>?)!!.size

                        // Add the event data to the list
                        events.add(eventData)
                    }
                }
            }
        }
    }

    private fun updateFields(view: View, event: EventData) {
        // Set the fields on the view
        view.findViewById<TextView>(R.id.textView3).text = event.name

        runBlocking {
            launch {
                // Load image from Firebase Storage
                val imageView = view.findViewById<ImageView>(R.id.imageView)

                // Get image bytes from Firebase Storage
                val imageBytes = fireBase.getFileBytes(event.imageId, 1024 * 1024)

                if (imageBytes == null) {
                    Toast.makeText(this@Home, "Failed to load image frame", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Set image view with the image bytes
                val bmp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                imageView.setImageBitmap(bmp)

                // Get the user's event IDs from the database
                //val userEvents = fireBase.getDocument("Users", userId)?.get("eventIds") as List<*>
            }
        }
    }
}
