package com.example.weclean.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import com.example.weclean.ui.add.Add
import com.example.weclean.ui.map.Map
import com.example.weclean.ui.profile.Profile
import com.example.weclean.Achievements
import com.example.weclean.R
import com.example.weclean.ui.events.EventData
import com.example.weclean.ui.events.EventDetails
import com.example.weclean.ui.profile.ProfileCommunities
import com.example.weclean.ui.profile.ProfileInfo
import java.util.Date

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)

        // Fragment manager for managing navigation between fragments
        val fragmentManager = supportFragmentManager

        // Begin new transition for fragment
        val transaction = fragmentManager.beginTransaction()

        // Ensure fragments for default profile view are created
        transaction.add(R.id.fragmentHomeEvents, HomeEvents())
        transaction.commit()

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
}

fun AppCompatActivity.openEventDetails(toEvent: Boolean, eventData: EventData) {
    // Fragment manager and transaction for switching default and edit profile fragments
    val fragmentManager = supportFragmentManager
    val transaction = fragmentManager.beginTransaction()

    // Get views for default profile, edit profile, and communities list views
    val eventDetails = fragmentManager.findFragmentById(R.id.fragmentEventDetails)
    val homeEvents = fragmentManager.findFragmentById(R.id.fragmentHomeEvents)

    // Null check on fragments
    if (eventDetails != null &&
        homeEvents != null) {

        // Cancel transaction
        transaction.commit()
        return
    }

    if (toEvent) {
        // Remove default profile view fragments and add profile edit fragment
        transaction.remove(homeEvents!!)
        transaction.add(R.id.fragmentEventDetails, EventDetails().newInstance(eventData))
    } else {
        // Remove profile edit fragment and add default profile view fragments
        transaction.remove(eventDetails!!)
        transaction.add(R.id.fragmentHomeEvents, HomeEvents())
    }

    // Complete transaction and navigate
    transaction.commit()
}

