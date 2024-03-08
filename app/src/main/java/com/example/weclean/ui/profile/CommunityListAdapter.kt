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
import java.util.ArrayList;

class CommunityListAdapter(context: Context, dataArrayList: ArrayList<CommunityListData>) : ArrayAdapter<CommunityListData>(context,
    R.layout.community_list_item, dataArrayList) {
    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var view = view
        var isNewView = false

        val listData : CommunityListData = getItem(position)!!

        if (view == null) {
            isNewView = true
            view = LayoutInflater.from(context).inflate(R.layout.community_list_item, parent, false)
        }

        val listTextView = view!!.findViewById<TextView>(R.id.text)
        listTextView.text = listData.name

        val listManageButton = view.findViewById<Button>(R.id.community_manage_button)
        val listLeaveButton = view.findViewById<Button>(R.id.community_leave_button)
        val listViewParent = view.findViewById<LinearLayout>(R.id.community_list_layout)

        if (!isNewView) return view

        if (listData.userIsAdmin) {
            listViewParent.removeView(listManageButton)
        } else {
            listViewParent.removeView(listLeaveButton)
        }

        return view
    }
}