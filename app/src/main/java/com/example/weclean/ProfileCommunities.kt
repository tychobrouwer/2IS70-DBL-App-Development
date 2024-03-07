package com.example.weclean

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

class ProfileCommunities : Fragment() {
    private fun joinCommunityDialog() {
        val builder = AlertDialog.Builder(activity as AppCompatActivity)
        builder.setCancelable(true)
        val joinDialog = builder.create()

        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.profile_join_community, null)

        joinDialog.setView(dialogLayout)
        joinDialog.show()

        val editText = joinDialog.findViewById<EditText>(R.id.community_code_edittext)
        val joinButton = joinDialog.findViewById<Button>(R.id.community_join_button)

        joinButton!!.setOnClickListener {
            val communityCode = editText!!.text

            if (communityCode.isNotEmpty()) {
                println("Join community with code $communityCode")
                joinDialog.dismiss()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val joinCommunityButton = view.findViewById<Button>(R.id.join_community_button)
        joinCommunityButton.setOnClickListener {
            println("show join community dialog")
            joinCommunityDialog()
        }

        val createCommunityButton = view.findViewById<Button>(R.id.create_community_button)
        createCommunityButton.setOnClickListener {
            println("create community")
        }

        val communities = ArrayList<CommunityListData>()
        communities.add(CommunityListData("Community 1", true))
        communities.add(CommunityListData("Community 2", false))
        communities.add(CommunityListData("Community 3", false))

        val communitiesListAdapter = CommunityListAdapter(
            activity as AppCompatActivity,
            communities
        )

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