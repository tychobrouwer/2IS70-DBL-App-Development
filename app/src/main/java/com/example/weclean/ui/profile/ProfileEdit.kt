package com.example.weclean.ui.profile

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weclean.R
import com.example.weclean.backend.FireBase
import com.example.weclean.ui.login.LoginActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Calendar

class ProfileEdit : Fragment(), DatePickerDialog.OnDateSetListener {
    // FireBase class instance to communicate with the database
    private val fireBase = FireBase()
    private val dbAuth = Firebase.auth

    private val userId = fireBase.currentUserId()
    private val email = fireBase.currentUserEmail()

    // Calendar object for the date and time of the event
    private var date = Calendar.getInstance()

    private lateinit var viewIt: View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewIt = view

        if (userId.isNullOrEmpty()) {
            // If no user is logged in, return to login screen
            startActivity(Intent(activity as AppCompatActivity, LoginActivity::class.java))
            return
        }

        setFields(view)

        // Set the email field to the current user's email
        val emailView = view.findViewById<EditText>(R.id.email)
        emailView.setText(email)

        val dobView = view.findViewById<TextView>(R.id.birthdate)
        dobView.setOnClickListener {
            var dateToOpen = date
            // Create a date picker dialog
            if (date.timeInMillis == 0L) {
                dateToOpen = Calendar.getInstance()
            }

            val datePicker = DatePickerDialog(
                this.requireContext(),
                this,
                dateToOpen.get(Calendar.YEAR),
                dateToOpen.get(Calendar.MONTH),
                dateToOpen.get(Calendar.DAY_OF_MONTH))

            // Show the date picker dialog
            datePicker.show()
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
            val region = view.findViewById<EditText>(R.id.region)
            val country = region.text.toString().trim()

            val emailNew = emailView.text.toString().trim()

            runBlocking {
                val updateCountry = fireBase.updateValue("Users", userId, "country", country)
                if (!updateCountry) {
                    Toast.makeText(context, "Failed to update country", Toast.LENGTH_SHORT).show()
                }

                if (date.timeInMillis != 0L) {
                    val updateDobResult = fireBase.updateValue("Users", userId, "dob", date.time)
                    if (!updateDobResult) {
                        Toast.makeText(context, "Failed to update date of birth", Toast.LENGTH_SHORT).show()
                    }
                }

                if (emailNew != email) {
                    // Get the current user
                    val currentAuth = dbAuth.currentUser
                    if (currentAuth == null) {
                        Toast.makeText(context, "Failed to get current user", Toast.LENGTH_SHORT)
                            .show()
                        Intent(activity as AppCompatActivity, LoginActivity::class.java)

                        return@runBlocking
                    }

                    // Verify and update the user's email
                    val updateEmailResult = currentAuth.verifyBeforeUpdateEmail(emailNew)
                    if (updateEmailResult.isSuccessful) {
                        dbAuth.updateCurrentUser(currentAuth)
                    } else {
                        Toast.makeText(context, "Failed to update email", Toast.LENGTH_SHORT).show()

                        return@runBlocking
                    }
                }

                Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                (activity as AppCompatActivity).switchFragment(ProfileViewStatus.PROFILE)
            }
        }
    }

    private fun setFields(view: View) {
        runBlocking {
            launch {
                if (userId != null) {
                    val userData = fireBase.getDocument("Users", userId)
                    if (userData != null) {
                        val email = userData.getString("email")
                        val dob = userData.getDate("dob")
                        val country = userData.getString("country")

                        val emailView = view.findViewById<EditText>(R.id.email)
                        emailView.setText(email)

                        val dobView = view.findViewById<TextView>(R.id.birthdate)
                        date.timeInMillis = dob?.time ?: 0

                        println(date.timeInMillis)
                        println(dob?.time)

                        if (date.timeInMillis != 0L) {
                            dobView.text = getString(R.string.date_format,
                                date.get(Calendar.DAY_OF_MONTH),
                                date.get(Calendar.MONTH) + 1,
                                date.get(Calendar.YEAR))
                        } else {
                            dobView.text = "-"
                        }

                        val region = view.findViewById<EditText>(R.id.region)
                        region.setText(country)
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

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        // Set the date of the event from the date picker
        date.set(Calendar.YEAR, year)
        date.set(Calendar.MONTH, month)
        date.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        // Set the date text view to the selected date
        val dobView = viewIt.findViewById<TextView>(R.id.birthdate)
        dobView.text = getString(R.string.date_format, dayOfMonth, month + 1, year)
    }
}