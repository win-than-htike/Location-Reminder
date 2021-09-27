package com.udacity.project4.source

import androidx.lifecycle.LiveData
import com.udacity.project4.datasource.local.ReminderDataSource
import com.udacity.project4.model.Remainder
import com.udacity.project4.utils.Result

class FakeDataSource : ReminderDataSource {

  private var remainders: MutableList<Remainder> = mutableListOf()
  private var shouldReturnError = false

  fun setShouldReturnError(shouldReturn: Boolean) {
    this.shouldReturnError = shouldReturn
  }

  override suspend fun saveRemainder(remainder: Remainder) {
    remainders.add(remainder)
  }

  override fun observeRemainders(): LiveData<List<Remainder>> {
    TODO("Not yet implemented")
  }

  override suspend fun deleteRemainder(remainder: Remainder) {
    remainders.remove(remainder)
  }

  override suspend fun getRemainderById(id: String): Result<Remainder> {
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

  override suspend fun getRemainders(): Result<List<Remainder>> {
    if (shouldReturnError) {
      return Result.Error(message = "no remainder found exception")
    }
    return Result.Success(remainders.toList())
  }

  override suspend fun deleteAllRemainder() {
    remainders.clear()
  }
}