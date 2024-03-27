package com.example.weclean.ui.home

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.weclean.R
import java.io.Serializable

class EventPopup : AppCompatActivity() {

    private fun <T : Serializable?> getSerializable(activity: Activity, name: String, clazz: Class<T>): T
    {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            activity.intent.getSerializableExtra(name, clazz)!!
        else
            activity.intent.getSerializableExtra(name) as T
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_eventpopup)

        val event: EventData = getSerializable(this, "event", EventData::class.java)

        val textCity = findViewById<TextView>(R.id.citypop)
        val numPpl = findViewById<TextView>(R.id.textView7)
        val img = findViewById<ImageView>(R.id.imageView3)
        val date = findViewById<TextView>(R.id.date)
        val time = findViewById<TextView>(R.id.textView9)
        val address = findViewById<TextView>(R.id.address)
        val desc = findViewById<TextView>(R.id.desc)

        //TODO: implement proper event parse
        textCity.text = event.loc?.city ?: "NA"
        numPpl.text = event.numppl.toString()
        img.setImageResource(R.drawable.ic_launcher_background)
        date.text = event.date.toString()
        time.text = event.date.time.toString()
        address.text = event.loc?.address ?: "NA"
        desc.text = event.desc

        val home = findViewById<Button>(R.id.homepage)

        home.setOnClickListener {
            finish()
        }

        val signup = findViewById<Button>(R.id.button2)

        //TODO: implement sign up to event
        signup.setOnClickListener {

        }

        //TODO: implement check if you are part of an event and change button sign up to grayed out








    }
}