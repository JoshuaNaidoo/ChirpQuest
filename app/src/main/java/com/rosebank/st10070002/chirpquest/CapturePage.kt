package com.rosebank.st10070002.chirpquest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rosebank.st10070002.chirpquest.databinding.ActivityCapturePageBinding

class CapturePage : AppCompatActivity() {

    private lateinit var binding: ActivityCapturePageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCapturePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Add any initialization or listeners for CapturePage here
    }

    // Additional methods for capturing images or other functionality can be added here
}