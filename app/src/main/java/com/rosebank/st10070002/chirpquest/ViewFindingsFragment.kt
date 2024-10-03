package com.rosebank.st10070002.chirpquest

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ViewFindingsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var findingsAdapter: FindingsAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth // Firebase Authentication instance


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_view_findings, container, false)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance() // Initialize Firebase Auth
        recyclerView = view.findViewById(R.id.findingsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize the adapter with an empty list and set it to the RecyclerView
        findingsAdapter = FindingsAdapter(mutableListOf())
        recyclerView.adapter = findingsAdapter

        // Fetch findings from Firestore
        fetchFindings()

        return view
    }

private fun fetchFindings() {
    val userId = auth.currentUser?.uid // Get the current user's ID
    if (userId == null) {
        // Handle the case where the user is not logged in
        Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
        return
    }

    firestore.collection("findings")
        .whereEqualTo("userId", userId) // Query for findings by the current user
        .get()
        .addOnSuccessListener { querySnapshot ->
            val findingsList = querySnapshot.toObjects(BirdCapture::class.java)
            if (findingsList.isNotEmpty()) {
                findingsAdapter.updateFindings(findingsList)
            } else {
                // Handle empty data if needed
            }
        }
        .addOnFailureListener { e ->
            // Handle failure
            Toast.makeText(requireContext(), "Error fetching findings: ${e.message}", Toast.LENGTH_SHORT).show()
        }
}



}
