package me.onething.locationreminder.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import me.onething.locationreminder.model.Remainder
import me.onething.locationreminder.repo.RemaindersRepository
import me.onething.locationreminder.utils.Result

class FakeRepository : RemaindersRepository {

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