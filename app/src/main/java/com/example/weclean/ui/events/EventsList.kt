package com.example.weclean.ui.events

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weclean.R
import com.example.weclean.backend.EventData
import com.example.weclean.backend.FireBase
import com.example.weclean.ui.login.LoginActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class EventsList : Fragment(), EventAdapter.RecyclerViewEvent {
    // FireBase class instance to communicate with the database
    private val fireBase = FireBase()

    // List of events shown
    private var events = ArrayList<EventData>()
    // Event adapter for the recycler view
    private lateinit var eventAdapter: EventAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set OnClickListener for the addEvent button
        val addEventButton = view.findViewById<Button>(R.id.addEvent)
        addEventButton.setOnClickListener {
            startActivity(Intent(activity as AppCompatActivity, AddEvent::class.java))
        }

        // Current logged in user
        val userId = fireBase.currentUserId()

        // If no user is logged in or user is empty
        if (userId.isNullOrEmpty()) {
            Toast.makeText(activity as AppCompatActivity, "Unable to get user", Toast.LENGTH_SHORT).show()
            startActivity(Intent(activity as AppCompatActivity, LoginActivity::class.java))

            return
        }

        runBlocking {
            // Get user data
            val userData = fireBase.getDocument("Users", userId) ?: return@runBlocking
            if (userData.data == null) return@runBlocking

            // Get the communities the user is an admin of
            val adminCommunities = userData.get("communityAdminIds") as? ArrayList<*> ?: emptyList()

            // If the user is not an admin of any communities, hide the add event button
            if (adminCommunities.isEmpty()) {
                addEventButton.visibility = View.GONE
            }
        }

        // Set up the recycler view
        eventAdapter = EventAdapter(events, this)
        val eventListView = view.findViewById<RecyclerView>(R.id.eventsListView)

        // Set the layout manager and adapter for the recycler view
        val mLayoutManager = LinearLayoutManager(activity as AppCompatActivity)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        eventListView.layoutManager = mLayoutManager
        eventListView.itemAnimator = DefaultItemAnimator()
        eventListView.adapter = eventAdapter

        // Get the events to display
        getEvents()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events_list, container, false)
    }

    private fun getEvents() {
        runBlocking {
            launch {
                // Current logged in user
                val userId = fireBase.currentUserId()

                // If no user is logged in or user is empty
                if (userId.isNullOrEmpty()) {
                    Toast.makeText(activity as AppCompatActivity, "Unable to get user", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(activity as AppCompatActivity, LoginActivity::class.java))

                    return@launch
                }

                // Get user data
                val userData = fireBase.getDocument("Users", userId) ?: return@launch
                if (userData.data == null) return@launch

                // Get the communities the user is a part of
                val communities = userData.get("communityIds") as ArrayList<*>? ?: return@launch

                // Get the events for each community
                for (community in communities) {
                    // Get the community data
                    val communityData = fireBase.getDocument("Community", community as String) ?: continue

                    // If the community data is null, continue
                    if (communityData.data == null) continue

                    // Get the event IDs for the community
                    val eventsToAdd = communityData.get("eventIds") as ArrayList<*>? ?: continue

                    // Add the events to the list
                    for (event in eventsToAdd) {
                        // Get the event data
                        val eventDataResult = fireBase.getDocument("Events", event as String) ?: continue

                        if (eventDataResult.data == null) continue

                        // Create the event data object
                        val eventData = EventData()
                        eventData.id = eventDataResult.id
                        eventData.name = eventDataResult.getString("name")!!
                        eventData.timeStamp = eventDataResult.getDate("date")!!.time
                        eventData.imageId = eventDataResult.getString("imageId")!!
                        eventData.description = eventDataResult.getString("description")!!
                            .replace("_newline", "\n")
                        eventData.location = eventDataResult.getString("location")!!
                        eventData.community = eventDataResult.getString("community")!!
                        eventData.communityName = communityData.getString("name")!!
                        eventData.numPeople = (eventDataResult.get("userIds") as List<*>?)!!.size

                        // Add the event data to the list
                        events.add(eventData)

                        // Notify adapter of data change
                        eventAdapter.notifyItemInserted(events.size - 1)
                    }
                }
            }
        }
    }

    override fun onEventClicked(adapterPosition: Int) {
        // Get the event data
        val event = events[adapterPosition]

        // Open the event details fragment
        (activity as AppCompatActivity).openEventDetails(true, event)
    }
}