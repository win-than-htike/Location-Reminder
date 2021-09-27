package com.udacity.project4.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.project4.model.Reminder
import com.udacity.project4.repo.RemindersRepository
import com.udacity.project4.utils.Result

class FakeAndroidRepository : RemindersRepository {

  private var reminders: MutableList<Reminder> = mutableListOf()
  private var reminderLiveData = MutableLiveData<List<Reminder>>()


  override fun observeReminders(): LiveData<List<Reminder>> {
    return reminderLiveData
  }

  override suspend fun saveReminder(reminder: Reminder) {
    reminders.add(reminder)
    reminderLiveData.postValue(reminders)
  }

  override suspend fun deleteReminder(reminder: Reminder) {
    reminders.remove(reminder)
  }

  override suspend fun getReminderById(id: String): Result<Reminder> {
    val reminder = reminders.findLast { it.id == id }
    return if (reminder != null) {
      Result.Success(reminder)
    } else {
      Result.Error("no reminder found")
    }
  }

  override suspend fun getReminders(): Result<List<Reminder>> {
    return Result.Success(reminders)
  }
}