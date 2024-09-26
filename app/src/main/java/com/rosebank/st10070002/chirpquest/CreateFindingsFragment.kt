package com.rosebank.st10070002.chirpquest

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class CreateFindingsFragment : Fragment() {

    private val CAMERA_REQUEST_CODE = 101
    private val CAMERA_PERMISSION_CODE = 102
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var speciesInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var saveButton: Button
    private lateinit var favoriteButton: MaterialButton
    private lateinit var photoImageView: ImageView
    private var isFavorite = false
    private var photoUri: Uri? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_findings, container, false)

        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        speciesInput = view.findViewById(R.id.speciesInput)
        descriptionInput = view.findViewById(R.id.descriptionInput)
        saveButton = view.findViewById(R.id.Save_FindingsButton)
        favoriteButton = view.findViewById(R.id.favoriteButton)
        photoImageView = view.findViewById(R.id.photoImageView)

        val cameraButton: AppCompatImageButton = view.findViewById(R.id.photoButton)
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
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                CAMERA_PERMISSION_CODE
            )
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        val photoFile = createImageFile()
        photoUri = Uri.fromFile(photoFile)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
    }

    private fun createImageFile(): File {
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${System.currentTimeMillis()}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            // Optionally, display the image in the app if desired
            photoUri?.let {
                val bitmap = BitmapFactory.decodeFile(it.path)
                photoImageView.setImageBitmap(bitmap)
            }
        }
    }

    private fun saveFindings() {
        val species = speciesInput.text.toString().trim()
        val description = descriptionInput.text.toString().trim()

        if (species.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter the species name", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if photoUri is not null
        if (photoUri == null) {
            Toast.makeText(requireContext(), "Please take a photo", Toast.LENGTH_SHORT).show()
            return
        }

        val storageRef = storage.reference
        val imageRef = storageRef.child("images/${photoUri!!.lastPathSegment}")

        imageRef.putFile(photoUri!!)
            .addOnSuccessListener {
                // Get the download URL
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val findingsMap = hashMapOf(
                        "species" to species,
                        "description" to description,
                        "photoUri" to downloadUri.toString(),
                        "isFavorite" to isFavorite
                    )

                    firestore.collection("findings")
                        .add(findingsMap)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Findings saved successfully", Toast.LENGTH_SHORT).show()
                            clearInputs()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Error saving findings: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error uploading image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun toggleFavorite() {
        isFavorite = !isFavorite
        favoriteButton.setBackgroundColor(if (isFavorite) 0xFFFF0000.toInt() else 0xFF999999.toInt())
    }

    private fun clearInputs() {
        speciesInput.text.clear()
        descriptionInput.text.clear()
        photoImageView.setImageResource(0) // Clear the image view
        photoUri = null
        isFavorite = false
        favoriteButton.setBackgroundColor(0xFF999999.toInt())
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
                Toast.makeText(requireContext(), "Camera permission is required to take pictures", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
