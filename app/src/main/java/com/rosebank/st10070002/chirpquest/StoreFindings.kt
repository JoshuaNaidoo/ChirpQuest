package com.rosebank.st10070002.chirpquest


class StoreFindings {

    // Data class to store findings with image URL
    data class BirdCapture(
        val id: String,
        val species: String,
        val date: String,
        val time: String,
        val latitude: String?,  // Ensure latitude is included
        val longitude: String?, // Ensure longitude is included
        val description: String,
        var imageUrl: String = "" // Default value for imageUrl
    )


    // You can add other methods and logic here for storing findings
}
