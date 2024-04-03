package com.example.weclean.ui.events

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import com.example.weclean.R
import com.example.weclean.backend.EventData
import com.example.weclean.backend.FireBase
import com.example.weclean.backend.dayStringFormat
import com.example.weclean.ui.login.LoginActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class EventDetails : Fragment() {
    // FireBase class to communicate with the database
    private val fireBase = FireBase()

    // Event data to display
    private lateinit var event : EventData

    // Boolean to check if the user is in the event
    private var userInEvent = false
    private var numPeopleSignedUp = 0

    // User ID
    private lateinit var userId: String

    /**
     * Create a new instance of the event details fragment with the event data
     *
     * @param event
     * @return EventDetails fragment
     */
    fun newInstance(event: EventData): EventDetails {
        // Create a new fragment with supplied event data
        val fragment = EventDetails()
        fragment.event = event

        return fragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Sign up button
        val signup = view.findViewById<Button>(R.id.signupButton)

        // Set the sign up button listener
        signup.setOnClickListener {
            signupForEvent(view)
        }

        // Get the current user ID
        userId = fireBase.currentUserId() ?: ""

        if (userId.isEmpty()) {
            // If the user ID is empty, go to the login activity
            Toast.makeText(activity as AppCompatActivity, "Failed to get user", Toast.LENGTH_SHORT)
                .show()
            startActivity(Intent(activity as AppCompatActivity, LoginActivity::class.java))

            return
        }

        // Update the fields on the view
        updateFields(view)
    }

    /**
     * Sign up for the event
     *
     * @param view
     */
    private fun signupForEvent(view: View) {
        runBlocking {
            launch {
                if (userInEvent) {
                    // Remove the user from the event
                    removeFromEvent()
                    // Update the sign up button
                    updateSignupButton(view)
                } else {
                    // Add the user to the event
                    addToEvent()
                    // Update the sign up button
                    updateSignupButton(view)
                }
            }
        }
    }

    /**
     * Add the user to the event
     *
     */
    private suspend fun addToEvent() {
        val failString = "Failed to sign up for event"

        // Add the user to the event
        val addUserToEvent = fireBase.addToArray("Events", event.id, "userIds", userId)

        if (!addUserToEvent) {
            Toast.makeText(activity as AppCompatActivity, failString, Toast.LENGTH_SHORT)
                .show()
        }

        // Add the event to the user
        val addEventToUser = fireBase.addToArray("Users", userId, "eventIds", event.id)

        if (!addEventToUser) {
            Toast.makeText(activity as AppCompatActivity, failString, Toast.LENGTH_SHORT)
                .show()
        }

        // Set the user in the event
        userInEvent = true
        numPeopleSignedUp += 1
        Toast.makeText(activity as AppCompatActivity, "Signed up for event", Toast.LENGTH_SHORT).show()
    }

    /**
     * Remove the user from the event
     *
     */
    private suspend fun removeFromEvent() {
        val failString = "Failed to remove sign up for event"

        // Remove the user from the event
        val removeUserFromEvent = fireBase.removeFromArray("Events", event.id, "userIds", userId)

        if (!removeUserFromEvent) {
            Toast.makeText(activity as AppCompatActivity, failString, Toast.LENGTH_SHORT)
                .show()
        }

        // Remove the event from the user
        val removeEventFromUser = fireBase.removeFromArray("Users", userId, "eventIds", event.id)

        if (!removeEventFromUser) {
            Toast.makeText(activity as AppCompatActivity, failString, Toast.LENGTH_SHORT)
                .show()
        }

        // Set the user not in the event
        userInEvent = false
        numPeopleSignedUp -= 1
        Toast.makeText(activity as AppCompatActivity, "Removed from event", Toast.LENGTH_SHORT).show()
    }

    /**
     * Update the sign up button
     *
     * @param view
     */
    private fun updateSignupButton(view: View) {
        // Sign up button for the event
        val signupButton = view.findViewById<Button>(R.id.signupButton)
        val signupText = view.findViewById<TextView>(R.id.numPeopleView)

        if (userInEvent) {
            // Update the sign up button
            signupButton.text = getText(R.string.action_sign_out)
            signupText.text = getString(R.string.people_signed_up, numPeopleSignedUp)
            signupButton.background = AppCompatResources.getDrawable(
                activity as AppCompatActivity,
                R.drawable.button_red_background)
        } else {
            // Update the sign up button
            signupButton.text = getText(R.string.action_sign_up)
            signupText.text = getString(R.string.people_signed_up, numPeopleSignedUp)
            signupButton.background = AppCompatResources.getDrawable(
                activity as AppCompatActivity,
                R.drawable.button_background)
        }
    }

    /**
     * Update the fields on the view
     *
     * @param view
     */
    private fun updateFields(view: View) {
        // Set the fields on the view
        view.findViewById<TextView>(R.id.nameView).text = event.name
        view.findViewById<TextView>(R.id.locationView).text = event.location
        view.findViewById<TextView>(R.id.timeView).text = dayStringFormat(event.timeStamp)
        view.findViewById<TextView>(R.id.descriptionView).text = event.description

        runBlocking {
            launch {
                val eventResult = fireBase.getDocument("Events", event.id)?.get("userIds") as List<*>
                numPeopleSignedUp = eventResult.size
                view.findViewById<TextView>(R.id.numPeopleView).text = getString(R.string.people_signed_up, numPeopleSignedUp)

                // Load image from Firebase Storage
                val imageView = view.findViewById<ImageView>(R.id.eventImage)

                // Get image bytes from Firebase Storage
                val imageBytes = fireBase.getFileBytes(event.imageId, 1024 * 1024)

                if (imageBytes == null) {
                    Toast.makeText(activity as AppCompatActivity, "Failed to load image", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Set image view with the image bytes
                val bmp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                imageView.setImageBitmap(bmp)

                // Get the user's event IDs from the database
                val userEvents = fireBase.getDocument("Users", userId)?.get("eventIds") as List<*>

                // Check if the user is in the event
                userInEvent = userEvents.contains(event.id)

                updateSignupButton(view)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_details, container, false)
    }
}