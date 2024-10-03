package com.rosebank.st10070002.chirpquest

//data class BirdCapture(
data class BirdCapture(
    var species: String? = null,
    var date: String? = null,
    var time: String? = null,
    var location: String? = null,
    var description: String? = null,
    var imageUrl: String? = null,
    var userId: String? = null
) {
    // No-argument constructor for Firestore deserialization
    constructor() : this(null, null, null, null, null, null, null)
}
