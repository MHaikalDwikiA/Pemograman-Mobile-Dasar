package com.example.kurirdirectoryapp

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
object ApiClient {
    // URL Dasar (BASE_URL) Wajib Diakhiri Tanda Garis Miring '/'
    private const val BASE_URL = "https://dummyjson.com/"
    // Inisialisasi Service Retrofit
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}