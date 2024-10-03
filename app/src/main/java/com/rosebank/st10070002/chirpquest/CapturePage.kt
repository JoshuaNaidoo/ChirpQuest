package com.rosebank.st10070002.chirpquest

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
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import java.util.Locale

class CapturePage : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    private var imageUri: Uri? = null // To store the image URI
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture_page)

        firestore = FirebaseFirestore.getInstance()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Setup button click listener
        findViewById<Button>(R.id.submitButton).setOnClickListener {
            submitFinding()
        }

        // Setup image button click listener
        findViewById<ImageButton>(R.id.addpictureButton).setOnClickListener {
            requestCameraPermission()
        }

        // Setup location button click listener
        findViewById<EditText>(R.id.location).setOnClickListener {
            requestLocationPermission()
        }

        // Setup date picker for the date EditText
        findViewById<EditText>(R.id.date).setOnClickListener {
            showDatePicker()
        }

        // Setup time picker for the time EditText
        findViewById<EditText>(R.id.time).setOnClickListener {
            showTimePicker()
        }
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            // Get the location
            getLastLocation()
        }
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                // Convert coordinates to address
                val geocoder = Geocoder(this, Locale.getDefault())
                val addressList = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                if (addressList != null && addressList.isNotEmpty()) {
                    // Get the address from the addressList
                    val address = addressList[0].getAddressLine(0)
                    // Display the address in the EditText
                    val locationEditText = findViewById<EditText>(R.id.location)
                    locationEditText.setText(address)
                } else {
                    Toast.makeText(this, "Unable to get address. Try again.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Unable to get location. Try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE)
        } else {
            //Open camera when permission is granted
            openCamera()
        }
    }

    private fun openCamera() {
        // Open camera to take a picture
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            // Check if data is not null
            data?.let {
                val imageBitmap = it.extras?.get("data") as? Bitmap
                if (imageBitmap != null) {
                    // Convert Bitmap to Uri (for Firestore)
                    val path = MediaStore.Images.Media.insertImage(contentResolver, imageBitmap, "CapturedImage", null)
                    imageUri = Uri.parse(path)

                    // Show success message
                    Toast.makeText(this, "Image captured successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Image capture failed!", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Toast.makeText(this, "Image capture failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Handle permission result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, open camera
                    openCamera()
                } else {
                    // Permission denied
                    Toast.makeText(this, "Camera permission is required to take pictures.", Toast.LENGTH_SHORT).show()
                }
            }
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, get location
                    getLastLocation()
                } else {
                    // Permission denied
                    Toast.makeText(this, "Location permission is required to access your location.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun submitFinding() {
        // Get input from EditText fields
        val species = findViewById<EditText>(R.id.speciesText).text.toString()
        val date = findViewById<EditText>(R.id.date).text.toString()
        val time = findViewById<EditText>(R.id.time).text.toString()
        val location = findViewById<EditText>(R.id.location).text.toString()
        val description = findViewById<EditText>(R.id.description).text.toString()

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
                Toast.makeText(this, "Finding added", Toast.LENGTH_SHORT).show()
                //Clear the fields
                clearInputFields()
            }
            .addOnFailureListener { e ->
                // Handle failure
                Toast.makeText(this, "Error adding finding: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearInputFields() {
        findViewById<EditText>(R.id.speciesText).text.clear()
        findViewById<EditText>(R.id.date).text.clear()
        findViewById<EditText>(R.id.time).text.clear()
        findViewById<EditText>(R.id.location).text.clear()
        findViewById<EditText>(R.id.description).text.clear()
    }

    private fun showDatePicker() {
        // Create a Calendar instance
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Show DatePickerDialog
        val datePickerDialog = DatePickerDialog(this, { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
            val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            findViewById<EditText>(R.id.date).setText(formattedDate)
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun showTimePicker() {
        // Create a Calendar instance
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        // Show TimePickerDialog
        val timePickerDialog = TimePickerDialog(this, { _: TimePicker, selectedHour: Int, selectedMinute: Int ->
            val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            findViewById<EditText>(R.id.time).setText(formattedTime)
        }, hour, minute, true)

        timePickerDialog.show()
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1001
        private const val CAMERA_REQUEST_CODE = 1002
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1003
    }
}
