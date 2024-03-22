package com.example.weclean.ui.events

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.weclean.R


class AddEventActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_event)

        // Set OnClickListener for the addEvent button
        val buttonClick = findViewById<Button>(R.id.cancelButton)
        buttonClick.setOnClickListener {
            startActivity(Intent(this, EventsActivity::class.java))
        }
    }
}