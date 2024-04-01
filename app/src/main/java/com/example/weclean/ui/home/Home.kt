package com.example.weclean.ui.home

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weclean.R
import com.example.weclean.backend.EventData
import com.example.weclean.backend.FireBase
import com.example.weclean.ui.add.Add
import com.example.weclean.ui.events.EventsActivity
import com.example.weclean.ui.login.LoginActivity
import com.example.weclean.ui.map.Map
import com.example.weclean.ui.profile.Profile
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class Home : AppCompatActivity() {

    private val fireBase = FireBase()
    private var events = ArrayList<EventData>()

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

        val currentTime = System.currentTimeMillis()
        val (pastEvents, futureEvents) = events.partition { it.timeStamp <= currentTime }



        val adapter = EventAdapter(futureEvents)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        val frameLayout1 = findViewById<FrameLayout>(R.id.frame1)
        val frameLayout2 = findViewById<FrameLayout>(R.id.frame2)

        val inflater = layoutInflater
        val itemLayout1 = inflater.inflate(R.layout.item_cleanedup, frameLayout1, false)
        val itemLayout2 = inflater.inflate(R.layout.item_cleanedup, frameLayout2, false)

        for ((i, event) in pastEvents.withIndex()) {
            if (i == 0) {
                updateFields(itemLayout1, event)
                itemLayout1.setOnClickListener {
                    val intent = Intent(this, EventsActivity::class.java)
                    intent.putExtra("event", event)
                    startActivity(intent)
                }
                frameLayout1.addView(itemLayout1)
            } else if (i == 1) {
                updateFields(itemLayout2, event)
                itemLayout2.setOnClickListener {
                    val intent = Intent(this, EventsActivity::class.java)
                    intent.putExtra("event", event)
                    startActivity(intent)
                }
                frameLayout2.addView(itemLayout2)
            }
        }
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
                val communities = userData["communityIds"] as ArrayList<*>? ?: return@launch

                // Get the events for each community
                for (community in communities) {
                    // Get the community data
                    val communityData = fireBase.getDocument("Community", community as String) ?: continue

                    // Get the event IDs for the community
                    val eventsToAdd = communityData["eventIds"] as ArrayList<*>? ?: continue

                    // Add the events to the list
                    for (event in eventsToAdd) {
                        // Get the event data
                        val eventDataResult = fireBase.getDocument("Events", event as String) ?: continue

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
                        eventData.numPeople = (eventDataResult["userIds"] as List<*>?)!!.size

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
