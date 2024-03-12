package com.example.weclean.ui.profile

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import com.example.weclean.Achievements
import com.example.weclean.ui.add.Add
import com.example.weclean.ui.Home.Home
import com.example.weclean.R
import com.example.weclean.ui.map.Map

class Profile : AppCompatActivity() {
    val isEditView = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)

        // Fragment manager for managing navigation between fragments
        val fragmentManager = supportFragmentManager

        // Begin new transition for fragment
        val transaction = fragmentManager.beginTransaction()

        // Ensure fragments for default profile view are created
        transaction.add(R.id.fragmentProfileInfo, ProfileInfo())
        transaction.add(R.id.fragmentProfileCommunities, ProfileCommunities())
        transaction.commit()

        // Parent view of navigation view
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView.selectedItemId = R.id.navigation_profile

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
                R.id.navigation_achievements -> {
                    startActivity(Intent(applicationContext, Achievements::class.java))
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_profile -> return@OnNavigationItemSelectedListener true
            }
            false
        })
    }
}

fun AppCompatActivity.switchEditFragment(toEdit: Boolean) {
    // Fragment manager and transaction for switching default and edit profile fragments
    val fragmentManager = supportFragmentManager
    val transaction = fragmentManager.beginTransaction()

    // Get views for default profile, edit profile, and communities list views
    val profileInfo = fragmentManager.findFragmentById(R.id.fragmentProfileInfo)
    val profileEdit = fragmentManager.findFragmentById(R.id.fragmentProfileEdit)
    val profileCommunities = fragmentManager.findFragmentById(R.id.fragmentProfileCommunities)

    // Null check on fragments
    if (profileInfo != null && profileEdit != null && profileCommunities != null) {
        // Cancel transaction
        transaction.commit()
        return
    }

    if (toEdit) {
        // Remove default profile view fragments and add profile edit fragment
        transaction.remove(profileInfo!!)
        transaction.remove(profileCommunities!!)
        transaction.add(R.id.fragmentProfileEdit, ProfileEdit())
    } else {
        // Remove profile edit fragment and add default profile view fragments
        transaction.remove(profileEdit!!)
        transaction.add(R.id.fragmentProfileInfo, ProfileInfo())
        transaction.add(R.id.fragmentProfileCommunities, ProfileCommunities())
    }

    // Complete transaction and navigate
    transaction.commit()
}
