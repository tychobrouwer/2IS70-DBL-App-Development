package com.example.weclean.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weclean.R
import com.example.weclean.ui.events.EventData
import com.example.weclean.ui.events.EventDetails
import java.util.Date

class HomeEvents : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO: when the database will be ready implement a proper data retrieve
        val eventList = ArrayList<EventData>()

        eventList.add(EventData("test","test", "test", Date(), 100, 1))
        eventList.add(EventData("test","test", "test", Date(), 100, 1))
        eventList.add(EventData("test","test", "test", Date(), 100, 1))

        val eventAdapter = EventAdapter(object: EventAdapter.EventViewHolder.Listener {
            override fun onEventClicked(event: EventData) {
                (activity as AppCompatActivity).openEventDetails(true, event)
            }
        }, eventList)
        val eventListView = view.findViewById<RecyclerView>(R.id.eventsListView)

        val mLayoutManager = LinearLayoutManager(activity as AppCompatActivity)
        mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        eventListView.layoutManager = mLayoutManager
        eventListView.itemAnimator = DefaultItemAnimator()
        eventListView.adapter = eventAdapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_events, container, false)
    }
}