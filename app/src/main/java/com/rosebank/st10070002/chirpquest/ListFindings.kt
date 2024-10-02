package com.rosebank.st10070002.chirpquest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ListFindingsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var findingsAdapter: FindingsAdapter
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_findings_listing, container, false)

        firestore = FirebaseFirestore.getInstance()
        recyclerView = view.findViewById(R.id.findingsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        loadFindings()

        return view
    }

    private fun loadFindings() {
        firestore.collection("findings")
            .get()
            .addOnSuccessListener { documents ->
                val findingsList = mutableListOf<Finding>()
                for (document in documents) {
                    val species = document.getString("species") ?: ""
                    val description = document.getString("description") ?: ""
                    val photoUri = document.getString("photoUri") ?: ""
                    val isFavorite = document.getBoolean("isFavorite") ?: false
                    findingsList.add(Finding(species, description, photoUri, isFavorite))
                }
                findingsAdapter = FindingsAdapter(findingsList)
                recyclerView.adapter = findingsAdapter
            }
            .addOnFailureListener { e ->
                // Handle the error
            }
    }
}
