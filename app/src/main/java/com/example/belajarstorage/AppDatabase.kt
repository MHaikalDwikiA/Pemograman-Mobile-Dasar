package com.example.belajarstorage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Catatan::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun catatanDao(): CatatanDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Singleton pattern: memastikan hanya ada 1 instance database yang dibuat
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "catatan_database" // Nama file fisik database di memori internal[cite: 1]
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}