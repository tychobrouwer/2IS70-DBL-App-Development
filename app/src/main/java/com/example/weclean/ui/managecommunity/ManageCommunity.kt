package com.example.weclean.ui.managecommunity

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import com.example.weclean.ui.add.Add
import com.example.weclean.ui.home.Home
import com.example.weclean.R
import com.example.weclean.ui.events.EventsActivity
import com.example.weclean.ui.map.Map
import com.example.weclean.ui.profile.ProfileCommunities
import com.example.weclean.ui.profile.ProfileInfo
import com.example.weclean.ui.profile.ProfileViewStatus
import com.example.weclean.ui.profile.switchFragment

class ManageCommunity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_manage_community)

        // Fragment manager for managing navigation between fragments
        val fragmentManager = supportFragmentManager

        // Begin new transition for fragment
        val transaction = fragmentManager.beginTransaction()

        // Ensure fragments for default profile view are created
        transaction.add(R.id.fragmentCommunityInfo, CommunityInfo())
        transaction.add(R.id.fragmentMembersList, MembersList())
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
                R.id.navigation_events -> {
                    startActivity(Intent(applicationContext, EventsActivity::class.java))
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_profile -> {
                    switchFragment(ProfileViewStatus.PROFILE)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }
}
