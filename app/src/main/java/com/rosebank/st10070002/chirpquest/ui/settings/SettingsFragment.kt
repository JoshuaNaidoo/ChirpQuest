package com.rosebank.st10070002.chirpquest.ui.settings

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rosebank.st10070002.chirpquest.R
import com.rosebank.st10070002.chirpquest.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private val fStore = FirebaseFirestore.getInstance()
    private val fAuth = FirebaseAuth.getInstance()
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Retrieve the current user's ID
        val userId = fAuth.currentUser?.uid

        if (userId != null) {
            // Fetch user details from Firestore
            fetchUserDetails(userId)
        } else {
            // Handle the case when user is not logged in
            Log.e("SettingsFragment", "User not logged in")
        }

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
                else -> "Kilometers" // Default metric
            }
            saveMetric(selectedMetric)
            Toast.makeText(requireContext(), "Selected metric: $selectedMetric", Toast.LENGTH_SHORT)
                .show()
        }

        return root
    }

    private fun handleDistanceInput() {
        val distanceInput = binding.enterMaxDistance.text.toString()

        if (distanceInput.isNotEmpty()) {
            val maxDistance = distanceInput.toInt()

            if (maxDistance > 50) {
                Toast.makeText(requireContext(), "Max distance cannot exceed 50 km", Toast.LENGTH_SHORT).show()
            } else {
                // Save the max distance in SharedPreferences
                saveRadius(maxDistance)
                Toast.makeText(requireContext(), "Max distance saved: $maxDistance km", Toast.LENGTH_SHORT).show()
                // Optionally navigate back or do any other action
            }
        } else {
            Toast.makeText(requireContext(), "Please enter a valid distance", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to save the radius in SharedPreferences
    private fun saveRadius(radius: Int) {
        val sharedPreferences = requireActivity().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putInt("radius", radius) // Save the radius
            apply()
        }
    }

    // Function to save the selected metric in SharedPreferences
    private fun saveMetric(metric: String) {
        val sharedPreferences = requireActivity().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("distance_metric", metric)
            apply()
        }
    }

    // Load the saved metric
    private fun loadSavedMetrics() {
        val sharedPreferences = requireActivity().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val savedMetric = sharedPreferences.getString("distance_metric", "Kilometers")
        if (savedMetric == "Miles") {
            binding.radioButton1.isChecked = true
        } else {
            binding.radioButton2.isChecked = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding reference to prevent memory leaks
    }
    private fun fetchUserDetails(userId: String) {
        fStore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    // Assuming the document contains the username field
                    val username = document.getString("username")
                    if (username != null) {
                        // Display the username in the TextView
                        binding.UserName.text = username
                    } else {
                        Log.e("SettingsFragment", "Username not found in Firestore")
                    }
                } else {
                    Log.e("SettingsFragment", "No such document")
                }
            }
            .addOnFailureListener { e ->
                Log.e("SettingsFragment", "Error fetching user details: ${e.message}")
            }
    }
}
