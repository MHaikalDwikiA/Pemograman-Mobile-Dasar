package com.example.kurirdirectoryapp
import com.google.gson.annotations.SerializedName

// Kelas pembungkus (Wrapper) karena struktur dari DummyJSON diawali dengan array "users"
data class DummyUserResponse(
    @SerializedName("users")
    val users: List<UserItem>
)

// Model Data Utama Pengguna / Kurir
data class UserItem(
    @SerializedName("id")
    val id: Int,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("company")
    val company: CompanyInfo
)

// Model Anak untuk Membaca Objek Bersarang 'company'
data class CompanyInfo(
    @SerializedName("name")
    val companyName: String,
    @SerializedName("title")
    val title: String // Jabatan Pekerjaan
)