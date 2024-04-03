package com.example.weclean.ui.events

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weclean.R
import com.example.weclean.backend.EventData
import com.example.weclean.backend.dayStringFormat

class EventAdapter(
    private var dataArrayList: ArrayList<EventData>,
    private val listener: RecyclerViewEvent,
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {
    class EventViewHolder(view: View, private val listener: RecyclerViewEvent) : RecyclerView.ViewHolder(view), View.OnClickListener {
        // Initialize the text views
        val name: TextView = view.findViewById(R.id.name)
        val location: TextView = view.findViewById(R.id.location)
        val date: TextView = view.findViewById(R.id.date)

        init {
            // Set the click listener for the view holder
            name.setOnClickListener(this)
            location.setOnClickListener(this)
            date.setOnClickListener(this)
            view.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            // Get the adapter position
            val adapterPosition = adapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION) {
                // Call the listener
                listener.onEventClicked(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_event, parent, false)

        // Return the view holder with the listener
        return EventViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        // Get the event at the current position
        val event = dataArrayList[position]

        // Set the text for the event
        holder.itemView.findViewById<TextView>(R.id.name).text = event.name
        holder.itemView.findViewById<TextView>(R.id.location).text = event.location
        holder.itemView.findViewById<TextView>(R.id.date).text = dayStringFormat(event.timeStamp)
    }

    override fun getItemCount(): Int {
        return dataArrayList.size
    }

    interface RecyclerViewEvent {
        fun onEventClicked(adapterPosition: Int)
    }
}