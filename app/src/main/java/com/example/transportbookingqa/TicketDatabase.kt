package com.example.transportbookingqa

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase

@Entity(tableName = "tickets")
data class TicketEntity(
    @PrimaryKey val ticketCode: String,
    val passengerName: String,
    val destination: String
)

@Dao
interface TicketDao {
    @Insert
    suspend fun insertTicket(ticket: TicketEntity)

    @Query("SELECT * FROM tickets WHERE ticketCode = :code")
    suspend fun getTicketByCode(code: String): TicketEntity?
}

@Database(entities = [TicketEntity::class], version = 1, exportSchema = false)
abstract class TestAppDatabase : RoomDatabase() {
    abstract fun ticketDao(): TicketDao
}
