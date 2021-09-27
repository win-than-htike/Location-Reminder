package com.udacity.project4.datasource.local

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.udacity.project4.db.RemindersDao
import com.udacity.project4.model.Reminder
import com.udacity.project4.utils.Result

class ReminderLocalDataSource constructor(private val dao: RemindersDao) :
  ReminderDataSource {
  override suspend fun saveReminder(reminder: Reminder) {
    withContext(Dispatchers.IO) {
      dao.insertReminder(reminder)
    }
  }

  override fun observeReminders(): LiveData<List<Reminder>> {
    return dao.observeReminders()
  }

  override suspend fun deleteReminder(reminder: Reminder) {
    dao.deleteReminder(reminder)
  }

  override suspend fun getReminderById(id: String) = withContext(Dispatchers.IO) {
    return@withContext try {
      Result.Success(dao.getReminderById(id)!!)
    } catch (e: Exception) {
      Result.Error(e.localizedMessage)
    }
  }

  override suspend fun getReminders() = withContext(Dispatchers.IO) {
    return@withContext try {
      Result.Success(dao.getReminders())
    } catch (e: Exception) {
      Result.Error(e.localizedMessage)
    }
  }

  override suspend fun deleteAllReminder() {
    dao.deleteAllReminders()
  }
}