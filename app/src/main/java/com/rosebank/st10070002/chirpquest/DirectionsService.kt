package com.rosebank.st10070002.chirpquest

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface DirectionsService {
    @GET
    fun getDirections(@Url url: String): Call<DirectionsResponse>
}

