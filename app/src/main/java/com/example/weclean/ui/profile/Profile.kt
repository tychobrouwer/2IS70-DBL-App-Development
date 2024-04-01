package com.example.weclean.ui.profile

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import com.example.weclean.ui.add.Add
import com.example.weclean.ui.home.Home
import com.example.weclean.R
import com.example.weclean.ui.events.EventsActivity
import com.example.weclean.ui.map.Map

class Profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)

        // Begin activity with correct fragments
        switchFragment(ProfileViewStatus.PROFILE)

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

/**
 * Switch between profile fragments
 *
 * @param toStatus
 */
fun AppCompatActivity.switchFragment(toStatus: ProfileViewStatus, communityData: CommunityListData? = null) {
    // Fragment manager and transaction for switching default and edit profile fragments
    val fragmentManager = supportFragmentManager
    val transaction = fragmentManager.beginTransaction()

    // Get views for default profile, edit profile, and communities list views
    val profileInfo = fragmentManager.findFragmentById(R.id.fragmentProfileInfo)
    val profileEdit = fragmentManager.findFragmentById(R.id.fragmentProfileEdit)
    val profileCommunities = fragmentManager.findFragmentById(R.id.fragmentProfileCommunities)
    val profileCreateCommunity = fragmentManager.findFragmentById(R.id.fragmentCreateCommunity)
    val profileManageCommunity = fragmentManager.findFragmentById(R.id.fragmentManageCommunity)

    // Null check on fragments
    if (profileInfo != null &&
        profileEdit != null &&
        profileCommunities != null &&
        profileCreateCommunity != null &&
        profileManageCommunity != null) {

        // Cancel transaction
        transaction.commit()
        return
    }

    // Remove all fragments
    try {
        transaction.remove(profileInfo!!)
    } catch (_: Exception) {}
    try {
        transaction.remove(profileCommunities!!)
    } catch (_: Exception) {}
    try {
        transaction.remove(profileEdit!!)
    } catch (_: Exception) {}
    try {
        transaction.remove(profileCreateCommunity!!)
    } catch (_: Exception) {}
    try {
        transaction.remove(profileManageCommunity!!)
    } catch (_: Exception) {}

    // Add fragment based on status
    when (toStatus) {
        ProfileViewStatus.PROFILE_EDIT -> {
            transaction.add(R.id.fragmentProfileEdit, ProfileEdit())
        }
        ProfileViewStatus.PROFILE -> {
            transaction.add(R.id.fragmentProfileInfo, ProfileInfo())
        }
        ProfileViewStatus.COMMUNITIES_LIST -> {
            transaction.add(R.id.fragmentProfileCommunities, ProfileCommunities())
        }
        ProfileViewStatus.COMMUNITY_CREATE -> {
            transaction.add(R.id.fragmentCreateCommunity, CreateCommunity())
        }
        ProfileViewStatus.COMMUNITY_MANAGE -> {
            transaction.add(R.id.fragmentManageCommunity, ManageCommunity().newInstance(communityData!!))
        }
    }

    // Complete transaction and navigate
    transaction.commit()
}