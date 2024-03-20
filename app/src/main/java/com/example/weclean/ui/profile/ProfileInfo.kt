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
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class ProfileInfo : Fragment() {
    private val db = Firebase.firestore
    private val dbAuth = FirebaseAuth.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Edit profile button
        val editButton = view.findViewById<Button>(R.id.edit_button)
        editButton.setOnClickListener {
            // Switch to edit profile view fragment
            val context = activity as AppCompatActivity
            context.switchFragment(ProfileViewStatus.PROFILE_EDIT, ProfileViewStatus.PROFILE)
        }

        view.findViewById<TextView>(R.id.email).text = dbAuth.currentUser?.email

        db.collection("Users").document(dbAuth.currentUser?.uid.toString()).get()
            .addOnSuccessListener { document ->
                val statLitteringEntries = (document.get("litteringEntries") as ArrayList<*>?)?.size ?: 0

                view.findViewById<TextView>(R.id.username).text = document.getString("username")
                view.findViewById<TextView>(R.id.region).text = document.getString("country")
                view.findViewById<TextView>(R.id.littering_entries).text = statLitteringEntries.toString()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error getting user information", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_info, container, false)
    }
}