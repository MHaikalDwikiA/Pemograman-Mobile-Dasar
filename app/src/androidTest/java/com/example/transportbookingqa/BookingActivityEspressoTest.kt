package com.example.transportbookingqa

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BookingActivityEspressoTest {

    // Luncurkan MainActivity secara otomatis sebelum test dijalankan
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun isiFormPemesanan_danTekanPesan_harusMenampilkanKonfirmasiTiket() {
        // 1. Ketik NIK Penumpang pada EditText
        onView(withId(R.id.etNik))
            .perform(typeText("3209123456780001"), closeSoftKeyboard())

        // 2. Ketik Nama Penumpang
        onView(withId(R.id.etName))
            .perform(typeText("Ahmad Dahlan"), closeSoftKeyboard())

        // 3. Tekan Tombol Pesan Tiket
        onView(withId(R.id.btnSubmitBooking))
            .perform(click())

        // 4. Verifikasi (Assert) bahwa status konfirmasi sukses muncul di layar
        onView(withId(R.id.tvBookingStatus))
            .check(matches(isDisplayed()))
            .check(matches(withText("Pemesanan Tiket Berhasil Diverifikasi")))
    }

    // Tugas Mandiri 2 (Negative Flow UI Automation)
    @Test
    fun kosongkanNik_danTekanPesan_harusTampilPeringatanError() {
        // 1. Pastikan etNik kosong
        onView(withId(R.id.etNik))
            .perform(clearText())

        // 2. Ketik Nama Penumpang
        onView(withId(R.id.etName))
            .perform(typeText("Ahmad Dahlan"), closeSoftKeyboard())

        // 3. Tekan Tombol Pesan Tiket
        onView(withId(R.id.btnSubmitBooking))
            .perform(click())

        // 4. Verifikasi bahwa pesan error muncul di TextInputLayout (tilNik)
        onView(withId(com.google.android.material.R.id.textinput_error))
            .check(matches(withText("NIK tidak boleh kosong")))
            
        // 5. Verifikasi bahwa tvBookingStatus tidak tampil
        onView(withId(R.id.cvStatus))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
    }
}
