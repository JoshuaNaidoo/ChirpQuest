package com.rosebank.st10070002.chirpquest.ui.capture

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import com.rosebank.st10070002.chirpquest.R
import com.rosebank.st10070002.chirpquest.databinding.FragmentCaptureBinding
import java.util.Calendar
import java.util.Locale
import com.rosebank.st10070002.chirpquest.BirdCapture

class CaptureFragment : Fragment() {

    private var _binding: FragmentCaptureBinding? = null
    private val binding get() = _binding!!

    private lateinit var firestore: FirebaseFirestore
    private var imageUri: Uri? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCaptureBinding.inflate(inflater, container, false)

        firestore = FirebaseFirestore.getInstance()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Setup button click listeners
        binding.submitButton.setOnClickListener { submitFinding() }
        binding.addpictureButton.setOnClickListener { requestCameraPermission() }
        binding.location.setOnClickListener { requestLocationPermission() }
        binding.date.setOnClickListener { showDatePicker() }
        binding.time.setOnClickListener { showTimePicker() }

        return binding.root
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Get the location
            getLastLocation()
        }
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                // Convert coordinates to address
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                val addressList = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                if (addressList != null && addressList.isNotEmpty()) {
                    // Get the address from the addressList
                    val address = addressList[0].getAddressLine(0)
                    // Display the address in the EditText
                    binding.location.setText(address)
                } else {
                    Toast.makeText(requireContext(), "Unable to get address. Try again.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Unable to get location. Try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            // Open camera when permission is granted
            openCamera()
        }
    }

    private fun openCamera() {
        // Open camera to take a picture
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            data?.let {
                val imageBitmap = it.extras?.get("data") as? Bitmap
                if (imageBitmap != null) {
                    // Convert Bitmap to Uri (for Firestore)
                    val path = MediaStore.Images.Media.insertImage(requireActivity().contentResolver, imageBitmap, "CapturedImage", null)
                    imageUri = Uri.parse(path)
                    Toast.makeText(requireContext(), "Image captured successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Image capture failed!", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Toast.makeText(requireContext(), "Image capture failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, open camera
                    openCamera()
                } else {
                    // Permission denied
                    Toast.makeText(requireContext(), "Camera permission is required to take pictures.", Toast.LENGTH_SHORT).show()
                }
            }
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, get location
                    getLastLocation()
                } else {
                    // Permission denied
                    Toast.makeText(requireContext(), "Location permission is required to access your location.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun submitFinding() {
        // Get input from EditText fields
        val species = binding.speciesText.text.toString()
        val date = binding.date.text.toString()
        val time = binding.time.text.toString()
        val location = binding.location.text.toString()
        val description = binding.description.text.toString()

        // Create a BirdCapture object
        val birdCapture = BirdCapture(
            species = species,
            date = date,
            time = time,
            location = location,
            description = description,
            imageUrl = imageUri.toString()
        )

        // Save to Firestore
        firestore.collection("findings")
            .add(birdCapture)
            .addOnSuccessListener {
                // Handle success
                Toast.makeText(requireContext(), "Finding added", Toast.LENGTH_SHORT).show()
                // Clear the fields
                clearInputFields()
            }
            .addOnFailureListener { e ->
                // Handle failure
                Toast.makeText(requireContext(), "Error adding finding: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearInputFields() {
        binding.speciesText.text.clear()
        binding.date.text.clear()
        binding.time.text.clear()
        binding.location.text.clear()
        binding.description.text.clear()
    }

    private fun showDatePicker() {
        // Create a Calendar instance
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Show DatePickerDialog
        val datePickerDialog = DatePickerDialog(requireContext(), { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
            val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            binding.date.setText(formattedDate)
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun showTimePicker() {
        // Create a Calendar instance
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        // Show TimePickerDialog
        val timePickerDialog = TimePickerDialog(requireContext(), { _: TimePicker, selectedHour: Int, selectedMinute: Int ->
            val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            binding.time.setText(formattedTime)
        }, hour, minute, true)

        timePickerDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clean up binding
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1001
        private const val CAMERA_REQUEST_CODE = 1002
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1003
    }
}
