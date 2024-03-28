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
import androidx.appcompat.app.AppCompatActivity
import com.example.weclean.R
import com.example.weclean.backend.FireBase
import com.example.weclean.ui.login.LoginActivity
import kotlinx.coroutines.runBlocking

class ProfileInfo : Fragment() {
    private val fireBase = FireBase()

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

        // Set the user's data in the view
        view.findViewById<TextView>(R.id.username).text = userData.getString("username")
        view.findViewById<TextView>(R.id.region).text = userData.getString("country")
        view.findViewById<TextView>(R.id.littering_entries).text = statLitteringEntries.toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_info, container, false)
    }
}