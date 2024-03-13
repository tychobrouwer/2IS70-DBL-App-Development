package com.example.weclean.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.weclean.R

class CreateCommunity : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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