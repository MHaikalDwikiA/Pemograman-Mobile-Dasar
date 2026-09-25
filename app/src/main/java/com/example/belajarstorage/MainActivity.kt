package com.example.belajarstorage

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // 1. Deklarasi nama file SharedPreferences dan Key
    private val PREF_NAME = "MyUserPrefs"
    private val KEY_NAMA = "KEY_NAMA_USER"
    private val KEY_REMEMBER = "KEY_REMEMBER_ME" // Key Boolean untuk Tugas Mandiri 1
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 2. Inisialisasi SharedPreferences dengan mode privat
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        // Hubungkan widget antarmuka
        val etNama = findViewById<EditText>(R.id.etNama)
        val cbRemember = findViewById<CheckBox>(R.id.cbRemember)
        val btnSimpan = findViewById<Button>(R.id.btnSimpan)
        val btnMuat = findViewById<Button>(R.id.btnMuat)
        val btnHapus = findViewById<Button>(R.id.btnHapus)
        val tvHasil = findViewById<TextView>(R.id.tvHasil)

        // 3. Operasi Menulis Data (Write) saat tombol Simpan ditekan
        btnSimpan.setOnClickListener {
            val inputNama = etNama.text.toString().trim()
            if (inputNama.isNotEmpty()) {
                val editor = sharedPreferences.edit()
                editor.putString(KEY_NAMA, inputNama)
                editor.putBoolean(KEY_REMEMBER, cbRemember.isChecked)
                editor.apply() // Menyimpan secara asynchronous (aman di background)

                Toast.makeText(this, "Data berhasil disimpan!", Toast.LENGTH_SHORT).show()
                etNama.text.clear()
            } else {
                Toast.makeText(this, "Silakan isi nama terlebih dahulu!", Toast.LENGTH_SHORT).show()
            }
        }

        // 4. Operasi Membaca Data (Read) saat tombol Muat ditekan
        btnMuat.setOnClickListener {
            val dataTersimpan = sharedPreferences.getString(KEY_NAMA, "Data tidak ditemukan")
            val statusRemember = sharedPreferences.getBoolean(KEY_REMEMBER, false)

            tvHasil.text = if (statusRemember) {
                "$dataTersimpan\n[Ingat Saya Aktif]"
            } else {
                dataTersimpan
            }
        }

        // 5. Operasi Menghapus Data (Delete) saat tombol Hapus ditekan
        btnHapus.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.remove(KEY_NAMA)
            editor.remove(KEY_REMEMBER)
            editor.apply()

            cbRemember.isChecked = false
            tvHasil.text = "[Data telah dihapus]"
            Toast.makeText(this, "Data berhasil dihapus!", Toast.LENGTH_SHORT).show()
        }

        // 6. Muat data otomatis saat pertama kali aplikasi dibuka
        val namaAwal = sharedPreferences.getString(KEY_NAMA, null)
        val isRemember = sharedPreferences.getBoolean(KEY_REMEMBER, false)
        if (namaAwal != null) {
            val statusText = if (isRemember) "\n[Ingat Saya Aktif]" else ""
            tvHasil.text = "Selamat datang kembali: $namaAwal$statusText"
            cbRemember.isChecked = isRemember
        }
    }
}