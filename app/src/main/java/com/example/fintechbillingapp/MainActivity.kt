package com.example.fintechbillingapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnParse = findViewById<Button>(R.id.btnParseBasic)
        val tvOutput = findViewById<TextView>(R.id.tvOutputBasic)
        val btnOpenInvoice = findViewById<Button>(R.id.btnOpenInvoice)

        btnParse.setOnClickListener {
            // Simulasi teks mentah JSON dari server
            val rawJsonString = """
                {
                    "merchant_id": "MCH-99201",
                    "merchant_name": "Mega Elektrindo Ritel",
                    "city_location": "Bandung",
                    "is_verified": true
                }
            """.trimIndent()

            // Proses Deserialisasi: String JSON -> Objek Kotlin Merchant
            val gson = Gson()
            val merchantObj: Merchant = gson.fromJson(rawJsonString, Merchant::class.java)

            // Menampilkan hasil ekstraksi properti objek Kotlin
            val result = """
                BERHASIL DI-PARSING:
                • ID Mitra : ${merchantObj.merchantId}
                • Nama Toko: ${merchantObj.merchantName}
                • Kota     : ${merchantObj.cityLocation}
                • Terverifikasi: ${if (merchantObj.isVerified) "RESMI (Ya)" else "Belum"}
            """.trimIndent()

            tvOutput.text = result
        }

        btnOpenInvoice.setOnClickListener {
            val intent = Intent(this, InvoiceActivity::class.java)
            startActivity(intent)
        }
    }
}