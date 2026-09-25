package com.example.belajarstorage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CatatanDao {
    // 1. Menyisipkan satu data catatan baru (suspend untuk Coroutine)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCatatan(catatan: Catatan)

    // 2. Mengambil seluruh daftar catatan diurutkan dari ID terbesar (terbaru)
    @Query("SELECT * FROM tabel_catatan ORDER BY id DESC")
    suspend fun getAllCatatan(): List<Catatan>

    // 3. Menghapus seluruh isi catatan
    @Query("DELETE FROM tabel_catatan")
    suspend fun deleteAll()
}