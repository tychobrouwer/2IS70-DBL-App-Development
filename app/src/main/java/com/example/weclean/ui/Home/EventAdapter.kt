package com.example.weclean.ui.Home

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weclean.R

class EventAdapter (
    var events:List<Event>
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>(){
    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imageView: ImageView = itemView.findViewById(R.id.imageView2)
        val textView: TextView = itemView.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        //TODO: get proper data from
        holder.imageView.setImageResource(R.drawable.ic_launcher_background)
        holder.textView.text = event.desc

        holder.itemView.setOnClickListener{
            holder.itemView.context.startActivity(
                Intent(holder.itemView.context, EventPopup::class.java).putExtra("event",event)

            )

        }
    }

    override fun getItemCount(): Int {
        return events.size
    }
}