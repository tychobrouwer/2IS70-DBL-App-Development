package com.example.weclean.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.weclean.R

class ProfileInfo : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Edit profile button
        val editButton = view.findViewById<Button>(R.id.edit_button)
        editButton.setOnClickListener {
            // Switch to edit profile view fragment
            val context = activity as AppCompatActivity
            context.switchEditFragment(ProfileViewStatus.PROFILE_EDIT, ProfileViewStatus.PROFILE)
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