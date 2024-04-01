package com.example.weclean.ui.managecommunity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weclean.R
import com.example.weclean.ui.managecommunity.MemberAdapter
import com.example.weclean.ui.managecommunity.MemberListData

class MemberAdapter(
    private var dataArrayList: ArrayList<MemberListData>,
    private val listener: RecyclerViewMember,
) : RecyclerView.Adapter<MemberAdapter.MemberViewHolder>() {
    class MemberViewHolder(view: View, private val listener: RecyclerViewMember) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private val removeButton: TextView = view.findViewById(R.id.remove_member_button)

        init {
            removeButton.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            val adapterPosition = adapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onMemberClicked(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.member_list_item, parent, false)

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

    }

    override fun getItemCount(): Int {
        return dataArrayList.size
    }

    interface RecyclerViewMember {
        fun onMemberClicked(adapterPosition: Int)
    }
}