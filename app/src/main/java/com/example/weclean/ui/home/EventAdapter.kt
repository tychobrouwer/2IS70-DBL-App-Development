package com.example.weclean.ui.home

import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.weclean.R
import com.example.weclean.backend.EventData
import com.example.weclean.backend.FireBase
import com.example.weclean.ui.events.EventsActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private val fireBase = FireBase()

class EventAdapter (
    private var events: List<EventData>
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


        // Set the fields on the view
        holder.textView.text = event.name

        runBlocking {
            launch {
                // Load image from Firebase Storage

                // Get image bytes from Firebase Storage
                val imageBytes = fireBase.getFileBytes(event.imageId, 1024 * 1024)

                if (imageBytes == null) {
                    Toast.makeText(holder.textView.context, "Failed to load image rc", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Set image view with the image bytes
                val bmp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                holder.imageView.setImageBitmap(bmp)

                // Get the user's event IDs from the database
                //val userEvents = fireBase.getDocument("Users", userId)?.get("eventIds") as List<*>
            }
        }

        holder.itemView.setOnClickListener {
            holder.itemView.context.startActivity(
                Intent(holder.itemView.context, EventsActivity::class.java).putExtra("event", event)
            )
        }
    }

    override fun getItemCount(): Int {
        return events.size
    }
}