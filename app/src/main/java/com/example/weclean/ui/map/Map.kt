package com.example.weclean.ui.map

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import com.example.weclean.Achievements
import com.example.weclean.ui.add.Add
import com.example.weclean.ui.home.Home
import com.example.weclean.ui.profile.Profile
import com.example.weclean.R
import com.example.weclean.ui.profile.ProfileCommunities
import com.example.weclean.ui.profile.ProfileInfo

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
                R.id.navigation_map -> return@OnNavigationItemSelectedListener true
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