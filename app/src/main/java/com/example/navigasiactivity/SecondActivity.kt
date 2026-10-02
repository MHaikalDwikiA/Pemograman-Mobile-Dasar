package com.example.navigasiactivity

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val txtNama = findViewById<TextView>(R.id.txtNama)
        val btnKembali = findViewById<Button>(R.id.btnKembali)

        // Praktikum 8: Menerima seluruh data
        val nama = intent.getStringExtra("nama")
        val umur = intent.getIntExtra("umur", 0)
        val alamat = intent.getStringExtra("alamat")

        txtNama.text = "Selamat Datang \n$nama\nUmur: $umur\nAlamat: $alamat"

        btnKembali.setOnClickListener {
            finish()
        }
    }
}