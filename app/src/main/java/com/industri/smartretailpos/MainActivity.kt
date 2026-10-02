package com.industri.smartretailpos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tilNominal = findViewById<TextInputLayout>(R.id.tilNominal)
        val etNominal = findViewById<TextInputEditText>(R.id.etNominal)
        val tilQty = findViewById<TextInputLayout>(R.id.tilQty)
        val etQty = findViewById<TextInputEditText>(R.id.etQty)
        
        val btnHitung = findViewById<Button>(R.id.btnHitung)
        val tvHasil = findViewById<TextView>(R.id.tvHasil)
        val btnBukaPos = findViewById<Button>(R.id.btnBukaPos)

        btnHitung.setOnClickListener {
            // Hide keyboard for better UX
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
            
            // Clear previous errors
            tilNominal.error = null
            tilQty.error = null

            val nominalStr = etNominal.text.toString().trim()
            val qtyStr = etQty.text.toString().trim()

            if (nominalStr.isEmpty()) {
                tilNominal.error = getString(R.string.error_nominal_empty)
                return@setOnClickListener
            }
            if (qtyStr.isEmpty()) {
                tilQty.error = getString(R.string.error_qty_empty)
                return@setOnClickListener
            }

            Timber.d("Menghitung transaksi kasir: Nominal=%s, Qty=%s", nominalStr, qtyStr)

            // Menggunakan idiom fungsional runCatching untuk menangkal crash input kosong
            val kalkulasiResult = runCatching {
                val nominal = nominalStr.toDouble()
                val qty = qtyStr.toInt()
                if (qty <= 0) throw IllegalArgumentException("Kuantitas barang minimal 1")
                
                val total = nominal * qty
                // Tugas 2: Memeriksa limit otorisasi
                if (total > 10000000.0) {
                    throw CashierLimitExceededException(10000000.0)
                }
                total
            }

            kalkulasiResult.onSuccess { total ->
                Timber.i("Kalkulasi sukses: Total bayar Rp %,.2f", total)
                tvHasil.text = getString(R.string.total_transaksi, total)
            }.onFailure { error ->
                Timber.e(error, "Terjadi kesalahan kalkulasi input kasir")
                if (error is CashierLimitExceededException) {
                    tvHasil.text = error.message
                    // Tampilkan dialog PIN Supervisor kasir (Simulasi)
                    androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle(R.string.otorisasi_title)
                        .setMessage(getString(R.string.otorisasi_msg, error.message))
                        .setPositiveButton(R.string.ok, null)
                        .show()
                } else {
                    tvHasil.text = getString(R.string.error_input_msg, error.message ?: "Input tidak valid")
                }
            }
        }

        btnBukaPos.setOnClickListener {
            startActivity(Intent(this, PosActivity::class.java))
        }
    }
}
