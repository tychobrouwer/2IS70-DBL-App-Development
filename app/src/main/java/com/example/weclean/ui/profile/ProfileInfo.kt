package com.example.weclean.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.weclean.R
import com.google.firebase.auth.FirebaseAuth

class ProfileInfo : Fragment() {
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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_info, container, false)
    }
}