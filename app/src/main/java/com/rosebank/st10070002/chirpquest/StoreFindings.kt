package com.rosebank.st10070002.chirpquest


class StoreFindings {

    // Data class to store findings with image URL
    data class Findings(
        val species: String,
        val description: String,
        val date: String,
        val time: String,
        val location: String,
        val imageUrl: String // Field for storing image URL
    )

    // You can add other methods and logic here for storing findings
}
