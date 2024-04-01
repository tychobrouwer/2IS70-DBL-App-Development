package com.example.weclean.ui.managecommunity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.weclean.R

class CommunityInfo : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_community_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO displaying current community name, current community code and adding edit button
        //val editButton = view.findViewById<Button>(R.id.edit_button)
        //  editButton.setOnClickListener {
        // Handle edit button click
        //}
    }
}