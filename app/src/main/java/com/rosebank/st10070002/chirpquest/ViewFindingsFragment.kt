package com.rosebank.st10070002.chirpquest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ViewFindingsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var findingsAdapter: FindingsAdapter
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_view_findings, container, false)

        firestore = FirebaseFirestore.getInstance()
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
        firestore.collection("findings")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val findingsList = querySnapshot.toObjects(BirdCapture::class.java)
                if (findingsList.isNotEmpty()) {
                    findingsAdapter.updateFindings(findingsList)
                } else {
                    // Handle empty data, if needed
                    // Example: Show a message or placeholder
                }
            }
            .addOnFailureListener { e ->
                // Handle failure (e.g., show a Toast or log the error)
                // Log.e("ViewFindingsFragment", "Error fetching findings", e)
            }
    }
}
