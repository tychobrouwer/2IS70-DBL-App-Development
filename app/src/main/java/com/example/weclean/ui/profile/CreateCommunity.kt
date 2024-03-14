package com.example.weclean.ui.profile

import android.content.ContentValues
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

class CreateCommunity : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    //private val db = FirebaseFirestore.getInstance()
    private val db = Firebase.firestore

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        // Cancel create community button
        val cancelButton = view.findViewById<Button>(R.id.cCancel_button)
        cancelButton.setOnClickListener {
            // Switch back to default profile fragment
            val context = activity as AppCompatActivity
            context.switchEditFragment(toEdit = false)
        }

        // Confirm create community button
        val confirmButton = view.findViewById<Button>(R.id.cConfirm_button)
        confirmButton.setOnClickListener {

            //get all text input fields
            val communityNameEditText = view.findViewById<EditText>(R.id.community_name)
            val communityName = communityNameEditText.text.toString()

            val communityEmailEditText = view.findViewById<EditText>(R.id.community_email)
            val communityEmail = communityEmailEditText.text.toString().trim()

            val communityLocationEditText = view.findViewById<EditText>(R.id.community_location)
            val communityLocation = communityLocationEditText.text.toString()

            val communityConfirmEmailEditText = view.findViewById<EditText>(R.id.community_confirm_email)
            val userConfirmEmail = communityConfirmEmailEditText.text.toString().trim()

            //ensure fields are not empty
            if (communityName.isEmpty() || communityEmail.isEmpty() ||
                communityLocation.isEmpty() || userConfirmEmail.isEmpty()) {
                Toast.makeText(context, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }

            //TODO: Create a community with fields listed above,
            //TODO: Add the user to this community in a collection "Users" (the user who created the community) to Community/C_Name/Users where C_Name is the chosen Community name
            //TODO: Remove the user from Community/No Community/Users as they are added to Community/C_Name/Users...

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