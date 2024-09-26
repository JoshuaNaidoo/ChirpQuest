package com.rosebank.st10070002.chirpquest

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface BirdHotspotService {
    @GET("v2/data/obs/geo/recent")
    fun getBirdHotspots(
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double,
        @Query("maxResults") maxResults: Int = 100,
        @Query("key") apiKey: String // Your API key
    ): Call<List<BirdHotspot>>
}