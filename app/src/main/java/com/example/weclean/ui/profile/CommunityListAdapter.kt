package com.example.weclean.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weclean.R

class CommunityAdapter(
    private var dataArrayList: ArrayList<CommunityListData>,
    private val listener: RecyclerViewCommunity,
) : RecyclerView.Adapter<CommunityAdapter.CommunityViewHolder>() {
    class CommunityViewHolder(view: View, private val listener: RecyclerViewCommunity) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private val manageButton: TextView = view.findViewById(R.id.community_manage_button)
        private val leaveButton: TextView = view.findViewById(R.id.community_leave_button)

        init {
            // Set the click listener for the buttons
            manageButton.setOnClickListener(this)
            leaveButton.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            // Get the adapter position
            val adapterPosition = adapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION) {
                // Call the listener with the adapter position
                listener.onCommunityClicked(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_community, parent, false)

        // Return the view holder with the listener
        return CommunityViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: CommunityViewHolder, position: Int) {
        // Get the community at the current position
        val community = dataArrayList[position]

        // Set the text for the community
        holder.itemView.findViewById<TextView>(R.id.name).text = community.name

        // Get buttons for delete and manage community
        val listManageButton = holder.itemView.findViewById<Button>(R.id.community_manage_button)
        val listLeaveButton = holder.itemView.findViewById<Button>(R.id.community_leave_button)

        // Hide either delete or manage button based on if user is an admin of community
        if (community.userIsAdmin) {
            listLeaveButton.visibility = View.GONE
        } else {
            listManageButton.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return dataArrayList.size
    }

    interface RecyclerViewCommunity {
        fun onCommunityClicked(adapterPosition: Int)
    }
}