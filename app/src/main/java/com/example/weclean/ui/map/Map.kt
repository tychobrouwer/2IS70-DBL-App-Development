package com.example.weclean.ui.map

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.weclean.ui.add.Add
import com.example.weclean.ui.home.Home
import com.example.weclean.ui.profile.Profile
import com.example.weclean.R
import com.example.weclean.backend.LitteringData
import com.example.weclean.ui.events.EventsActivity
import java.io.Serializable

class Map : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {
    private fun <T : Serializable?> getSerializable(activity: Activity, name: String, clazz: Class<T>): LitteringData?
    {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            activity.intent.getSerializableExtra(name, clazz)?.let { it as LitteringData? }
        else
            activity.intent.getSerializableExtra(name) as LitteringData?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_map)

        // Begin activity with correct fragments based on optional serialized data
        val litteringData: LitteringData? = getSerializable(this, "littering", LitteringData::class.java)
        if (litteringData != null) {
            switchFragment(MapViewStatus.LitteringDetails, litteringData.id)
        } else {
            switchFragment(MapViewStatus.Map)
        }

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

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val requestPermissionCode = 999
        if (requestCode == requestPermissionCode && permissions.isNotEmpty() && grantResults.isNotEmpty()) {
            // Reload the map view after permissions are granted
            switchFragment(MapViewStatus.Map)
        }
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

    // Remove all fragments
    try {
        transaction.remove(mapView!!)
    } catch (_: Exception) {}
    try {
        transaction.remove(litteringDetails!!)
    } catch (_: Exception) {}

    if (toStatus == MapViewStatus.Map) {
        // Add map view fragment
        transaction.add(R.id.fragmentMapView, MapView())
    } else if (toStatus == MapViewStatus.LitteringDetails) {
        // Add littering details fragment
        transaction.add(R.id.fragmentLitteringDetails, LitteringDetails().newInstance(documentId))
    }

    // Complete transaction and navigate
    transaction.commit()
}
