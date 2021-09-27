package com.udacity.project4.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.project4.model.Reminder

@Dao
interface RemindersDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRemainder(remainder: Reminder)

    @Query("SELECT * FROM reminder")
    fun observeRemainders(): LiveData<List<Reminder>>

    @Query("SELECT * FROM reminder")
    fun getRemainders(): List<Reminder>

    @Query("SELECT * FROM reminder WHERE reminderId = :id LIMIT 1")
    suspend fun getRemainderById(id: String): Reminder?

    @Update
    fun updateRemainder(remainder: Reminder)

    @Delete
    fun deleteRemainder(remainder: Reminder)

    @Query("DELETE FROM reminder")
    fun deleteAllRemainders(): Int
}