package com.rosebank.st10070002.chirpquest

data class BirdCapture(
    val id: String = "",
    val species: String,
    val date: String,
    val time: String,
    val location: String,
    val description: String? = null,
    var imageUrl: String? = null
)