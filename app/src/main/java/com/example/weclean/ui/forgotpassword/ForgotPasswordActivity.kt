package com.example.weclean.ui.forgotpassword

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weclean.databinding.ActivityForgotPasswordBinding
import com.example.weclean.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

/**
 * Gets the email the user puts in and sends a reset password mail.
 */
class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.toLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.reset.setOnClickListener {
            val emailAddress = binding.email.text.toString().trim()

            if (emailAddress.isNotEmpty()) {
                sendPasswordResetEmail(emailAddress)
            } else {
                Toast.makeText(this, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Sends an email to the user to reset their password.
     * If it cant send, it gives an error message.
     *
     * @param emailAddress the email address the user has put in.
     */
    private fun sendPasswordResetEmail(emailAddress: String) {
        firebaseAuth.sendPasswordResetEmail(emailAddress)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Reset email send", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                } else {
                    Toast.makeText(this, "Failed to send reset email", Toast.LENGTH_SHORT).show()
                }
            }
    }
}