package com.example.weclean.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weclean.ui.home.Home
import com.example.weclean.databinding.ActivityLoginBinding
import com.example.weclean.ui.forgotpassword.ForgotPasswordActivity
import com.example.weclean.ui.signup.SignupActivity
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    /**
     * Signs in the user and throws exceptions if there is an error in logging in
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        // If the user has no account, go to the signup page
        binding.noAccount?.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        // If the user forgot their password, go to the forgot password page
        binding.forgotPassword?.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        // If the user logged in, go to the home page
        binding.login.setOnClickListener {
            // Get the email and password from the user input
            val email = binding.username.text.toString()
            val password = binding.password.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // now login the user
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                when {
                    it.isSuccessful -> {
                        // If the login is successful, go to the home page
                        val intent = Intent(this, Home::class.java)
                        startActivity(intent)
                    }
                    it.exception is FirebaseAuthInvalidCredentialsException -> {
                        Toast.makeText(this, "Incorrect credential(s)", Toast.LENGTH_SHORT).show()
                    }
                    it.exception is FirebaseTooManyRequestsException -> {
                        Toast.makeText(this, "Too many login attempts, try again later", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}