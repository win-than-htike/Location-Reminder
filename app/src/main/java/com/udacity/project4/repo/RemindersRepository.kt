package com.udacity.project4.repo

import androidx.lifecycle.LiveData
import com.udacity.project4.model.Reminder
import com.udacity.project4.utils.Result

interface RemindersRepository {

    fun observeReminders(): LiveData<List<Reminder>>

    suspend fun saveReminder(reminder: Reminder)

    suspend fun deleteReminder(reminder: Reminder)

    suspend fun getReminderById(id: String): Result<Reminder>

    suspend fun getReminders(): Result<List<Reminder>>

}