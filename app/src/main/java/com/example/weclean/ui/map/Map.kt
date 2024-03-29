package com.example.weclean.ui.map

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import com.example.weclean.ui.add.Add
import com.example.weclean.ui.home.Home
import com.example.weclean.ui.profile.Profile
import com.example.weclean.R
import com.example.weclean.ui.events.EventsActivity

class Map : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_map)

        // Fragment manager for managing navigation between fragments
        val fragmentManager = supportFragmentManager

        // Begin new transition for fragment
        val transaction = fragmentManager.beginTransaction()

        // Ensure fragments for default profile view are created
        transaction.add(R.id.fragmentMapView, MapView())
        transaction.commit()

        // Parent view of navigation view
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView.selectedItemId = R.id.navigation_map

        // Switch activity based on pressing navigation buttons
        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(applicationContext, Home::class.java))
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_map -> {
                    switchFragment(MapViewStatus.Map)
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
    }
}

/**
 * Switch between fragments for the map view and the littering details view
 *
 * @param toStatus
 * @param documentId
 */
fun AppCompatActivity.switchFragment(toStatus: MapViewStatus, documentId: String = "") {
    // Fragment manager and transaction for switching default and edit profile fragments
    val fragmentManager = supportFragmentManager
    val transaction = fragmentManager.beginTransaction()

    // Get views for default profile, edit profile, and communities list views
    val mapView = fragmentManager.findFragmentById(R.id.fragmentMapView)
    val litteringDetails = fragmentManager.findFragmentById(R.id.fragmentLitteringDetails)

    // Null check on fragments
    if (mapView != null && litteringDetails != null) {
        // Cancel transaction
        transaction.commit()
        return
    }

    if (toStatus == MapViewStatus.Map) {
        // Remove default profile view fragments and add profile edit fragment
        transaction.remove(litteringDetails!!)
        transaction.add(R.id.fragmentMapView, MapView())
    } else if (toStatus == MapViewStatus.LitteringDetails) {
        transaction.remove(mapView!!)
        transaction.add(R.id.fragmentLitteringDetails, LitteringDetails().newInstance(documentId))
    }

    // Complete transaction and navigate
    transaction.commit()
}