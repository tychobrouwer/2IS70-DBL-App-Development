package com.example.weclean.ui.signup

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weclean.backend.FireBase
import com.example.weclean.ui.home.Home
import com.example.weclean.databinding.ActivitySignupBinding
import com.example.weclean.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.example.weclean.backend.User
import kotlinx.coroutines.runBlocking


class SignupActivity : AppCompatActivity() {
    // Variables for sign up activity and firebase authentication
    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth

    // FireBase class instance to communicate with the database
    private val fireBase = FireBase()

    /**
     * Signs up user, adds their data to FireStore and then
     * navigates to the home screen
     *
     * @param savedInstanceState
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the firebase authentication instance
        firebaseAuth = FirebaseAuth.getInstance()

        // If account then navigate to the page where they confirm they have an account
        binding.yesAccount.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // If the button to signup is clicked
        binding.signup.setOnClickListener {

            // Get all text input fields
            val email = binding.email.text.toString()
            val password = binding.registerPassword.text.toString()
            val confirmPassword = binding.confirmPassword.text.toString()
            val username = binding.username.text.toString()

            // Ensure fields are not empty
            if (email.isEmpty() ||
                password.isEmpty() ||
                confirmPassword.isEmpty() ||
                username.isEmpty()
            ) {
                Toast.makeText(this, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
            }

            // Ensure password and confirm password match
            if (password != confirmPassword) {
                Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
            }
            
            //create the user
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                val errorMessage = "Error creating user"

                // If the user already exists
                if (it.exception is FirebaseAuthUserCollisionException) {
                    Toast.makeText(this, "Email is already in use", Toast.LENGTH_SHORT).show()

                    return@addOnCompleteListener
                }

                // If the user was not created successfully
                if (!it.isSuccessful) {
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    return@addOnCompleteListener
                }

                // Get the new user's ID
                val uid = it.result.user?.uid

                // If the user ID is null then show an error message
                if (uid == null) {
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    return@addOnCompleteListener
                }

                // Create a new user object
                val user = User().createUser(username)

                runBlocking {
                    // Add the user to the database
                    val addUserResult = fireBase.addDocumentWithName("Users", uid, user)

                    // If the user was not added successfully
                    if (!addUserResult) {
                        Toast.makeText(this@SignupActivity, errorMessage, Toast.LENGTH_SHORT).show()
                        return@runBlocking
                    }

                    // Navigate to the home screen
                    val intent = Intent(this@SignupActivity, Home::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}