package com.example.weclean.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.weclean.R
import com.example.weclean.backend.FireBase
import com.example.weclean.ui.login.LoginActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.runBlocking
import java.util.Timer
import java.util.TimerTask

class ProfileInfo : Fragment() {
    private val fireBase = FireBase()
    private val dbAuth = Firebase.auth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Edit profile button
        val editButton = view.findViewById<Button>(R.id.edit_button)
        editButton.setOnClickListener {
            // Switch to edit profile view fragment
            val context = activity as AppCompatActivity
            context.switchFragment(ProfileViewStatus.PROFILE_EDIT)
        }

        // Set the user's profile information
        runBlocking { setProfileInfo(view) }
    }

    /**
     * Set the user's profile information in the view
     *
     * @param view
     */
    private suspend fun setProfileInfo(view: View) {
        // Get the current user's ID
        val userId = fireBase.currentUserId()

        // If no user is logged in or user is empty
        if (userId.isNullOrEmpty()) {
            Toast.makeText(activity as AppCompatActivity, "Unable to get user", Toast.LENGTH_SHORT).show()
            startActivity(Intent(activity as AppCompatActivity, LoginActivity::class.java))

            return
        }

        // Logout button
        val logout = view.findViewById<Button>(R.id.logout_button)
        logout.setOnClickListener {
            logoutDialog()
        }

        // View communities button
        val viewCommunitiesButton = view.findViewById<Button>(R.id.view_communities)
        viewCommunitiesButton.setOnClickListener {
            // Switch to communities list view fragment
            val context = activity as AppCompatActivity
            context.switchFragment(ProfileViewStatus.COMMUNITIES_LIST)
        }

        // Set the email field to the current user's email
        view.findViewById<TextView>(R.id.email).text = fireBase.currentUserEmail()

        // Get the user's data from the database
        val userData = fireBase.getDocument("Users", userId)

        if (userData == null) {
            Toast.makeText(context, "Error getting user information", Toast.LENGTH_SHORT).show()
            return
        }

        // Get the number of littering entries the user has made
        val statLitteringEntries = (userData.get("litteringEntries") as ArrayList<*>?)?.size ?: 0

        // Get the number of littering entries the user has made
        val statEventsJoined = (userData.get("eventIds") as ArrayList<*>?)?.size ?: 0

        // String of user's communities to display
        var userCommunitiesListString = ""

        // Get user's communities
        val userCommunities = userData.get("communityIds") as? ArrayList<*> ?: emptyList()

        for (community in userCommunities) {
            // Get community data from database
            val communityResult =
                fireBase.getDocument("Community", community as String) ?: continue
            if (communityResult.data == null) continue

            // Add community to list of user's communities to display
            userCommunitiesListString += communityResult.getString("name") + "\n"
        }

        // Set the user's data in the view
        view.findViewById<TextView>(R.id.username).text = userData.getString("username")
        view.findViewById<TextView>(R.id.littering_entries).text = statLitteringEntries.toString()
        view.findViewById<TextView>(R.id.events_joined).text = statEventsJoined.toString()
        view.findViewById<TextView>(R.id.communities_list).text = userCommunitiesListString
    }

    /**
     * Show a dialog to confirm logging out
     */
    private fun logoutDialog() {
        // Builder for alert dialog popup
        val builder = AlertDialog.Builder(activity as AppCompatActivity)
        builder.setCancelable(true)

        // Create dialog from builder
        val deleteDialog = builder.create()

        // Inflate dialog from R.layout.profile_join_community
        val dialogLayout = layoutInflater.inflate(R.layout.dialog_logout, null)

        // Show alert dialog
        deleteDialog.setView(dialogLayout)
        deleteDialog.show()

        // Join/confirm button
        val logoutButton = deleteDialog.findViewById<Button>(R.id.logout_button)
        val cancelButton = deleteDialog.findViewById<Button>(R.id.cancel_button)

        if (logoutButton == null || cancelButton == null) {
            return
        }

        // Listener for cancel button
        cancelButton.setOnClickListener {
            deleteDialog.dismiss()
        }

        // Listener for join button
        logoutButton.setOnClickListener {
            dbAuth.signOut()
            startActivity(Intent(activity as AppCompatActivity, LoginActivity::class.java))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_info, container, false)
    }
}