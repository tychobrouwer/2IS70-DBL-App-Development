package com.example.weclean.ui.events

import com.example.weclean.R
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.weclean.ui.add.Add
import com.example.weclean.ui.home.EventData
import com.example.weclean.ui.home.EventPopup
import com.example.weclean.ui.home.Home
import com.example.weclean.ui.map.Map
import com.example.weclean.ui.profile.Profile
import com.google.android.material.bottomnavigation.BottomNavigationView


class EventsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_events)

        // Set OnClickListener for the addEvent button
        val buttonClick = findViewById<Button>(R.id.addEvent)
        buttonClick.setOnClickListener {
            startActivity(Intent(this, AddEventActivity::class.java))
        }

        // Parent view of navigation view
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView.selectedItemId = R.id.navigation_events

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
                R.id.navigation_events -> return@OnNavigationItemSelectedListener true
                R.id.navigation_profile -> {
                    startActivity(Intent(applicationContext, Profile::class.java))
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })

        val tl = findViewById<TableLayout>(com.example.weclean.R.id.rows)

        var tempmaxwidth = 0
        var maxwidth = 0
        var tempmaxwidth1 = 0
        var maxwidth1 = 0
        var tempmaxwidth2 = 0
        var maxwidth2 = 0
        var tempmaxwidth3 = 0
        var maxwidth3 = 0

        val eventList = mutableListOf(
            EventData(null),
            EventData(null),
            EventData(null)
        )

        var i: Int = 0
        for (ev: EventData in eventList){
            val newRow = TableRow(applicationContext)
            newRow.setId(i)

            val name = TextView(applicationContext)
            name.text = ev.name
            name.textSize = 16f
            name.setBackgroundResource(R.drawable.blueframe)
            name.measure(0, 0)
            tempmaxwidth = name.measuredWidth

            if (tempmaxwidth > maxwidth) maxwidth = tempmaxwidth

            val date = TextView(applicationContext)
            date.text = ev.date.toString()
            date.textSize = 16f
            date.setBackgroundResource(R.drawable.blueframe)
            date.measure(0, 0)
            tempmaxwidth1 = date.measuredWidth

            if (tempmaxwidth1 > maxwidth1) maxwidth1 = tempmaxwidth1

            val time = TextView(applicationContext)
            time.text = ev.date.toString()
            time.textSize = 16f
            time.setBackgroundResource(R.drawable.blueframe)
            time.measure(0, 0)
            tempmaxwidth2 = time.measuredWidth

            if (tempmaxwidth2 > maxwidth2) maxwidth2 = tempmaxwidth2

            val loc = TextView(applicationContext)
            loc.text = buildString {
                append(ev.loc?.city ?: "NA")
                append(", ")
                append(ev.loc?.address ?: "NA")
            }
            loc.textSize = 16f
            loc.setBackgroundResource(R.drawable.blueframe)
            loc.measure(0, 0)
            tempmaxwidth3 = loc.measuredWidth

            if (tempmaxwidth3 > maxwidth3) maxwidth3 = tempmaxwidth3

            newRow.setOnClickListener {}

            newRow.addView(name)
            newRow.addView(date)
            newRow.addView(time)
            newRow.addView(loc)

            newRow.setOnClickListener {
                val intent = Intent(this, EventPopup::class.java)
                intent.putExtra("event", ev)
                startActivity(intent)
            }

            tl.addView(newRow)
            i++
        }

        val tvh1 = findViewById<View>(R.id.h1) as TextView
        tvh1.setWidth(maxwidth)

        val tvh2 = findViewById<View>(R.id.h2) as TextView
        tvh2.setWidth(maxwidth1)

        val tvh3 = findViewById<View>(R.id.h3) as TextView
        tvh3.setWidth(maxwidth2)

        val tvh4 = findViewById<View>(R.id.h4) as TextView
        tvh4.setWidth(maxwidth3)

    }
}