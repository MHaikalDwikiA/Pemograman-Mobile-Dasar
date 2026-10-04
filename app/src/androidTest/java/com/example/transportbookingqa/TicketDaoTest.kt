package com.example.transportbookingqa

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TicketDaoTest {

    private lateinit var db: TestAppDatabase
    private lateinit var dao: TicketDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // inMemoryDatabaseBuilder: Database murni hidup di RAM, musnah setelah test selesai
        db = Room.inMemoryDatabaseBuilder(context, TestAppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.ticketDao()
    }

    @After
    fun closeDb() {
        if (::db.isInitialized) {
            db.close()
        }
    }

    @Test
    fun insertDanAmbilTiket_harusTersimpanPersis() = runBlocking {
        val tiketBaru = TicketEntity("TKT-8801", "Budi Santoso", "Surabaya Gubeng")
        dao.insertTicket(tiketBaru)

        val retrieved = dao.getTicketByCode("TKT-8801")
        assertThat(retrieved).isNotNull()
        assertThat(retrieved?.passengerName).isEqualTo("Budi Santoso")
    }
}