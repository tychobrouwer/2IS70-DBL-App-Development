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

    private var maxNumber = 0

    private var nextNumber = 0

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
            // TODO: add community  information with backend
            val communityNameEditText = view.findViewById<EditText>(R.id.community_name)
            val communityName = communityNameEditText.text.toString()

            val communityEmailEditText = view.findViewById<EditText>(R.id.community_email)
            val communityEmail = communityEmailEditText.text.toString().trim()

            val communityLocationEditText = view.findViewById<EditText>(R.id.community_location)
            val communityLocation = communityLocationEditText.text.toString()

            val communityConfirmEmailEditText =
                view.findViewById<EditText>(R.id.community_confirm_email)
            val userConfirmEmail = communityConfirmEmailEditText.text.toString().trim()

            if (communityName.isEmpty() || communityEmail.isEmpty() ||
                communityLocation.isEmpty() || userConfirmEmail.isEmpty()
            ) {
                Toast.makeText(context, "Password is not matching", Toast.LENGTH_SHORT).show()
            }

            //check if valid email address
            val emailExists: Boolean = checkEmailExists(userConfirmEmail)

            if (!emailExists) {
                Toast.makeText(context, "Enter your valid email address", Toast.LENGTH_SHORT).show()
            }

            var firstName: String? = null
            var lastName: String? = null
            var email: String? = null

            var documentRef : MutableMap<String, Any> = mutableMapOf()

            //Now email exists...
            db.collection("Community").document("No Community").collection("Users").get().addOnSuccessListener { documents ->

                for (document in documents) {
                    val userData = document.data

                    val e1 = userData["firstName"] as? String
                    val e2 = userData["lastName"] as? String
                    val e3 = userData["email"] as? String
                    if (e3 == userConfirmEmail) {

                        firstName = e1.toString()
                        lastName = e2.toString()
                        email = e3.toString()

                        documentRef = userData

                        break
                    }
                }
            }.addOnFailureListener { e ->
                println("Error retrieving user: $e")
            }

            //TODO: Delete user from "No Community" page
            db.collection("Community").document("No Community").collection("Users")
                .whereEqualTo("firstName", firstName)
                .whereEqualTo("lastName", lastName)
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    // Loop through the query results
                    for (document in querySnapshot.documents) {
                        // Delete the user document
                        document.reference.delete()
                            .addOnSuccessListener {
                                println("User document successfully deleted.")
                            }
                            .addOnFailureListener { e ->
                                println("Error deleting user document: $e")
                            }
                    }
                }
                .addOnFailureListener { e ->
                    println("Error retrieving user documents: $e")
                }

            //TODO: Create the new community and add the user to it
            db.collection("Community").get().addOnSuccessListener { documents ->
                for (document in documents) {
                    val userId = document.id
                    // Assuming document IDs are in the format "userx", extract the integer value of 'x'
                    val userNumber = userId.removePrefix("community").toIntOrNull() ?: continue
                    if (userNumber > maxNumber) {
                        maxNumber = userNumber
                    }
                }
                nextNumber = maxNumber + 1
                println("Next possible value for 'x': $nextNumber")

                // Add the user to newly generated community
                db.collection("Community").document("community$nextNumber").
                collection("Users")
                    .add(documentRef)
                    .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!") }
                    .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }

            }.addOnFailureListener { e ->
                println("Error getting documents: $e")
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

    //TODO: Change this method to check the email signed in with:
    private fun checkEmailExists(email : String) : Boolean {
        firebaseAuth = FirebaseAuth.getInstance()

        var k: Boolean = false

        db.collection("Users").get().addOnSuccessListener { documents ->
            for (document in documents) {
                val userData = document.data
                val e2 = userData["email"] as? String
                if (e2 == email) {
                    k = true
                }
            }
        }.addOnFailureListener { exception ->
            // Handle failures
            println("Error: ${exception.message}")
        }

        return k;
    }

}