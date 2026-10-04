package com.example.transportbookingqa

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class FareCalculatorTest {

    private lateinit var calculator: FareCalculator

    @Before
    fun setUp() {
        calculator = FareCalculator()
    }

    @Test
    fun `isValidNik dengan 16 digit angka harus bernilai true`() {
        val validNik = "3209123456780001"
        val result = calculator.isValidNik(validNik)
        assertThat(result).isTrue()
    }

    @Test
    fun `isValidNik dengan kurang dari 16 digit harus bernilai false`() {
        val shortNik = "32091234"
        val result = calculator.isValidNik(shortNik)
        assertThat(result).isFalse()
    }

    @Test
    fun `isValidNik mengandung huruf harus bernilai false`() {
        val invalidNik = "320912345678ABCD"
        val result = calculator.isValidNik(invalidNik)
        assertThat(result).isFalse()
    }

    @Test
    fun `calculateTotalFare untuk penumpang lansia umur 65 tahun mendapat diskon 20 persen`() {
        val baseFare = 500000.0 // Tarif dasar Rp 500.000
        val total = calculator.calculateTotalFare(baseFare, passengerAge = 65, baggageWeightKg = 15)

        // Rp 500.000 - 20% = Rp 400.000 (Bagasi 15kg gratis)
        assertThat(total).isEqualTo(400000.0)
    }

    @Test
    fun `calculateTotalFare dengan kelebihan bagasi 25 kg dikenakan denda bagasi`() {
        val baseFare = 500000.0
        val total = calculator.calculateTotalFare(baseFare, passengerAge = 30, baggageWeightKg = 25)

        // Kelebihan: 5 kg x Rp 25.000 = Rp 125.000. Total = Rp 625.000
        assertThat(total).isEqualTo(625000.0)
    }

    // Tugas Mandiri 1 (a)
    @Test(expected = IllegalArgumentException::class)
    fun `calculateTotalFare dengan tarif dasar negatif harus melempar IllegalArgumentException`() {
        calculator.calculateTotalFare(-10000.0, passengerAge = 30, baggageWeightKg = 10)
    }

    // Tugas Mandiri 1 (b)
    @Test
    fun `calculateTotalFare untuk balita umur 2 tahun harus mendapat diskon 100 persen (Rp 0)`() {
        val baseFare = 500000.0
        val total = calculator.calculateTotalFare(baseFare, passengerAge = 2, baggageWeightKg = 10)
        assertThat(total).isEqualTo(0.0)
    }
}
