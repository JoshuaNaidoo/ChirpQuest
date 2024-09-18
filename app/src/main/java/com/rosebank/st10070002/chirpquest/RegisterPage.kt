package com.rosebank.st10070002.chirpquest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterPage : AppCompatActivity() {

    private lateinit var FirstName: EditText
    private lateinit var Surname: EditText
    private lateinit var Username: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var registerBtn: Button
    private lateinit var gotoLogin: Button
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_page)

        FirstName = findViewById(R.id.NameET)
        Surname = findViewById(R.id.SurnameET)
        Username = findViewById(R.id.UsernameET)
        email = findViewById(R.id.EmailET)
        password = findViewById(R.id.PasswordET)
        registerBtn = findViewById(R.id.registerBtn)
        gotoLogin = findViewById(R.id.gotoLogin)

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

    }

    fun RegisterClick(view: View) {
        val firstName = FirstName.text.toString().trim()
        val lastName = Surname.text.toString().trim()
        val username = Username.text.toString().trim()
        val email = email.text.toString().trim()
        val password = password.text.toString().trim()

        if (firstName.isNotEmpty() && lastName.isNotEmpty() && username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
            registerUser(firstName, lastName, username, email, password)

        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        }
    }

    fun LoginPageClick(view: View) {
        // Redirect to Login Page
        startActivity(Intent(this, LoginPage::class.java))
    }

        private fun registerUser(firstName: String, lastName: String, username: String, email: String, password: String) {
        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Store user details in Firestore
                val userId = fAuth.currentUser?.uid
                val userMap = hashMapOf(
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "username" to username,
                    "email" to email
                )
                userId?.let {
                    fStore.collection("users").document(it).set(userMap).addOnSuccessListener {
                        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginPage::class.java))
                    }.addOnFailureListener { e ->
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
