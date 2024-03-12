package com.example.weclean.ui.signup

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weclean.ui.Home.Home
import com.example.weclean.databinding.ActivitySignupBinding
import com.example.weclean.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.example.weclean.backend.User
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore


private val user1 = User()

class SignupActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth
    //private val db = FirebaseFirestore.getInstance()
    private val db = Firebase.firestore

    private var maxNumber = 0

    private var nextNumber = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.yesAccount.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.signup.setOnClickListener {
            val email = binding.registerUsername.text.toString()
            val password = binding.registerPassword.text.toString()
            val confirmPassword = binding.confirmPassword.text.toString()
            val firstName = binding.firstName.text.toString()
            val lastName = binding.lastName.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()
                && firstName.isNotEmpty() && lastName.isNotEmpty()) {
                if (password == confirmPassword) {

                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            //add user to the database
                            val user = user1.createUser(firstName, lastName, email)

                            db.collection("Users").get().addOnSuccessListener { documents ->
                                for (document in documents) {
                                    val userId = document.id
                                    // Assuming document IDs are in the format "userx", extract the integer value of 'x'
                                    val userNumber = userId.removePrefix("user").toIntOrNull() ?: continue
                                    if (userNumber > maxNumber) {
                                        maxNumber = userNumber
                                    }
                                }
                                nextNumber = maxNumber + 1
                                println("Next possible value for 'x': $nextNumber")

                                // Add a new document with a generated ID
                                db.collection("Users").document("user$nextNumber")
                                    .set(user)
                                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                                    .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }

                            }.addOnFailureListener { e ->
                                println("Error getting documents: $e")
                            }

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

}