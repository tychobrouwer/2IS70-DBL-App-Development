package com.example.weclean

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weclean.ui.Home.Event
import com.example.weclean.ui.Home.EventAdapter

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
                R.id.navigation_profile -> {
                    startActivity(Intent(applicationContext, Profile::class.java))
                    overridePendingTransition(0,0)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })



        val eventList = mutableListOf(
            Event(),
            Event(),
            Event()
        )


        val adapter = EventAdapter(eventList)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

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
        textView1.text = "Sample1"
        imageView1.setImageResource(R.drawable.ic_launcher_background)

        textView2.text = "Sample2"
        imageView2.setImageResource(R.drawable.button_background)

        itemLayout1.setOnClickListener {
            val intent = Intent(this, EventPopup::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        itemLayout2.setOnClickListener {
            val intent = Intent(this, EventPopup::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }


// Finally, add the inflated item_layout to your FrameLayout
        frameLayout1.addView(itemLayout1)
        frameLayout2.addView(itemLayout2)

    }


}