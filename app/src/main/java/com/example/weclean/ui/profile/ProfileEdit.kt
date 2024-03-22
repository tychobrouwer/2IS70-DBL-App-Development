package com.example.weclean.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.weclean.R
import com.example.weclean.backend.FireBase
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.example.weclean.backend.User

class ProfileEdit : Fragment() {
    private val fireBase = FireBase()
    private lateinit var firebaseAuth: FirebaseAuth
    private val db = Firebase.firestore

    private val user1 = User()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.email).text = fireBase.currentUserEmail()

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
            // TODO: Update user information with backend

            //get all text input fields
            val email = view.findViewById<EditText>(R.id.email)
            val userEmail = email.text.toString()

            val dob = view.findViewById<EditText>(R.id.birthdate)
            val dateOfBirth = dob.text.toString().trim()

            val region = view.findViewById<EditText>(R.id.region)
            val country = region.text.toString().trim()

            var documentID : String

            //Update users data:
            db.collection("Users").whereEqualTo("email", userEmail).get().addOnSuccessListener { documents ->
                for (document in documents) {
                    // Save the document ID
                    documentID = document.id

                    val data = hashMapOf(
                        "dob" to dateOfBirth,
                        "country" to country
                    )

                    //Update the document
                    // Update the document with the provided fields
                    db.document(documentID).update(data as Map<String, Any>).addOnSuccessListener {
                        println("Document with ID $documentID updated successfully.")
                    }.addOnFailureListener { exception ->
                        println("Error updating document: $exception")
                    }
                }
            }.addOnFailureListener { exception ->
                println("Error getting documents: $exception")
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