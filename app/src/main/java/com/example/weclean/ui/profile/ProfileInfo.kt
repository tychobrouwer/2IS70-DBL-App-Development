package com.example.weclean.ui.profile

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
            context.switchFragment(ProfileViewStatus.PROFILE_EDIT, ProfileViewStatus.PROFILE)
        }

        runBlocking { setProfileInfo(view) }
    }

    private suspend fun setProfileInfo(view: View) {
        val userId = fireBase.currentUserId()

        view.findViewById<TextView>(R.id.email).text = fireBase.currentUserEmail()

        val userData = fireBase.getDocument("Users", userId)

        if (userData == null) {
            Toast.makeText(context, "Error getting user information", Toast.LENGTH_SHORT).show()
            return
        }

        val statLitteringEntries = (userData.get("litteringEntries") as ArrayList<*>?)?.size ?: 0

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