package com.udacity.project4.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.project4.model.Reminder
import com.udacity.project4.repo.RemindersRepository
import com.udacity.project4.utils.Result

class FakeRepository : RemindersRepository {

  private var reminders: MutableList<Reminder> = mutableListOf()
  private var reminderLiveData = MutableLiveData<List<Reminder>>()

  private var shouldReturnError = false

  fun setShouldReturnError(shouldReturn: Boolean) {
    this.shouldReturnError = shouldReturn
  }

  override fun observeReminders(): LiveData<List<Reminder>> {
    return reminderLiveData
  }

  override suspend fun saveReminder(reminder: Reminder) {
    reminders.add(reminder)
    reminderLiveData.value = reminders
  }

  override suspend fun deleteReminder(reminder: Reminder) {
    reminders.remove(reminder)
  }

  override suspend fun getReminderById(id: String): Result<Reminder> {
    if (shouldReturnError) {
      return Result.Error("no remainder found exception")
    }
    val remainder = reminders.find { it.id == id }
    return if(remainder!=null){
      Result.Success(remainder)
    }else{
      Result.Error("no remainder found")
    }
  }

  override suspend fun getReminders(): Result<List<Reminder>> {
    return if (shouldReturnError) {
      Result.Error("No remainder found exception")
    } else {
      Result.Success(reminders)
    }
  }

}