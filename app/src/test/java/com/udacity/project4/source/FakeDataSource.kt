package com.udacity.project4.source

import androidx.lifecycle.LiveData
import com.udacity.project4.datasource.local.ReminderDataSource
import com.udacity.project4.model.Reminder
import com.udacity.project4.utils.Result

class FakeDataSource : ReminderDataSource {

  private var reminders: MutableList<Reminder> = mutableListOf()
  private var shouldReturnError = false

  fun setShouldReturnError(shouldReturn: Boolean) {
    this.shouldReturnError = shouldReturn
  }

  override suspend fun saveReminder(reminder: Reminder) {
    reminders.add(reminder)
  }

  override fun observeReminders(): LiveData<List<Reminder>> {
    TODO("Not yet implemented")
  }

  override suspend fun deleteReminder(reminder: Reminder) {
    reminders.remove(reminder)
  }

  override suspend fun getReminderById(id: String): Result<Reminder> {
    if (shouldReturnError) {
      return Result.Error("no reminder found exception")
    }
    val reminder = reminders.findLast { id == it.id }
    return if (reminder != null) {
      Result.Success(reminder)
    } else {
      Result.Error("no reminder found")
    }
  }

  override suspend fun getReminders(): Result<List<Reminder>> {
    if (shouldReturnError) {
      return Result.Error(message = "no reminder found exception")
    }
    return Result.Success(reminders.toList())
  }

  override suspend fun deleteAllReminder() {
    reminders.clear()
  }
}