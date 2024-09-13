package com.rosebank.st10070002.chirpquest

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterPage : AppCompatActivity() {

    private lateinit var firstName: EditText
    private lateinit var surname: EditText
    private lateinit var username: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var registerBtn: Button
    private lateinit var gotoLogin: Button
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_page)

        firstName = findViewById(R.id.NameET)
        surname = findViewById(R.id.SurnameET)
        username = findViewById(R.id.UsernameET)
        email = findViewById(R.id.EmailET)
        password = findViewById(R.id.PasswordET)
        registerBtn = findViewById(R.id.registerBtn)
        gotoLogin = findViewById(R.id.gotoLogin)

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        registerBtn.setOnClickListener { RegisterClick() }
        gotoLogin.setOnClickListener { LoginPageClick() }
    }

    private fun RegisterClick() {
        val firstNameStr = firstName.text.toString().trim()
        val surnameStr = surname.text.toString().trim()
        val usernameStr = username.text.toString().trim()
        val emailStr = email.text.toString().trim()
        val passwordStr = password.text.toString().trim()

        // Field validations
        if (firstNameStr.isEmpty()) {
            firstName.error = "First name is required"
            firstName.requestFocus()
            return
        }
        if (surnameStr.isEmpty()) {
            surname.error = "Surname is required"
            surname.requestFocus()
            return
        }
        if (usernameStr.isEmpty()) {
            username.error = "Username is required"
            username.requestFocus()
            return
        }
        if (emailStr.isEmpty()) {
            email.error = "Email is required"
            email.requestFocus()
            return
        }
        if (passwordStr.isEmpty()) {
            password.error = "Password is required"
            password.requestFocus()
            return
        }
        if (passwordStr.length < 6) {
            password.error = "Password must be at least 6 characters"
            password.requestFocus()
            return
        }

        // Register the user with Firebase Authentication
        fAuth.createUserWithEmailAndPassword(emailStr, passwordStr)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Send verification email
                    val user = fAuth.currentUser
                    user?.sendEmailVerification()?.addOnCompleteListener { verifyTask ->
                        if (verifyTask.isSuccessful) {
                            // Store user details in Firestore
                            val userId = fAuth.currentUser?.uid
                            val userMap = hashMapOf(
                                "FirstName" to firstNameStr,
                                "Surname" to surnameStr,
                                "Username" to usernameStr,
                                "Email" to emailStr
                            )

                            if (userId != null) {
                                fStore.collection("users").document(userId)
                                    .set(userMap)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "User Registered. Please verify your email.", Toast.LENGTH_LONG).show()
                                        startActivity(Intent(this, LoginPage::class.java))
                                        finish()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Failed to store user: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                            }
                        } else {
                            Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun LoginPageClick() {
        // Redirect to Login Page
        startActivity(Intent(this, LoginPage::class.java))
    }
}
