package com.udacity.project4.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.project4.model.Reminder

@Dao
interface RemindersDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: Reminder)

    @Query("SELECT * FROM reminder")
    fun observeReminders(): LiveData<List<Reminder>>

    @Query("SELECT * FROM reminder")
    fun getReminders(): List<Reminder>

    @Query("SELECT * FROM reminder WHERE reminderId = :id LIMIT 1")
    suspend fun getReminderById(id: String): Reminder?

    @Update
    fun updateReminder(reminder: Reminder)

    @Delete
    fun deleteReminder(reminder: Reminder)

    @Query("DELETE FROM reminder")
    fun deleteAllReminders(): Int
}