package com.example.weclean.ui.profile

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.weclean.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import com.example.weclean.backend.Community
import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.runBlocking

class CreateCommunity : Fragment() {
    private val communityObject = Community()

    /**
     * Create a community and add the user who created the community
     * to this community
     *
     * @param view
     * @param savedInstanceState
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Cancel create community button
        val cancelButton = view.findViewById<Button>(R.id.cCancel_button)
        cancelButton.setOnClickListener {
            // Switch back to default profile fragment
            val context = activity as AppCompatActivity
            context.switchFragment(ProfileViewStatus.PROFILE, ProfileViewStatus.COMMUNITY_CREATE)
        }

        // Confirm create community button
        val confirmButton = view.findViewById<Button>(R.id.cConfirm_button)
        confirmButton.setOnClickListener {

            //get all text input fields
            val communityNameEditText = view.findViewById<EditText>(R.id.community_name)
            val communityName = communityNameEditText.text.toString()

            val communityEmailEditText = view.findViewById<EditText>(R.id.community_email)
            val communityEmail = communityEmailEditText.text.toString().trim()

            val communityConfirmEmailEditText = view.findViewById<EditText>(R.id.community_confirm_email)
            val userConfirmEmail = communityConfirmEmailEditText.text.toString().trim()

            val communityLocationEditText = view.findViewById<EditText>(R.id.community_location)
            val communityLocation = communityLocationEditText.text.toString()

            //ensure fields are not empty
            if (communityName.isEmpty() || communityEmail.isEmpty() ||
                communityLocation.isEmpty() || userConfirmEmail.isEmpty()) {
                Toast.makeText(context, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            } else if (communityEmail != userConfirmEmail) {
                Toast.makeText(context, "Emails do not match", Toast.LENGTH_SHORT).show()
            }

            //TODO: GENERATE A UNIQUE COMMUNITY CODE
            val cCode = 1

            runBlocking {
                val result = communityObject.addCommunityToDatabase(
                    communityName,
                    communityEmail,
                    communityLocation,
                    cCode)

                if (result) {
                    Toast.makeText(context, "Community created successfully!", Toast.LENGTH_SHORT).show()

                    // Switch back to default profile fragment
                    // TODO: Switch to community profile fragment
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.create_community, container, false)
    }

}