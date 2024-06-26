package com.example.weclean.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weclean.R
import com.example.weclean.backend.Community
import com.example.weclean.backend.FireBase
import com.example.weclean.ui.login.LoginActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ProfileCommunities : Fragment(), CommunityAdapter.RecyclerViewCommunity {
    private var fireBase = FireBase()
    private val communityObject = Community(fireBase)

    private var communities = ArrayList<CommunityListData>()

    private lateinit var communitiesListAdapter: CommunityAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Join community button
        val joinCommunityButton = view.findViewById<Button>(R.id.join_community_button)
        // Listener for opening the join community alert dialog
        joinCommunityButton.setOnClickListener {
            joinCommunityDialog()
        }

        // Create community button
        val createCommunityButton = view.findViewById<Button>(R.id.create_community_button)
        // Listener for opening the create community fragment
        createCommunityButton.setOnClickListener {
            val context = activity as AppCompatActivity
            context.switchFragment(ProfileViewStatus.COMMUNITY_CREATE)
        }

        // Set up the recycler view
        communitiesListAdapter = CommunityAdapter(communities, this)
        val eventListView = view.findViewById<RecyclerView>(R.id.community_list)

        // Set up the layout manager and adapter for the recycler view
        val mLayoutManager = LinearLayoutManager(activity as AppCompatActivity)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        eventListView.layoutManager = mLayoutManager
        eventListView.itemAnimator = DefaultItemAnimator()
        eventListView.adapter = communitiesListAdapter

        // Get the user's communities
        setCommunities()
    }

    private fun joinCommunityDialog() {
        // Builder for alert dialog popup
        val builder = AlertDialog.Builder(activity as AppCompatActivity)
        builder.setCancelable(true)

        // Create dialog from builder
        val joinDialog = builder.create()

        // Inflate dialog from R.layout.profile_join_community
        val dialogLayout = layoutInflater.inflate(R.layout.dialog_join_community, null)

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
            if (communityCode.isEmpty()) {
                Toast.makeText(context, "Please enter a community code", Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }

            runBlocking {
                val result = communityObject.addUserWithCode(communityCode)

                if (result) {
                    joinDialog.dismiss()
                    setCommunities()
                } else {
                    Toast.makeText(context, "Community code not valid", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setCommunities() {
        runBlocking {
            launch {
                // Get the current user's ID
                val userId = fireBase.currentUserId()

                // If no user is logged in or user is empty
                if (userId.isNullOrEmpty()) {
                    Toast.makeText(activity as AppCompatActivity, "Unable to get user", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(activity as AppCompatActivity, LoginActivity::class.java))

                    return@launch
                }

                // Get user's communities
                val communitiesResult = fireBase.getDocument("Users", userId)
                if (communitiesResult == null) {
                    Toast.makeText(activity as AppCompatActivity, "Error getting user", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Get community IDs from user
                val userCommunities = communitiesResult.get("communityIds") as? ArrayList<*> ?: emptyList()

                for (communityId in userCommunities) {
                    // Get community data from database
                    val communityResult =
                        fireBase.getDocument("Community", communityId as String) ?: return@launch

                    // Skip if community data is null
                    if (communityResult.data == null) continue

                    // Add community to list
                    val communityListData = CommunityListData(
                        communityResult.get("name") as String,
                        communityId,
                        (communityResult.get("adminIds") as ArrayList<*>).contains(fireBase.currentUserId())
                    )

                    // Update the list adapter with the new community
                    if (!communities.map { it.id }.contains(communityId)) {
                        communities.add(communityListData)
                        communitiesListAdapter.notifyItemInserted(communities.size - 1)
                    }
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

    override fun onCommunityClicked(adapterPosition: Int) {
        // Get the community at the current position
        val community = communities[adapterPosition]

        // If user is admin, switch to manage community fragment else leave community
        if (community.userIsAdmin) {
            val context = activity as AppCompatActivity
            context.switchFragment(ProfileViewStatus.COMMUNITY_MANAGE, community)
        } else {
            // Leave community
            val currentUser = fireBase.currentUserId()
            if (currentUser.isNullOrEmpty()) {
                Toast.makeText(context, "Unable to get user", Toast.LENGTH_SHORT).show()
                return
            }

            runBlocking {
                // Remove user from community
                val updateCommunityResult = fireBase.removeFromArray(
                    "Community",
                    community.id,
                    "userIds",
                    currentUser)

                if (!updateCommunityResult) {
                    Toast.makeText(context, "Failed to leave community", Toast.LENGTH_SHORT).show()

                    return@runBlocking
                }

                // Remove community from user
                val updateUserResult = fireBase.removeFromArray(
                    "Users",
                    currentUser,
                    "communityIds",
                    community.id)

                if (!updateUserResult) {
                    Toast.makeText(context, "Failed to update user", Toast.LENGTH_SHORT).show()

                    return@runBlocking
                }

                // Remove the community from the list and update the view
                communities.removeAt(adapterPosition)
                communitiesListAdapter.notifyItemRemoved(adapterPosition)

                Toast.makeText(context, "Left ${community.name} community", Toast.LENGTH_SHORT).show()
            }
        }
    }
}