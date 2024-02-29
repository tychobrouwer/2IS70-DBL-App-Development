package com.example.weclean

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.weclean.databinding.ActivityMapBinding

class Map : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_map)

        val screenLabelTextView = findViewById<TextView>(R.id.screen_label);
        screenLabelTextView.text = "Map"

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView.selectedItemId = R.id.navigation_map

        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(applicationContext, Home::class.java))
                    overridePendingTransition(0,0);
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_map -> return@OnNavigationItemSelectedListener true
                R.id.navigation_add -> {
                    startActivity(Intent(applicationContext, Add::class.java))
                    overridePendingTransition(0,0);
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_achievements -> {
                    startActivity(Intent(applicationContext, Achievements::class.java))
                    overridePendingTransition(0,0);
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_profile -> {
                    startActivity(Intent(applicationContext, Profile::class.java))
                    overridePendingTransition(0,0);
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }
}