package com.rosebank.st10070002.chirpquest

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class FlockFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var flockAdapter: FlockAdapter
    private lateinit var firestore: FirebaseFirestore

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_flock, container, false)

        firestore = FirebaseFirestore.getInstance()
        recyclerView = view.findViewById(R.id.flockRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize the adapter with an empty list and set it to the RecyclerView
        flockAdapter = FlockAdapter(mutableListOf())
        recyclerView.adapter = flockAdapter

        // Fetch all bird posts from Firestore
        fetchFlockData()

        return view
    }

    private fun fetchFlockData() {
        firestore.collection("findings") // Fetch all findings from Firestore
            .get()
            .addOnSuccessListener { querySnapshot ->
                val flockList = querySnapshot.toObjects(BirdCapture::class.java)
                if (flockList.isNotEmpty()) {
                    flockAdapter.updateFlock(flockList)
                } else {
                    // Handle empty data if needed
                    Toast.makeText(requireContext(), "No birds found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error fetching flock data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
