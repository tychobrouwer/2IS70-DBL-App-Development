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
    //variables for sign up activity and firebase authentication
    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth

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

        firebaseAuth = FirebaseAuth.getInstance()

        //if account then navigate to the page where they confirm they have an account
        binding.yesAccount.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        //if the button to signup is clicked
        binding.signup.setOnClickListener {

            //get all text input fields
            val email = binding.email.text.toString()
            val password = binding.registerPassword.text.toString()
            val confirmPassword = binding.confirmPassword.text.toString()
            val username = binding.username.text.toString()

            if (email.isEmpty() ||
                password.isEmpty() ||
                confirmPassword.isEmpty() ||
                username.isEmpty()
            ) {
                Toast.makeText(this, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
            }
            
            //create the user
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    val uid = it.result.user?.uid

                    if (uid == null) {
                        Toast.makeText(this, "Error creating user", Toast.LENGTH_SHORT).show()
                        return@addOnCompleteListener
                    }

                    val country = getResources().configuration.locales.get(0).country

                    val user = User().createUser(username, 0, country)

                    runBlocking {
                        val addUserResult = fireBase.addDocumentWithName("Users", uid, user)

                        if (!addUserResult) {
                            Toast.makeText(this@SignupActivity, "Error creating user", Toast.LENGTH_SHORT).show()
                            return@runBlocking
                        }
                    }

                    // navigate to the home screen
                    val intent = Intent(this, Home::class.java)
                    startActivity(intent)

                } else if (it.exception is FirebaseAuthUserCollisionException) {
                    Toast.makeText(this, "Email is already in use", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}