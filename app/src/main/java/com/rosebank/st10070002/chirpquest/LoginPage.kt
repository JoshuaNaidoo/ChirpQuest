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
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.rosebank.st10070002.chirpquest.databinding.ActivityLoginPageBinding
import com.rosebank.st10070002.chirpquest.ui.home.HomeFragment

class LoginPage : AppCompatActivity()   {

    private lateinit var usernameLP: EditText
    private lateinit var passwordLP: EditText
    private lateinit var loginBtn: Button
    private lateinit var gotoRegister: Button
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    // Declare the binding object
    private lateinit var binding: ActivityLoginPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login_page)
        // Inflate the layout using ViewBinding
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
            val username = usernameLP.text.toString().trim()
            val password = passwordLP.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                loginWithUsername(username, password)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun RegisterPageClick(view: View) {
    // Redirect to Register Page
    startActivity(Intent(this, RegisterPage::class.java))

}
    private fun loginWithUsername(username: String, password: String) {
        // Query Firestore to get the email associated with the username
        fStore.collection("users").whereEqualTo("username", username).get().addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                // Get the email of the user
                val email = documents.documents[0].getString("email")

                // Now login with the email and password using FirebaseAuth
                email?.let {
                    fAuth.signInWithEmailAndPassword(it, password).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                             startActivity(Intent(this, MainActivity::class.java))
                        } else {
                            Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Username not found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
