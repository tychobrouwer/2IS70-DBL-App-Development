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
import com.example.weclean.R
import com.example.weclean.backend.FireBase
import com.example.weclean.ui.login.LoginActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class ProfileEdit : Fragment() {
    // FireBase class instance to communicate with the database
    private val fireBase = FireBase()
    private val dbAuth = Firebase.auth

    // User ID and email
    private val userId = fireBase.currentUserId()
    private val email = fireBase.currentUserEmail()

    // View of the fragment
    private lateinit var viewIt: View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewIt = view

        if (userId.isNullOrEmpty()) {
            // If no user is logged in, return to login screen
            startActivity(Intent(activity as AppCompatActivity, LoginActivity::class.java))
            return
        }

        // Set the fields in the profile edit fragment
        setFields(view)

        // Set the email field to the current user's email
        val emailView = view.findViewById<EditText>(R.id.email)
        emailView.setText(email)

        // Delete profile button
        val deleteButton = view.findViewById<Button>(R.id.delete_button)
        deleteButton.setOnClickListener {
            // Open dialog to confirm deletion of user profile
            deleteProfileDialog()
        }

        // Cancel edit profile button
        val cancelButton = view.findViewById<Button>(R.id.cancel_button)
        cancelButton.setOnClickListener {
            // Switch back to default profile fragment
            val context = activity as AppCompatActivity
            context.switchFragment(ProfileViewStatus.PROFILE)
        }

        // Confirm profile edit button
        val confirmButton = view.findViewById<Button>(R.id.confirm_button)
        confirmButton.setOnClickListener {
            // Get the email and username fields
            val username = view.findViewById<EditText>(R.id.username).text.toString().trim()
            val emailNew = emailView.text.toString().trim()

            runBlocking {
                // Get the current user
                val currentAuth = dbAuth.currentUser
                if (currentAuth == null) {
                    Toast.makeText(context, "Failed to get current user", Toast.LENGTH_SHORT)
                        .show()
                    startActivity(Intent(activity as AppCompatActivity, LoginActivity::class.java))

                    return@runBlocking
                }

                // Update the user's username information
                val updateUsername = fireBase.updateValue("Users", userId, "username", username)
                if (!updateUsername) {
                    Toast.makeText(context, "Failed to update username", Toast.LENGTH_SHORT).show()

                    return@runBlocking
                }

                if (emailNew != email) {
                    // Verify and update the user's email
                    val updateEmailResult = currentAuth.updateEmail(emailNew)

                    // Delay for the email to be updated in the database
                    Thread.sleep(300)
                }

                Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                (activity as AppCompatActivity).switchFragment(ProfileViewStatus.PROFILE)
            }
        }
    }

    /**
     * Dialog to confirm deletion of user profile
     */
    private fun deleteProfileDialog() {
        // Builder for alert dialog popup
        val builder = AlertDialog.Builder(activity as AppCompatActivity)
        builder.setCancelable(true)

        // Create dialog from builder
        val deleteDialog = builder.create()

        // Inflate dialog from R.layout.delete_profile_confirm
        val dialogLayout = layoutInflater.inflate(R.layout.dialog_delete_profile, null)

        // Show alert dialog
        deleteDialog.setView(dialogLayout)
        deleteDialog.show()

        // Join/confirm button
        val deleteButton = deleteDialog.findViewById<Button>(R.id.delete_button)
        val cancelButton = deleteDialog.findViewById<Button>(R.id.cancel_button)

        if (deleteButton == null || cancelButton == null) {
            return
        }

        // Dismiss dialog on cancel button
        cancelButton.setOnClickListener {
            deleteDialog.dismiss()
        }

        // Listener for delete button
        deleteButton.setOnClickListener {
            runBlocking {
                // Delete user profile from database
                val result = fireBase.deleteDocument("Users", userId!!)

                // If successful, delete the user's account
                if (result) {
                    val currentAuth = dbAuth.currentUser
                    if (currentAuth == null) {
                        Toast.makeText(context, "Failed to get current user", Toast.LENGTH_SHORT)
                            .show()

                        deleteDialog.dismiss()
                        startActivity(Intent(activity as AppCompatActivity, LoginActivity::class.java))

                        return@runBlocking
                    }

                    try {
                        // Delete the user's account
                        currentAuth.delete().await()

                        Toast.makeText(context, "Account deleted", Toast.LENGTH_SHORT).show()
                        deleteDialog.dismiss()

                        // Return to login screen
                        startActivity(Intent(activity as AppCompatActivity, LoginActivity::class.java))

                    } catch (e: Exception) {
                        Toast.makeText(context, "Failed to delete account", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


    /**
     * Set the fields in the profile edit fragment
     */
    private fun setFields(view: View) {
        runBlocking {
            launch {
                if (userId != null) {
                    // Get the user's data from the database
                    val userData = fireBase.getDocument("Users", userId)

                    if (userData != null) {
                        // Get the user's data from the database result
                        val email = userData.getString("email")
                        val username = userData.getString("username")

                        // Set the fields in the profile edit fragment
                        val emailView = view.findViewById<EditText>(R.id.email)
                        emailView.setText(email)

                        val usernameView = view.findViewById<EditText>(R.id.username)
                        usernameView.setText(username)
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
        return inflater.inflate(R.layout.fragment_profile_edit, container, false)
    }
}