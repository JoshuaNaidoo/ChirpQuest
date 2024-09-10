package com.rosebank.st10070002.chirpquest

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.rosebank.st10070002.chirpquest.ui.home.HomeFragment

class LoginPage : AppCompatActivity() {

    private lateinit var usernameLP: EditText
    private lateinit var passwordLP: EditText
    private lateinit var loginBtn: Button
    private lateinit var gotoRegister: Button
    private var valid = true
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        usernameLP = findViewById(R.id.UsernameETLogin)
        passwordLP = findViewById(R.id.loginPasswordLP)
        loginBtn = findViewById(R.id.LoginBTN)
        gotoRegister = findViewById(R.id.SignUpBTN)


    }
}