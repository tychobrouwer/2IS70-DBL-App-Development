package com.example.weclean.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.weclean.R
import com.example.weclean.backend.Community
import com.example.weclean.backend.FireBase
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

class CreateCommunity : Fragment() {

    private val fireBase = FireBase()
    // Community object
    private val communityObject = Community(fireBase)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Cancel create community button
        val cancelButton = view.findViewById<Button>(R.id.cCancel_button)
        cancelButton.setOnClickListener {
            // Switch back to default profile fragment
            (activity as AppCompatActivity).switchFragment(ProfileViewStatus.PROFILE)
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

                return@setOnClickListener
            } else if (communityEmail != userConfirmEmail) {
                Toast.makeText(context, "Emails do not match", Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }

            // Generate a random community code
            val cCode = Random.nextInt(0, 999999)

            runBlocking {
                // Add community to database
                val result = communityObject.addToDatabase(
                    communityName,
                    communityEmail,
                    communityLocation,
                    cCode)

                if (result) {
                    Toast.makeText(context, "Community created successfully!", Toast.LENGTH_SHORT).show()

                    // Switch back to default profile fragment
                    (activity as AppCompatActivity).switchFragment(ProfileViewStatus.PROFILE)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_community, container, false)
    }

}