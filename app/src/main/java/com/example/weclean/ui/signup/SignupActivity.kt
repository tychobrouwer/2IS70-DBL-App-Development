package com.example.weclean.ui.signup

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weclean.ui.home.Home
import com.example.weclean.databinding.ActivitySignupBinding
import com.example.weclean.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.example.weclean.backend.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import android.content.Context
import android.content.SharedPreferences


private val user1 = User()

class SignupActivity : AppCompatActivity() {

    //variables for sign up activity and firebase authentication
    private lateinit var binding:ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth
    //private val db = FirebaseFirestore.getInstance()
    private val db = Firebase.firestore

    // SharedPreferences file name
    private val PREFS_FILENAME = "com.example.weclean.backend"

    // Key for saving email
    private val EMAIL_KEY = "user_email"

    // SharedPreferences instance
    private lateinit var sharedPreferences: SharedPreferences


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

        firebaseAuth = FirebaseAuth.getInstance()

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)

        //if account then navigate to the page where they confirm they have an account
        binding.yesAccount.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        //if the button to signup is clicked
        binding.signup.setOnClickListener {

            //get all text input fields
            val email = binding.registerUsername.text.toString()
            val password = binding.registerPassword.text.toString()
            val confirmPassword = binding.confirmPassword.text.toString()
            val firstName = binding.firstName.text.toString()
            val lastName = binding.lastName.text.toString()

            //ensuring the fields are not empty
            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()
                && firstName.isNotEmpty() && lastName.isNotEmpty()) {

                //and password matches with the confirm password
                if (password == confirmPassword) {

                    //create the user
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            saveUserEmail(email)
                            //add user to the database
                            val user = user1.createUser(firstName, lastName, email)

                            //add user to Community/No Community/Users/...
                            db.collection("Community").document("No Community").
                            collection("Users").add(user)
                                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }

                            //navigate to the home screen
                            val intent = Intent(this, Home::class.java)
                            startActivity(intent)

                        } else if (it.exception is FirebaseAuthUserCollisionException) {
                            Toast.makeText(this, "Email is already in use", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }

                } else {
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to save user email to SharedPreferences
    private fun saveUserEmail(email: String) {
        val editor = sharedPreferences.edit()
        editor.putString(EMAIL_KEY, email)
        editor.apply()
    }

    // Function to retrieve user email from SharedPreferences
    private fun getUserEmail(): String? {
        return sharedPreferences.getString(EMAIL_KEY, null)
    }
}