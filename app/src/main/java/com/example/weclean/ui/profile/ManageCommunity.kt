package com.example.weclean.ui.profile

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weclean.R
import com.example.weclean.backend.FireBase
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ManageCommunity : Fragment(), MemberAdapter.RecyclerViewMember {
    // FireBase class instance to communicate with the database
    private val fireBase = FireBase()

    // Adapter for the members list
    private lateinit var membersListAdapter: MemberAdapter

    // List of members in the community
    private var members = ArrayList<MemberListData>()
    // Community code
    private var code = ""
    // Community data
    private lateinit var community: CommunityListData

    /**
     * Create a new instance of the ManageCommunity fragment with the community data
     *
     * @param community
     * @return ManageCommunity fragment
     */
    fun newInstance(community: CommunityListData): ManageCommunity {
        // Create a new fragment with supplied event data
        val fragment = ManageCommunity()
        fragment.community = community

        return fragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the community name
        val communityName = view.findViewById<TextView>(R.id.community_name)
        communityName.text = community.name

        // Set the community code
        val nameEditButton = view.findViewById<Button>(R.id.name_edit_button)
        nameEditButton.setOnClickListener {
            // Show dialog to change community name
            changeNameDialog(view)
        }

        // Copy the community code to the clipboard button
        val communityCopyCode = view.findViewById<Button>(R.id.code_copy_button)
        communityCopyCode.setOnClickListener {
            // Copy the community code to the clipboard
            val clipboard = (activity as AppCompatActivity).getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager

            // Create a clip data object with the community code
            val clip = ClipData.newPlainText("Community Code", code)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(context, "Community code copied to clipboard", Toast.LENGTH_SHORT).show()
        }

        // Set up the recycler view
        membersListAdapter = MemberAdapter(members, this)
        val memberListView = view.findViewById<RecyclerView>(R.id.member_list)

        // Set the layout manager and adapter for the recycler view
        val mLayoutManager = LinearLayoutManager(activity as AppCompatActivity)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        memberListView.layoutManager = mLayoutManager
        memberListView.itemAnimator = DefaultItemAnimator()
        memberListView.adapter = membersListAdapter

        // Get the members of the community
        setMembers(view)
    }

    /**
     * Get the members of the community
     *
     * @param view
     */
    private fun setMembers(view: View) {
        runBlocking {
            launch {
                // Get the current user ID
                val userId = fireBase.currentUserId()

                // If the user ID is empty, show an error message
                if (userId.isNullOrEmpty()) {
                    Toast.makeText(activity as AppCompatActivity, "Unable to get user", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Get the community data
                val communityResult = fireBase.getDocument("Community", community.id)
                if (communityResult == null) {
                    // Show an error message if the community data is empty
                    Toast.makeText(activity as AppCompatActivity, "Unable to get community", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Get the user IDs and admin IDs for the community
                val communityUserIds = communityResult.get("userIds") as List<*>
                val adminUserIds = communityResult.get("adminIds") as List<*>

                // Get the community invite code
                code = communityResult.get("code").toString()
                view.findViewById<TextView>(R.id.community_code_text).text = code

                // Add the members to the list
                for (memberId in communityUserIds) {
                    // Get the user data for the member
                    val userResult = fireBase.getDocument("Users", memberId as String) ?: continue
                    val username = userResult.getString("username") ?: continue

                    // Update the members list
                    members.add(MemberListData(username, memberId, adminUserIds.contains(memberId)))
                    membersListAdapter.notifyItemInserted(members.size - 1)
                }
            }
        }
    }

    private fun changeNameDialog(view: View) {
        // Builder for alert dialog popup
        val builder = AlertDialog.Builder(activity as AppCompatActivity)
        builder.setCancelable(true)

        // Create dialog from builder
        val changeDialog = builder.create()

        // Inflate dialog from R.layout.profile_join_community
        val dialogLayout = layoutInflater.inflate(R.layout.dialog_change_community_name, null)

        // Show alert dialog
        changeDialog.setView(dialogLayout)
        changeDialog.show()

        // Text input for community code
        val editText = changeDialog.findViewById<EditText>(R.id.community_name_edittext)
        // Join/confirm button
        val changeButton = changeDialog.findViewById<Button>(R.id.community_name_change_button)

        // Listener for join button
        changeButton!!.setOnClickListener {
            // Get community code from text input
            val communityName = editText!!.text.toString()

            // If empty, close dialog else add user to community
            if (communityName.isEmpty()) {
                Toast.makeText(context, "Please enter a new community name", Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }

            runBlocking {
                // Update the community name in the database
                val result = fireBase.updateValue("Community", community.id, "name", communityName)

                if (result) {
                    // Update the community name in the view
                    view.findViewById<TextView>(R.id.community_name).text = communityName

                    // Show success message
                    changeDialog.dismiss()
                } else {
                    Toast.makeText(context, "Failed to change name", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    override fun onMemberClicked(adapterPosition: Int) {
        // Get the member that was clicked
        val clickedUser = members[adapterPosition]

        runBlocking {
            // Remove the user from the community
            val removeResult = fireBase.removeFromArray("Community", community.id, "userIds", clickedUser.id)
            if (!removeResult) {
                Toast.makeText(context, "Failed to remove member", Toast.LENGTH_SHORT).show()

                return@runBlocking
            }

            // Remove the community from the user
            val updateUserResult = fireBase.removeFromArray("Users", clickedUser.id, "communityIds", community.id)
            if (!updateUserResult) {
                Toast.makeText(context, "Failed to update user", Toast.LENGTH_SHORT).show()

                return@runBlocking
            }

            Toast.makeText(requireContext(), "Member ${clickedUser.name} removed from community", Toast.LENGTH_SHORT).show()

            // Remove the member from the list and update the view
            members.removeAt(adapterPosition)
            membersListAdapter.notifyItemRemoved(adapterPosition)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_community, container, false)
    }
}