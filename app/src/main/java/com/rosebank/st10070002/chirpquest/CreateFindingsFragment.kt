package com.rosebank.st10070002.chirpquest

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore

class CreateFindingsFragment : Fragment() {

    private val CAMERA_PERMISSION_CODE = 102
    private val REQUEST_IMAGE_CAPTURE = 1

    private lateinit var firestore: FirebaseFirestore
    private lateinit var speciesInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var saveButton: Button
    private lateinit var favoriteButton: Button
    private var isFavorite = false
    private var imageUri: Uri? = null
    private lateinit var imageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_create_findings, container, false)

        firestore = FirebaseFirestore.getInstance()
        speciesInput = view.findViewById(R.id.speciesInput)
        descriptionInput = view.findViewById(R.id.descriptionInput)
        saveButton = view.findViewById(R.id.Save_FindingsButton)
        favoriteButton = view.findViewById(R.id.favoriteButton)
        imageView = view.findViewById(R.id.imageView) // Add an ImageView in your XML layout to show the image

        val cameraButton: ImageButton = view.findViewById(R.id.photoButton)
        cameraButton.setOnClickListener {
            checkAndRequestCameraPermission()
        }

        saveButton.setOnClickListener {
            saveFindings()
        }

        favoriteButton.setOnClickListener {
            toggleFavorite()
        }

        return view
    }

    private fun checkAndRequestCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }


    // Handling the image result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            val imageView: ImageView = view?.findViewById(R.id.imageView) ?: return
            imageView.setImageBitmap(imageBitmap)
        }
    }


    private fun saveFindings() {
        val species = speciesInput.text.toString().trim()
        val description = descriptionInput.text.toString().trim()

        if (species.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Please enter the species name",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val findingsMap = hashMapOf(
            "species" to species,
            "description" to description,
            "photoUri" to imageUri?.toString(),
            "isFavorite" to isFavorite
        )

        firestore.collection("findings")
            .add(findingsMap)
            .addOnSuccessListener {
                Toast.makeText(
                    requireContext(),
                    "Findings saved successfully",
                    Toast.LENGTH_SHORT
                ).show()
                clearInputs()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    requireContext(),
                    "Error saving findings: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun toggleFavorite() {
        isFavorite = !isFavorite
        favoriteButton.setBackgroundColor(if (isFavorite) 0xFFFF0000.toInt() else 0xFF999999.toInt()) // Change color based on favorite status
    }

    private fun clearInputs() {
        speciesInput.text.clear()
        descriptionInput.text.clear()
        imageUri = null
        isFavorite = false
        imageView.setImageBitmap(null) // Clear the ImageView
        favoriteButton.setBackgroundColor(0xFF999999.toInt()) // Reset color
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Camera permission is required to take pictures",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
