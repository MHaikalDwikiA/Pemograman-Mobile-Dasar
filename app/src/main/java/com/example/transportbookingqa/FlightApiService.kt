package com.example.transportbookingqa

import retrofit2.http.GET

interface FlightApiService {
    @GET("api/v1/flights/availabilities")
    suspend fun checkAvailability(): List<String>
}
