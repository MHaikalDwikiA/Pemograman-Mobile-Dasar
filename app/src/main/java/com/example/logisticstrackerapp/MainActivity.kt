package com.example.logisticstrackerapp

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var editTextTrackingNumber: EditText
    private lateinit var buttonTrack: Button
    
    private lateinit var editTextKeyword: EditText
    private lateinit var buttonSearch: Button

    private lateinit var progressBarLoading: ProgressBar
    private lateinit var cardResult: CardView
    
    private lateinit var cardError: CardView
    private lateinit var textViewErrorMessage: TextView
    
    private lateinit var textViewSearchResult: TextView

    private lateinit var textViewResiTitle: TextView
    private lateinit var textViewStatusBadge: TextView
    private lateinit var textViewCourier: TextView
    private lateinit var textViewLocation: TextView
    private lateinit var textViewRecipient: TextView
    private lateinit var textViewEta: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        editTextTrackingNumber = findViewById(R.id.editTextTrackingNumber)
        buttonTrack = findViewById(R.id.buttonTrack)
        
        editTextKeyword = findViewById(R.id.editTextKeyword)
        buttonSearch = findViewById(R.id.buttonSearch)
        
        progressBarLoading = findViewById(R.id.progressBarLoading)
        cardResult = findViewById(R.id.cardResult)
        
        cardError = findViewById(R.id.cardError)
        textViewErrorMessage = findViewById(R.id.textViewErrorMessage)
        
        textViewSearchResult = findViewById(R.id.textViewSearchResult)

        textViewResiTitle = findViewById(R.id.textViewResiTitle)
        textViewStatusBadge = findViewById(R.id.textViewStatusBadge)
        textViewCourier = findViewById(R.id.textViewCourier)
        textViewLocation = findViewById(R.id.textViewLocation)
        textViewRecipient = findViewById(R.id.textViewRecipient)
        textViewEta = findViewById(R.id.textViewEta)

        buttonTrack.setOnClickListener {
            hideKeyboard()
            val nomorResi = editTextTrackingNumber.text.toString().trim()
            if (nomorResi.isNotEmpty()) {
                if (isNetworkAvailable()) {
                    lacakPaket(nomorResi)
                } else {
                    tampilkanPesanOffline(it)
                }
            } else {
                Toast.makeText(this, "Silakan ketik nomor resi terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }

        buttonSearch.setOnClickListener {
            hideKeyboard()
            val keyword = editTextKeyword.text.toString().trim()
            if (keyword.isNotEmpty()) {
                if (isNetworkAvailable()) {
                    cariPaket(keyword)
                } else {
                    tampilkanPesanOffline(it)
                }
            } else {
                Toast.makeText(this, "Silakan ketik kata kunci pencarian", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    private fun tampilkanPesanOffline(view: View) {
        val snackbar = Snackbar.make(view, "Perangkat Offline - Periksa Jaringan Anda", Snackbar.LENGTH_LONG)
        snackbar.setBackgroundTint(Color.parseColor("#DC2626"))
        snackbar.setTextColor(Color.WHITE)
        snackbar.show()
    }

    private fun lacakPaket(nomorResi: String) {
        setLoadingState()

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = ApiClient.apiService.getTrackingDetail(nomorResi)
                delay(400)

                withContext(Dispatchers.Main) {
                    resetLoadingState()
                    if (response.isSuccessful && response.body() != null) {
                        textViewSearchResult.visibility = View.GONE
                        val dataPaket = response.body()!!
                        tampilkanDataPaket(dataPaket)
                    } else {
                        tampilkanError("Nomor resi [$nomorResi] tidak ditemukan pada sistem.")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    resetLoadingState()
                    tampilkanError("Koneksi internet bermasalah: ${e.localizedMessage}")
                }
            }
        }
    }

    private fun cariPaket(keyword: String) {
        setLoadingState()

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = ApiClient.apiService.searchTracking(keyword)
                delay(400)

                withContext(Dispatchers.Main) {
                    resetLoadingState()
                    if (response.isSuccessful && response.body() != null) {
                        val results = response.body()!!
                        
                        textViewSearchResult.visibility = View.VISIBLE
                        textViewSearchResult.text = "Ditemukan ${results.size} paket dengan kata kunci '$keyword'"
                        
                        if (results.isNotEmpty()) {
                            tampilkanDataPaket(results[0])
                        } else {
                            cardResult.visibility = View.GONE
                            tampilkanError("Tidak ada paket yang cocok dengan kata kunci '$keyword'")
                        }
                    } else {
                        tampilkanError("Pencarian gagal atau tidak ditemukan data.")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    resetLoadingState()
                    tampilkanError("Koneksi internet bermasalah: ${e.localizedMessage}")
                }
            }
        }
    }

    private fun setLoadingState() {
        progressBarLoading.visibility = View.VISIBLE
        cardResult.visibility = View.GONE
        cardError.visibility = View.GONE
        textViewSearchResult.visibility = View.GONE
        buttonTrack.isEnabled = false
        buttonSearch.isEnabled = false
    }

    private fun resetLoadingState() {
        progressBarLoading.visibility = View.GONE
        buttonTrack.isEnabled = true
        buttonSearch.isEnabled = true
    }

    private fun tampilkanError(pesan: String) {
        cardResult.visibility = View.GONE
        textViewErrorMessage.text = pesan
        cardError.visibility = View.VISIBLE
    }

    private fun tampilkanDataPaket(dataPaket: TrackingResponse) {
        cardResult.visibility = View.VISIBLE
        textViewResiTitle.text = dataPaket.trackingNumber
        textViewCourier.text = "${dataPaket.serviceType} (${dataPaket.courierName})"
        textViewLocation.text = dataPaket.lastLocation
        textViewRecipient.text = dataPaket.recipientName
        textViewEta.text = dataPaket.estimatedDelivery

        textViewStatusBadge.text = dataPaket.status
        
        val bgShape = textViewStatusBadge.background as? GradientDrawable
        
        when (dataPaket.status.uppercase()) {
            "DELIVERED" -> {
                bgShape?.setColor(Color.parseColor("#DCFCE7"))
                textViewStatusBadge.setTextColor(Color.parseColor("#15803D"))
            }
            "IN_TRANSIT" -> {
                bgShape?.setColor(Color.parseColor("#FEF3C7"))
                textViewStatusBadge.setTextColor(Color.parseColor("#B45309"))
            }
            else -> {
                bgShape?.setColor(Color.parseColor("#F1F5F9"))
                textViewStatusBadge.setTextColor(Color.parseColor("#475569"))
            }
        }
    }
}