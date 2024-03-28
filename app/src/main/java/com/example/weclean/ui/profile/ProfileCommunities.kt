package com.example.weclean.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.weclean.R
import com.example.weclean.backend.Community
import com.example.weclean.backend.FireBase
import com.example.weclean.ui.login.LoginActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ProfileCommunities : Fragment() {
    private val communityObject = Community()
    private val fireBase = FireBase()

    private val communities = ArrayList<CommunityListData>()

    private lateinit var joinDialog: AlertDialog

    private fun joinCommunityDialog() {
        // Builder for alert dialog popup
        val builder = AlertDialog.Builder(activity as AppCompatActivity)
        builder.setCancelable(true)

        // Create dialog from builder
        joinDialog = builder.create()

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
            val communityCode = editText!!.text.toString()

            // If empty, close dialog else add user to community
            if (communityCode.isNotEmpty()) {
                runBlocking {
                    val result = communityObject.addUserWithCode(communityCode)

                    if (result) {
                        joinDialog.dismiss()
                    } else {
                        Toast.makeText(context, "Community code no valid", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "Please enter a community code", Toast.LENGTH_SHORT).show()
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
            val context = activity as AppCompatActivity
            context.switchFragment(ProfileViewStatus.COMMUNITY_CREATE)
        }

        setCommunities()

        // Create list adapter for the communities list
        val communitiesListAdapter = CommunityListAdapter(
            activity as AppCompatActivity,
            communities
        )

        // Get the community list view, and set the adapter
        val communitiesListView  = view.findViewById<ListView>(R.id.community_list)
        communitiesListView.adapter = communitiesListAdapter
    }

    private fun setCommunities() {
        runBlocking {
            launch {
                val userId = fireBase.currentUserId()

                // If no user is logged in or user is empty
                if (userId.isNullOrEmpty()) {
                    Toast.makeText(activity as AppCompatActivity, "Unable to get user", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(activity as AppCompatActivity, LoginActivity::class.java))

                    return@launch
                }

                val communitiesResult = fireBase.getDocument("Users", userId)

                if (communitiesResult == null) {
                    Toast.makeText(activity as AppCompatActivity, "Error getting user", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val userCommunities = communitiesResult.get("communityIds") as? ArrayList<*> ?: emptyList()
                for (community in userCommunities) {
                    val communityResult =
                        fireBase.getDocument("Community", community as String) ?: return@launch

                    communities.add(CommunityListData(
                        communityResult.get("name") as String,
                        (communityResult.get("adminIds") as ArrayList<*>).contains(fireBase.currentUserId())
                    ))
                }
            }
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