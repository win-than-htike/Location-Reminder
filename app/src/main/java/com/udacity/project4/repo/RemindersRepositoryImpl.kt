package com.udacity.project4.repo

import com.udacity.project4.datasource.local.ReminderDataSource
import com.udacity.project4.model.Reminder
import com.udacity.project4.utils.wrapEspressoIdlingResource
import com.udacity.project4.utils.Result

class RemindersRepositoryImpl constructor(
  private val remindersLocalDataSource: ReminderDataSource,
) :
  RemindersRepository {

  override fun observeReminders() = remindersLocalDataSource.observeReminders()

  override suspend fun saveReminder(reminder: Reminder) {
    remindersLocalDataSource.saveReminder(reminder)
  }

  override suspend fun deleteReminder(reminder: Reminder) {
    wrapEspressoIdlingResource {
      remindersLocalDataSource.deleteReminder(reminder)
    }
  }

  override suspend fun getReminderById(id: String): Result<Reminder> {
    wrapEspressoIdlingResource {
      return try {
        remindersLocalDataSource.getReminderById(id)
      } catch (e: Exception) {
        Result.Error(e.localizedMessage)
      }
    }
  }

  override suspend fun getReminders(): Result<List<Reminder>> {
    wrapEspressoIdlingResource {
      return try {
        remindersLocalDataSource.getReminders()
      } catch (e: Exception) {
        Result.Error(e.localizedMessage)
      }
    }
  }
}