package com.udacity.project4.source

import androidx.lifecycle.LiveData
import com.udacity.project4.datasource.local.ReminderDataSource
import com.udacity.project4.model.Reminder
import com.udacity.project4.utils.Result

class FakeDataSource : ReminderDataSource {

  private var remainders: MutableList<Reminder> = mutableListOf()
  private var shouldReturnError = false

  fun setShouldReturnError(shouldReturn: Boolean) {
    this.shouldReturnError = shouldReturn
  }

  override suspend fun saveReminder(reminder: Reminder) {
    remainders.add(reminder)
  }

  override fun observeReminders(): LiveData<List<Reminder>> {
    TODO("Not yet implemented")
  }

  override suspend fun deleteReminder(reminder: Reminder) {
    remainders.remove(reminder)
  }

  override suspend fun getReminderById(id: String): Result<Reminder> {
    if (shouldReturnError) {
      return Result.Error("no remainder found exception")
    }
    val remainder = remainders.findLast { id == it.id }
    return if (remainder != null) {
      Result.Success(remainder)
    } else {
      Result.Error("no remainder found")
    }
  }

  override suspend fun getReminders(): Result<List<Reminder>> {
    if (shouldReturnError) {
      return Result.Error(message = "no remainder found exception")
    }
    return Result.Success(remainders.toList())
  }

  override suspend fun deleteAllReminder() {
    remainders.clear()
  }
}