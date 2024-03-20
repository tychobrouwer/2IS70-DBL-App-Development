package com.example.weclean.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weclean.R
import com.example.weclean.ui.events.EventData
import java.util.ArrayList

class EventAdapter(
    private val listener: EventViewHolder.Listener,
    private var dataArrayList: ArrayList<EventData>
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {
    class EventViewHolder(view: View, private val listener: Listener) : RecyclerView.ViewHolder(view) {
        interface Listener {
            fun onEventClicked(event: EventData)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return EventViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = dataArrayList[position]

        holder.itemView.setOnClickListener {
            listener.onEventClicked(event)
        }
    }

    override fun getItemCount(): Int {
        return dataArrayList.size
    }
}