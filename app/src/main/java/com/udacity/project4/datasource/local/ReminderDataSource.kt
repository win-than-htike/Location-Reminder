package com.udacity.project4.datasource.local

import androidx.lifecycle.LiveData
import com.udacity.project4.model.Reminder
import com.udacity.project4.utils.Result

interface ReminderDataSource {

    suspend fun saveReminder(reminder: Reminder)

    suspend fun getReminderById(id: String): Result<Reminder>

    suspend fun getReminders(): Result<List<Reminder>>

    fun observeReminders(): LiveData<List<Reminder>>

    suspend fun deleteReminder(reminder: Reminder)

    suspend fun deleteAllReminder()

}