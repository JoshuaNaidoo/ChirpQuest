package com.rosebank.st10070002.chirpquest

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.rosebank.st10070002.chirpquest.databinding.ActivitySettingsPageBinding

class SettingsPage : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load saved metrics setting
        loadSavedMetrics()

        // Listener for the distance setting
        binding.enterMaxDistance.setOnEditorActionListener { textView, actionId, keyEvent ->
            handleDistanceInput()
            true
        }

        // Listener for the radio group selection
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val selectedMetric = when (checkedId) {
                R.id.radioButton1 -> "Miles"
                R.id.radioButton2 -> "Kilometers"
                else -> "Kilometers" // Default
            }
            saveMetric(selectedMetric)
            Toast.makeText(this, "Selected metric: $selectedMetric", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleDistanceInput() {
        val distanceInput = binding.enterMaxDistance.text.toString()

        if (distanceInput.isNotEmpty()) {
            val maxDistance = distanceInput.toInt()

            if (maxDistance > 50) {
                Toast.makeText(this, "Max distance cannot exceed 50 km", Toast.LENGTH_SHORT).show()
            } else {
                // Save the max distance in SharedPreferences
                saveRadius(maxDistance) // Call the saveRadius function
                Toast.makeText(this, "Max distance saved: $maxDistance km", Toast.LENGTH_SHORT).show()
                finish() // Go back to the previous screen
            }
        } else {
            Toast.makeText(this, "Please enter a valid distance", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to save the radius in SharedPreferences
    private fun saveRadius(radius: Int) {
        val sharedPreferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putInt("radius", radius) // Save the radius
            apply()
        }
    }

    // Function to save the selected metric in SharedPreferences
    private fun saveMetric(metric: String) {
        val sharedPreferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("metric", metric) // Save the selected metric
            apply()
        }
    }

    // Load the saved metric from SharedPreferences
    private fun loadSavedMetrics() {
        val sharedPreferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val metric = sharedPreferences.getString("metric", "Kilometers") // Default to Kilometers

        when (metric) {
            "Miles" -> binding.radioButton1.isChecked = true
            "Kilometers" -> binding.radioButton2.isChecked = true
        }
    }

    fun onClickableTextClick(view: View) {
        Toast.makeText(this, "View Profile button clicked", Toast.LENGTH_SHORT).show()
    }
}
