package com.rosebank.st10070002.chirpquest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rosebank.st10070002.chirpquest.databinding.ActivityLoginPageBinding

class LoginPage : AppCompatActivity() {

    private lateinit var usernameLP: EditText
    private lateinit var passwordLP: EditText
    private lateinit var loginBtn: Button
    private lateinit var gotoRegister: Button
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private lateinit var binding: ActivityLoginPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        usernameLP = findViewById(R.id.UsernameETLogin)
        passwordLP = findViewById(R.id.loginPasswordLP)
        loginBtn = findViewById(R.id.LoginBTN)
        gotoRegister = findViewById(R.id.SignUpBTN)

        // Set OnClickListener for the login button
        loginBtn.setOnClickListener {
            val usernameStr = usernameLP.text.toString().trim()
            val passwordStr = passwordLP.text.toString().trim()

            // Field validation
            if (usernameStr.isEmpty()) {
                usernameLP.error = "Username is required"
                usernameLP.requestFocus()
                return@setOnClickListener
            }

            if (passwordStr.isEmpty()) {
                passwordLP.error = "Password is required"
                passwordLP.requestFocus()
                return@setOnClickListener
            }

            // Authenticate user with email and password
            fAuth.signInWithEmailAndPassword(usernameStr, passwordStr)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = fAuth.currentUser

                        // Check if email is verified
                        if (user != null && user.isEmailVerified) {
                            // Check if the username matches the email stored in Firestore
                            fStore.collection("users").document(user.uid).get()
                                .addOnSuccessListener { document ->
                                    if (document != null) {
                                        val storedUsername = document.getString("Username")
                                        if (storedUsername == usernameStr) {
                                            // Proceed to MainActivity
                                            startActivity(Intent(this, MainActivity::class.java))
                                            finish()
                                        } else {
                                            Toast.makeText(
                                                this,
                                                "Username does not match the registered email",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    } else {
                                        Toast.makeText(this, "User data not found", Toast.LENGTH_LONG).show()
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Log.e("LoginPage", "Error fetching user data", e)
                                    Toast.makeText(this, "Failed to retrieve user data", Toast.LENGTH_LONG).show()
                                }
                        } else {
                            Toast.makeText(this, "Please verify your email before logging in", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        gotoRegister.setOnClickListener {
            RegisterPageClick()
        }
    }

    fun RegisterPageClick() {
        // Redirect to Register Page
        startActivity(Intent(this, RegisterPage::class.java))
    }
}
