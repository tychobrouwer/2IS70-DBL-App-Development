package com.example.weclean

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class ProfileCommunities : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val joinCommunityButton = view.findViewById<Button>(R.id.join_community_button)
        joinCommunityButton.setOnClickListener {
            println("join community")
        }

        val createCommunityButton = view.findViewById<Button>(R.id.create_community_button)
        createCommunityButton.setOnClickListener {
            println("create community")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_communities, container, false)
    }
}