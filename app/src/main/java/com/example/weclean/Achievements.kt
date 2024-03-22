package com.example.weclean

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import com.example.weclean.ui.home.Home
import com.example.weclean.ui.add.Add
import com.example.weclean.ui.map.Map
import com.example.weclean.ui.profile.Profile

class Achievements : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_achievements)

//        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)
//        bottomNavigationView.selectedItemId = R.id.navigation_add
//
//        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.navigation_home -> {
//                    startActivity(Intent(applicationContext, Home::class.java))
//                    return@OnNavigationItemSelectedListener true
//                }
//                R.id.navigation_map -> {
//                    startActivity(Intent(applicationContext, Map::class.java))
//                    return@OnNavigationItemSelectedListener true
//                }
//                R.id.navigation_add -> {
//                    startActivity(Intent(applicationContext, Add::class.java))
//                    return@OnNavigationItemSelectedListener true
//                }
//                R.id.navigation_events -> return@OnNavigationItemSelectedListener true
//                R.id.navigation_profile -> {
//                    startActivity(Intent(applicationContext, Profile::class.java))
//                    return@OnNavigationItemSelectedListener true
//                }
//            }
//            false
//        })
    }
}