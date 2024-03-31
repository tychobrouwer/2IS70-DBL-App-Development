package com.example.weclean.ui.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.example.weclean.R
import java.util.ArrayList

class CommunityListAdapter(context: Context, dataArrayList: ArrayList<CommunityListData>) : ArrayAdapter<CommunityListData>(context,
    R.layout.community_list_item, dataArrayList) {
    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var view = view

        val listData : CommunityListData = getItem(position)!!

        // Boolean to check if a new view is created from R.layout.community_list_item
        var isNewView = false
        if (view == null) {
            // Inflate new instance from R.layout.community_list_item
            view = LayoutInflater.from(context).inflate(R.layout.community_list_item, parent, false)
            isNewView = true
        }

        // Set community name by setting text value of R.id.text
        val listTextView = view!!.findViewById<TextView>(R.id.text)
        listTextView.text = listData.name

        // Get buttons for delete and manage community
        val listManageButton = view.findViewById<Button>(R.id.community_manage_button)
        val listLeaveButton = view.findViewById<Button>(R.id.community_leave_button)

        // Parent view of R.id.community_list_layout
        val listViewParent = view.findViewById<LinearLayout>(R.id.community_list_layout)

        // Return if not new view instance
        if (!isNewView) return view

        // Hide either delete or manage button based on if user is an admin of community
        if (listData.userIsAdmin) {
            listViewParent.removeView(listLeaveButton)
        } else {
            listViewParent.removeView(listManageButton)
        }

        return view
    }

    fun updateData(dataArrayList: ArrayList<CommunityListData>) {
        clear()
        addAll(dataArrayList)
        notifyDataSetChanged()
    }
}