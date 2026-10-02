package com.industri.smartretailpos

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class PosActivity : AppCompatActivity() {

    private lateinit var tilBarcode: TextInputLayout
    private lateinit var etBarcode: TextInputEditText
    private lateinit var btnProcessPayment: Button
    private lateinit var pbPosLoading: ProgressBar
    private lateinit var cardPosResult: CardView
    private lateinit var tvPosStatus: TextView
    private lateinit var tvPosDetails: TextView
    private lateinit var rgSimulation: RadioGroup
    
    // Variabel untuk Tugas 1
    private var retryAttempt = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pos)

        tilBarcode = findViewById(R.id.tilBarcode)
        etBarcode = findViewById(R.id.etBarcode)
        btnProcessPayment = findViewById(R.id.btnProcessPayment)
        pbPosLoading = findViewById(R.id.pbPosLoading)
        cardPosResult = findViewById(R.id.cardPosResult)
        tvPosStatus = findViewById(R.id.tvPosStatus)
        tvPosDetails = findViewById(R.id.tvPosDetails)
        rgSimulation = findViewById(R.id.rgSimulation)

        btnProcessPayment.setOnClickListener {
            // Hide keyboard for better UX
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
            
            tilBarcode.error = null

            val barcode = etBarcode.text.toString().trim()
            if (barcode.isNotEmpty()) {
                // Reset retry saat menekan tombol utama
                retryAttempt = 0
                eksekusiTransaksi(barcode)
            } else {
                tilBarcode.error = getString(R.string.error_barcode_empty)
            }
        }
    }

    private fun eksekusiTransaksi(barcode: String) {
        pbPosLoading.visibility = View.VISIBLE
        cardPosResult.visibility = View.GONE
        btnProcessPayment.isEnabled = false

        // Coroutine Exception Handler: Pengaman terakhir agar aplikasi tidak force-close
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
            Timber.e(exception, "FATAL COROUTINE ERROR TERTANGKAP: %s", exception.message)
            runOnUiThread {
                pbPosLoading.visibility = View.GONE
                btnProcessPayment.isEnabled = true
                tampilkanSnackbarError(getString(R.string.error_system, exception.localizedMessage), canRetry = false)
            }
        }

        lifecycleScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            Timber.d("Memulai alur pembayaran untuk Barcode: %s", barcode)

            // Menggunakan idiom runCatching untuk menangani logika transaksi perbankan
            val hasil = runCatching {
                delay(1200) // Simulasi latensi jaringan

                // Simulasi kondisi berdasarkan pilihan RadioButton
                when (rgSimulation.checkedRadioButtonId) {
                    R.id.rbBarcodeError -> {
                        throw ProductBarcodeNotFoundException(barcode)
                    }
                    R.id.rbNetworkTimeout -> {
                        throw PaymentGatewayTimeoutException("QRIS Bank Settlement")
                    }
                    else -> {
                        PosTransaction(
                            transactionId = "TRX-2026-9901",
                            barcode = barcode,
                            productName = "Susu UHT Full Cream 1 Liter",
                            totalAmount = 21500.0,
                            status = "SETTLED_SUCCESS"
                        )
                    }
                }
            }

            withContext(Dispatchers.Main) {
                pbPosLoading.visibility = View.GONE
                btnProcessPayment.isEnabled = true

                hasil.onSuccess { trx ->
                    Timber.i("Transaksi kasir berhasil dicatat: %s", trx.transactionId)
                    cardPosResult.visibility = View.VISIBLE
                    tvPosStatus.text = getString(R.string.trx_success)
                    
                    val detailFormatted = getString(
                        R.string.trx_details_template,
                        trx.transactionId,
                        trx.productName,
                        trx.totalAmount
                    )
                    tvPosDetails.text = detailFormatted.trim()
                    
                    // Reset retry jika berhasil
                    retryAttempt = 0
                }.onFailure { err ->
                    Timber.w("Transaksi kasir ditolak / gagal: %s", err.message)
                    when (err) {
                        is ProductBarcodeNotFoundException -> {
                            tampilkanSnackbarError(err.message ?: getString(R.string.error_barcode_not_found), canRetry = false)
                        }
                        is PaymentGatewayTimeoutException -> {
                            // Error Jaringan: Berikan tombol aksi RETRY pada Snackbar
                            tampilkanSnackbarError(err.message ?: getString(R.string.error_gateway_timeout), canRetry = true)
                        }
                        else -> {
                            tampilkanSnackbarError(getString(R.string.error_operational, err.message), canRetry = true)
                        }
                    }
                }
            }
        }
    }

    private fun tampilkanSnackbarError(pesan: String, canRetry: Boolean) {
        val root = findViewById<View>(R.id.coordinatorLayout)
        val snackbar = Snackbar.make(root, pesan, Snackbar.LENGTH_LONG)
        
        // Menggunakan warna dari resources (colors.xml) untuk M3 feel
        snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.pos_error_red))
        snackbar.setTextColor(ContextCompat.getColor(this, R.color.white))

        if (canRetry) {
            snackbar.setAction(R.string.action_coba_lagi) {
                retryAttempt++
                if (retryAttempt >= 3) {
                    // Tampilkan AlertDialog untuk Tugas 1
                    tampilkanDialogTunaiManual()
                } else {
                    val barcode = etBarcode.text.toString().trim()
                    if (barcode.isNotEmpty()) eksekusiTransaksi(barcode)
                }
            }
            snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.pos_warning_yellow))
        }
        snackbar.show()
    }
    
    // Fungsi untuk Tugas 1
    private fun tampilkanDialogTunaiManual() {
        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_network_title)
            .setMessage(R.string.dialog_network_msg)
            .setPositiveButton(R.string.mengerti) { dialog, _ ->
                retryAttempt = 0 // Reset setelah dialog ditutup
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }
}
