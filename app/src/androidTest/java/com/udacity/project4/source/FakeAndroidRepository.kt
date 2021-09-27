package com.udacity.project4.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.project4.model.Reminder
import com.udacity.project4.repo.RemindersRepository
import com.udacity.project4.utils.Result

class FakeAndroidRepository : RemindersRepository {

  private var remainders: MutableList<Reminder> = mutableListOf()
  private var remainderLiveData = MutableLiveData<List<Reminder>>()


  override fun observeReminders(): LiveData<List<Reminder>> {
    return remainderLiveData
  }

  override suspend fun saveReminder(reminder: Reminder) {
    remainders.add(reminder)
    remainderLiveData.postValue(remainders)
  }

  override suspend fun deleteReminder(reminder: Reminder) {
    remainders.remove(reminder)
  }

  override suspend fun getReminderById(id: String): Result<Reminder> {
    val remainder = remainders.findLast { it.id == id }
    return if (remainder != null) {
      Result.Success(remainder)
    } else {
      Result.Error("no remainder found")
    }
  }

  override suspend fun getReminders(): Result<List<Reminder>> {
    return Result.Success(remainders)
  }
}