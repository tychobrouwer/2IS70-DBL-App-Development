package com.example.weclean.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.weclean.R

class ProfileCommunities : Fragment() {
    private fun joinCommunityDialog() {
        // Builder for alert dialog popup
        val builder = AlertDialog.Builder(activity as AppCompatActivity)
        builder.setCancelable(true)

        // Create dialog from builder
        val joinDialog = builder.create()

        // Inflate dialog from R.layout.profile_join_community
        val dialogLayout = layoutInflater.inflate(R.layout.profile_join_community, null)

        // Show alert dialog
        joinDialog.setView(dialogLayout)
        joinDialog.show()

        // Text input for community code
        val editText = joinDialog.findViewById<EditText>(R.id.community_code_edittext)
        // Join/confirm button
        val joinButton = joinDialog.findViewById<Button>(R.id.community_join_button)

        // Listener for join button
        joinButton!!.setOnClickListener {
            // Get community code from text input
            val communityCode = editText!!.text

            // If not empty, close dialog
            if (communityCode.isNotEmpty()) {
                // TODO: Here the user needs to be added to the community
                println("Join community with code $communityCode")
                joinDialog.dismiss()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Join community button
        val joinCommunityButton = view.findViewById<Button>(R.id.join_community_button)
        // Listener for opening the join community alert dialog
        joinCommunityButton.setOnClickListener {
            println("show join community dialog")
            joinCommunityDialog()
        }

        // Create community button
        val createCommunityButton = view.findViewById<Button>(R.id.create_community_button)
        // Listener for opening the create community fragment
        createCommunityButton.setOnClickListener {
            println("create community")
            // TODO: Here the create community fragement needs to be opened
        }

        // List of community objects
        // TODO: This needs to be updated to use the backend
        val communities = ArrayList<CommunityListData>()
        communities.add(CommunityListData("Community 1", true))
        communities.add(CommunityListData("Community 2", false))
        communities.add(CommunityListData("Community 3", false))

        // Create list adapter for the communities list
        val communitiesListAdapter = CommunityListAdapter(
            activity as AppCompatActivity,
            communities
        )

        // Get the community list view, and set the adapter
        val communitiesListView  = view.findViewById<ListView>(R.id.community_list)
        communitiesListView.adapter = communitiesListAdapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_communities, container, false)
    }
}