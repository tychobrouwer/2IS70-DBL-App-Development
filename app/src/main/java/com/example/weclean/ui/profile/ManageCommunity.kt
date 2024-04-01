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
    private val fireBase = FireBase()

    private lateinit var membersListAdapter: MemberAdapter

    private var members = ArrayList<MemberListData>()
    private var code = ""
    private lateinit var community: CommunityListData

    fun newInstance(community: CommunityListData): ManageCommunity {
        // Create a new fragment with supplied event data
        val fragment = ManageCommunity()
        fragment.community = community

        return fragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val communityName = view.findViewById<TextView>(R.id.community_name)
        communityName.text = community.name

        val nameEditButton = view.findViewById<Button>(R.id.name_edit_button)
        nameEditButton.setOnClickListener {
            changeNameDialog(view)
        }

        val communityCopyCode = view.findViewById<Button>(R.id.code_copy_button)
        communityCopyCode.setOnClickListener {
            val clipboard = (activity as AppCompatActivity).getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Community Code", code)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(context, "Community code copied to clipboard", Toast.LENGTH_SHORT).show()
        }

        // Set up the recycler view
        membersListAdapter = MemberAdapter(members, this)
        val memberListView = view.findViewById<RecyclerView>(R.id.member_list)

        val mLayoutManager = LinearLayoutManager(activity as AppCompatActivity)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        memberListView.layoutManager = mLayoutManager
        memberListView.itemAnimator = DefaultItemAnimator()
        memberListView.adapter = membersListAdapter

        setMembers(view)
    }

    private fun setMembers(view: View) {
        runBlocking {
            launch {
                val userId = fireBase.currentUserId()

                if (userId.isNullOrEmpty()) {
                    Toast.makeText(activity as AppCompatActivity, "Unable to get user", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val communityResult = fireBase.getDocument("Community", community.id)
                if (communityResult == null) {
                    Toast.makeText(activity as AppCompatActivity, "Unable to get community", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val communityUserIds = communityResult.get("userIds") as List<*>
                val adminUserIds = communityResult.get("adminIds") as List<*>

                code = communityResult.get("code").toString()
                view.findViewById<TextView>(R.id.community_code_text).text = code

                for (memberId in communityUserIds) {
                    val userResult = fireBase.getDocument("Users", memberId as String) ?: continue
                    val username = userResult.getString("username") ?: continue

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
            if (communityName.isNotEmpty()) {
                runBlocking {
                    val result = fireBase.updateValue("Community", community.id, "name", communityName)

                    if (result) {
                        view.findViewById<TextView>(R.id.community_name).text = communityName

                        changeDialog.dismiss()
                    } else {
                        Toast.makeText(context, "Failed to change name", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "Please enter a new community name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMemberClicked(adapterPosition: Int) {
        val clickedUser = members[adapterPosition]

        Toast.makeText(requireContext(), "Member ${clickedUser.name} removed from community", Toast.LENGTH_SHORT).show()

        runBlocking {
            val removeResult = fireBase.removeFromArray("Community", community.id, "userIds", clickedUser.id)

            if (!removeResult) {
                Toast.makeText(context, "Failed to remove member", Toast.LENGTH_SHORT).show()
            }

            val updateUserResult = fireBase.removeFromArray("Users", clickedUser.id, "communityIds", community.id)

            if (!updateUserResult) {
                Toast.makeText(context, "Failed to update user", Toast.LENGTH_SHORT).show()
            }

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