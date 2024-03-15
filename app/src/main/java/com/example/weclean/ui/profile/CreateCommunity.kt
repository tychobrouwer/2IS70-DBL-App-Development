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

private val community1 = Community()
class CreateCommunity : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    //private val db = FirebaseFirestore.getInstance()
    private val db = Firebase.firestore

    /**
     * Create a community and add the user who created the community
     * to this community
     *
     * @param view
     * @param savedInstanceState
     */
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

            //create the community
            val community = community1.createCommunity(communityName, communityEmail, communityLocation)

            var documentID = ""
            var userData: MutableMap<String, Any?> = mutableMapOf()

            //TODO: Create a community with fields listed above
            //create the community in Community/C_NAME/...(fields)...
            db.collection("Community").add(community).addOnSuccessListener { documentReference ->
                Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!")
                Toast.makeText(context, "Community created successfully!", Toast.LENGTH_SHORT).show()
                // Retrieve the ID of the added community document
                val communityId = documentReference.id

                // Add the user to this community
                addUserToCommunity(communityId, userConfirmEmail)

            }.addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.create_community, container, false)
    }

    private fun addUserToCommunity(communityId: String, userEmail : String) {

        db.collection("Community").document("No Community")
            .collection("Users").whereEqualTo("email", userEmail).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val userData = document.data

                    // Add the user to the community
                    db.collection("Community").document(communityId)
                        .collection("Users").add(userData)
                        .addOnSuccessListener {
                            Log.d(TAG, "User added successfully to the community")

                            // Now remove the user from standard community
                            db.collection("Community").document("No Community")
                                .collection("Users").whereEqualTo("email", userEmail).get()
                                .addOnSuccessListener { documents ->
                                    for (document in documents) {
                                        // Delete the user document
                                        document.reference.delete()
                                            .addOnSuccessListener {
                                                Log.d(TAG, "User document deleted successfully")
                                                // Handle success, if needed
                                            }
                                            .addOnFailureListener { e ->
                                                Log.w(TAG, "Error deleting user document", e)
                                                // Handle failure, if needed
                                            }
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Log.w(TAG, "Error getting user document: ", exception)
                                }

                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error adding user to the community", e)
                            // Handle failure, if needed
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting user document: ", exception)
            }
    }

}