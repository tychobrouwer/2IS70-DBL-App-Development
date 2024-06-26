package com.example.weclean.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weclean.R

class MemberAdapter(
    private var dataArrayList: ArrayList<MemberListData>,
    private val listener: RecyclerViewMember,
) : RecyclerView.Adapter<MemberAdapter.MemberViewHolder>() {
    class MemberViewHolder(view: View, private val listener: RecyclerViewMember) : RecyclerView.ViewHolder(view), View.OnClickListener {
        // Get the remove button
        private val removeButton: TextView = view.findViewById(R.id.remove_member_button)

        init {
            // Set the click listener for the buttons
            removeButton.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            // Get the adapter position
            val adapterPosition = adapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION) {
                // Call the listener with the adapter position
                listener.onMemberClicked(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_member, parent, false)

        // Return the view holder with the listener
        return MemberViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        // Get the community at the current position
        val member = dataArrayList[position]

        // Set the text for the community
        holder.itemView.findViewById<TextView>(R.id.member_name).text = member.name

        // Get button for removing members
        val listRemoveButton = holder.itemView.findViewById<Button>(R.id.remove_member_button)
        val listAdminLabel = holder.itemView.findViewById<TextView>(R.id.admin_label)

        // Hide remove button if user is an admin
        if (member.isAdmin) {
            listRemoveButton.visibility = View.GONE
            listAdminLabel.visibility = View.VISIBLE
        }  else {
            listRemoveButton.visibility = View.VISIBLE
            listAdminLabel.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return dataArrayList.size
    }

    interface RecyclerViewMember {
        fun onMemberClicked(adapterPosition: Int)
    }
}