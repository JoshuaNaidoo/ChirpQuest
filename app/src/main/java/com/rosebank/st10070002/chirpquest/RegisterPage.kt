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
    private var valid = true

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
}