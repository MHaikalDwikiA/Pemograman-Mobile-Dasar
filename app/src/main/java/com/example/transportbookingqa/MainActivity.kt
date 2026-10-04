package com.example.transportbookingqa

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tilNik = findViewById<TextInputLayout>(R.id.tilNik)
        val etNik = findViewById<EditText>(R.id.etNik)
        val etName = findViewById<EditText>(R.id.etName)
        val btnSubmit = findViewById<Button>(R.id.btnSubmitBooking)
        val cvStatus = findViewById<MaterialCardView>(R.id.cvStatus)

        btnSubmit.setOnClickListener {
            // Sembunyikan keyboard saat tombol ditekan
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)

            val nik = etNik.text.toString().trim()
            val name = etName.text.toString().trim()

            if (nik.isEmpty()) {
                tilNik.error = "NIK tidak boleh kosong"
                tilNik.isErrorEnabled = true
                cvStatus.visibility = View.GONE
                return@setOnClickListener
            }

            // Bersihkan error jika input sudah valid
            tilNik.error = null
            tilNik.isErrorEnabled = false

            cvStatus.visibility = View.VISIBLE
        }
    }
}