package com.example.logisticstrackerapp

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private const val BASE_URL = "https://6abf6c2b06bcd2f20672af3b.mockapi.io/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val mockDataList = listOf(
        """
            {
                "id": "EXP-8801",
                "tracking_number": "EXP-8801",
                "courier_name": "Budi Santoso",
                "service_type": "JNE Regular",
                "status": "IN_TRANSIT",
                "last_location": "Hub Sortir Jakarta Barat",
                "estimated_delivery": "Besok, 16:00 WIB",
                "recipient_name": "PT Sumber Makmur (Surabaya)"
            }
        """.trimIndent(),
        """
            {
                "id": "EXP-8802",
                "tracking_number": "EXP-8802",
                "courier_name": "Siti Aminah",
                "service_type": "SiCepat Best",
                "status": "DELIVERED",
                "last_location": "Diterima oleh Satpam Gedung",
                "estimated_delivery": "Hari Ini, 10:30 WIB",
                "recipient_name": "Kantor Cabang Bandung"
            }
        """.trimIndent(),
        """
            {
                "id": "EXP-8803",
                "tracking_number": "EXP-8803",
                "courier_name": "Rian Hidayat",
                "service_type": "J&T Express",
                "status": "IN_TRANSIT",
                "last_location": "Gateway Cirebon",
                "estimated_delivery": "Lusa, 14:00 WIB",
                "recipient_name": "Dewi Sartika (Kuningan)"
            }
        """.trimIndent(),
        """
            {
                "id": "EXP-8804",
                "tracking_number": "EXP-8804",
                "courier_name": "Ahmad Fauzi",
                "service_type": "Anteraja Reg",
                "status": "DELIVERED",
                "last_location": "Diterima Ybs (Bapak Hendra)",
                "estimated_delivery": "Kemarin, 11:15 WIB",
                "recipient_name": "Hendra Wijaya (Jakarta)"
            }
        """.trimIndent(),
        """
            {
                "id": "EXP-8805",
                "tracking_number": "EXP-8805",
                "courier_name": "Doni Prasetyo",
                "service_type": "Pos Indonesia Kilat Khusus",
                "status": "IN_TRANSIT",
                "last_location": "Kantor Pos Pusat Cirebon",
                "estimated_delivery": "2 Hari Lagi, 17:00 WIB",
                "recipient_name": "Toko Berkah Mandiri (Majalengka)"
            }
        """.trimIndent()
    )

    private fun getSimulatedTrackingData(nomorResi: String): String? {
        return mockDataList.find { it.contains("\"tracking_number\": \"$nomorResi\"") }
    }

    private fun getSimulatedSearchData(keyword: String): String {
        val lowerKeyword = keyword.lowercase()
        val results = mockDataList.filter { it.lowercase().contains(lowerKeyword) }
        return "[\n" + results.joinToString(",\n") + "\n]"
    }

    private val smartFallbackInterceptor = Interceptor { chain ->
        val request = chain.request()
        var response: Response? = null
        val urlString = request.url.toString()

        try {
            response = chain.proceed(request)
        } catch (_: Exception) {}

        if (response != null && response.isSuccessful) {
            return@Interceptor response
        }

        if (urlString.contains("/api/v1/tracking/search")) {
            val queryParam = request.url.queryParameter("query") ?: ""
            val mockSearchJson = getSimulatedSearchData(queryParam)
            
            response?.close()
            return@Interceptor Response.Builder()
                .code(200)
                .message("OK (Simulated Search Fallback)")
                .protocol(Protocol.HTTP_1_1)
                .request(request)
                .body(mockSearchJson.toResponseBody("application/json; charset=utf-8".toMediaTypeOrNull()))
                .addHeader("content-type", "application/json")
                .build()
        }

        if ((response == null || response.code == 404) && urlString.contains("/api/v1/")) {
            val fallbackUrl = urlString.replace("/api/v1/", "/")
            val fallbackRequest = request.newBuilder().url(fallbackUrl).build()
            try {
                response?.close()
                val retryResponse = chain.proceed(fallbackRequest)
                if (retryResponse.isSuccessful) {
                    return@Interceptor retryResponse
                }
                response = retryResponse
            } catch (_: Exception) {}
        }

        val resiInput = request.url.pathSegments.lastOrNull()?.trim()?.uppercase() ?: ""
        val mockJsonData = getSimulatedTrackingData(resiInput)

        if (mockJsonData != null) {
            response?.close()
            return@Interceptor Response.Builder()
                .code(200)
                .message("OK (Simulated Fallback)")
                .protocol(Protocol.HTTP_1_1)
                .request(request)
                .body(mockJsonData.toResponseBody("application/json; charset=utf-8".toMediaTypeOrNull()))
                .addHeader("content-type", "application/json")
                .build()
        }

        return@Interceptor response ?: chain.proceed(request)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(smartFallbackInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    val apiService: LogisticsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LogisticsApiService::class.java)
    }
}