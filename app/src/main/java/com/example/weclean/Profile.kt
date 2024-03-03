package com.example.weclean

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity

class Profile : AppCompatActivity() {
    val isEditView = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)

        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()

        transaction.add(R.id.fragmentProfileInfo, ProfileInfo())
        transaction.add(R.id.fragmentProfileCommunities, ProfileCommunities())
        transaction.commit()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView.selectedItemId = R.id.navigation_profile

        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(applicationContext, Home::class.java))
                    overridePendingTransition(0,0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_map -> {
                    startActivity(Intent(applicationContext, Map::class.java))
                    overridePendingTransition(0,0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_add -> {
                    startActivity(Intent(applicationContext, Add::class.java))
                    overridePendingTransition(0,0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_achievements -> {
                    startActivity(Intent(applicationContext, Achievements::class.java))
                    overridePendingTransition(0,0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_profile -> return@OnNavigationItemSelectedListener true
            }
            false
        })
    }
}

fun AppCompatActivity.switchEditFragment(toEdit: Boolean) {
    val fragmentManager = supportFragmentManager
    val transaction = fragmentManager.beginTransaction()

    val profileInfo = fragmentManager.findFragmentById(R.id.fragmentProfileInfo)
    val profileEdit = fragmentManager.findFragmentById(R.id.fragmentProfileEdit)
    val profileCommunities = fragmentManager.findFragmentById(R.id.fragmentProfileCommunities)

    if (profileInfo != null && profileEdit != null && profileCommunities != null) {
        transaction.commit()
        return
    }

    if (toEdit) {
        transaction.remove(profileInfo!!)
        transaction.remove(profileCommunities!!)
        transaction.add(R.id.fragmentProfileEdit, ProfileEdit())
    } else {
        transaction.remove(profileEdit!!)
        transaction.add(R.id.fragmentProfileInfo, ProfileInfo())
        transaction.add(R.id.fragmentProfileCommunities, ProfileCommunities())
    }

    transaction.commit()
}
