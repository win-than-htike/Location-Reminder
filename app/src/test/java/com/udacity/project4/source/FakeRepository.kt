package com.udacity.project4.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.project4.model.Remainder
import com.udacity.project4.repo.RemindersRepository
import com.udacity.project4.utils.Result

class FakeRepository : RemindersRepository {

  private var remainders: MutableList<Remainder> = mutableListOf()
  private var remainderLiveData = MutableLiveData<List<Remainder>>()

  private var shouldReturnError = false

  fun setShouldReturnError(shouldReturn: Boolean) {
    this.shouldReturnError = shouldReturn
  }

  override fun observeRemainders(): LiveData<List<Remainder>> {
    return remainderLiveData
  }

  override suspend fun saveReminder(remainder: Remainder) {
    remainders.add(remainder)
    remainderLiveData.value = remainders
  }

  override suspend fun deleteRemainder(remainder: Remainder) {
    remainders.remove(remainder)
  }

  override suspend fun getRemainderById(id: String): Result<Remainder> {
    if (shouldReturnError) {
      return Result.Error("no remainder found exception")
    }
    val remainder = remainders.find { it.id == id }
    return if(remainder!=null){
      Result.Success(remainder)
    }else{
      Result.Error("no remainder found")
    }
  }

  override suspend fun getRemainders(): Result<List<Remainder>> {
    return if (shouldReturnError) {
      Result.Error("No remainder found exception")
    } else {
      Result.Success(remainders)
    }
  }

}