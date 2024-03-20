package com.example.weclean.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import com.example.weclean.ui.add.Add
import com.example.weclean.ui.map.Map
import com.example.weclean.ui.profile.Profile
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weclean.Achievements
import com.example.weclean.R

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)

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

        //TODO: when the database will be ready implement a proper data retrieve
        val eventList = mutableListOf(
            EventData(null,null, null, null, null, null),
            EventData(null,null, null, null, null, null),
            EventData(null,null, null, null, null, null)
        )

        val adapter = EventAdapter(eventList)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        //Frame creation TODO: make a separate function using loop for each frame
        val frameLayout1 = findViewById<FrameLayout>(R.id.frame1)
        val frameLayout2 = findViewById<FrameLayout>(R.id.frame2)

        val inflater = layoutInflater
        val itemLayout1 = inflater.inflate(R.layout.item_cleanedup, frameLayout1, false)
        val itemLayout2 = inflater.inflate(R.layout.item_cleanedup, frameLayout2, false)

        // Then find the TextView and ImageView in your inflated item_layout
        val textView1 = itemLayout1.findViewById<TextView>(R.id.textView3)
        val imageView1 = itemLayout1.findViewById<ImageView>(R.id.imageView)

        val textView2 = itemLayout2.findViewById<TextView>(R.id.textView3)
        val imageView2 = itemLayout2.findViewById<ImageView>(R.id.imageView)

        // Set the text and image
        //TODO: one data is retrievable put proper information there
        textView1.text = "Sample1"
        imageView1.setImageResource(R.drawable.ic_launcher_background)

        textView2.text = "Sample2"
        imageView2.setImageResource(R.drawable.button_background)

        itemLayout1.setOnClickListener {
            val intent = Intent(this, EventPopup::class.java)
            intent.putExtra("event", EventData(null,null, null, null, null, null))
            startActivity(intent)
        }

        itemLayout2.setOnClickListener {
            val intent = Intent(this, EventPopup::class.java)
            intent.putExtra("event", EventData(null,null, null, null, null, null))
            startActivity(intent)
        }

        // Finally, add the inflated item_layout to your FrameLayout
        frameLayout1.addView(itemLayout1)
        frameLayout2.addView(itemLayout2)

    }


}
