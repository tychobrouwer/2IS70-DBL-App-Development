package com.example.weclean.ui.events

import android.app.Activity
import com.example.weclean.R
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.weclean.backend.EventData
import com.example.weclean.ui.add.Add
import com.example.weclean.ui.home.Home
import com.example.weclean.ui.map.Map
import com.example.weclean.ui.profile.Profile
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.Serializable

class EventsActivity : AppCompatActivity() {
    private fun <T : Serializable?> getSerializable(activity: Activity, name: String, clazz: Class<T>): T?
    {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            activity.intent.getSerializableExtra(name, clazz)?.let { it as T? }
        else
            activity.intent.getSerializableExtra(name) as T?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_events)

        // Begin activity with correct fragments
        val eventData: EventData? = getSerializable(this, "event", EventData::class.java)
        if (eventData != null) {
            openEventDetails(true, eventData)
        } else {
            openEventDetails(false, EventData())
        }

        // Parent view of navigation view
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView.selectedItemId = R.id.navigation_events

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
                R.id.navigation_add -> {
                    startActivity(Intent(applicationContext, Add::class.java))
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_events -> {
                    openEventDetails(false, EventData())
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
}

/**
 * Open event details fragment and pass event data
 *
 * @param toEvent
 * @param eventData
 */
fun AppCompatActivity.openEventDetails(toEvent: Boolean, eventData: EventData) {
    // Fragment manager and transaction for switching default and edit profile fragments
    val fragmentManager = supportFragmentManager
    val transaction = fragmentManager.beginTransaction()

    // Get views for events list and event details fragments
    val eventDetails = fragmentManager.findFragmentById(R.id.fragmentEventDetails)
    val eventsList = fragmentManager.findFragmentById(R.id.fragmentEventsList)

    if (toEvent) {
        // Remove events list view fragments and add event details fragment
        transaction.remove(eventsList!!)
        transaction.add(R.id.fragmentEventDetails, EventDetails().newInstance(eventData))
    } else {
        // Remove event details fragment and add events list view fragments
        if (eventDetails != null) {
            transaction.remove(eventDetails)
        }
        transaction.add(R.id.fragmentEventsList, EventsList())
    }

    // Complete transaction and navigate
    transaction.commit()
}