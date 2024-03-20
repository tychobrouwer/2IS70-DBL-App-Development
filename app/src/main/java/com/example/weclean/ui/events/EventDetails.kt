package com.example.weclean.ui.events

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.weclean.R

class EventDetails : Fragment() {
    private lateinit var event : EventData

    fun newInstance(event: EventData): EventDetails {
        val fragment = EventDetails()
        fragment.event = event

        return fragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textCity = view.findViewById<TextView>(R.id.citypop)
        val numPpl = view.findViewById<TextView>(R.id.textView7)
        val img = view.findViewById<ImageView>(R.id.imageView3)
        val date = view.findViewById<TextView>(R.id.date)
        val time = view.findViewById<TextView>(R.id.textView9)
        val address = view.findViewById<TextView>(R.id.address)
        val desc = view.findViewById<TextView>(R.id.desc)

        //TODO: implement proper event parse
        textCity.text = event.loc
        numPpl.text = event.numppl.toString()
        img.setImageResource(R.drawable.ic_launcher_background)
        date.text = event.date.toString()
        time.text = event.date?.time.toString()
        address.text = event.loc
        desc.text = event.desc

        val signup = view.findViewById<Button>(R.id.button2)

        //TODO: implement sign up to event
        signup.setOnClickListener {

        }

        //TODO: implement check if you are part of an event and change button sign up to grayed out

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_details, container, false)
    }
}