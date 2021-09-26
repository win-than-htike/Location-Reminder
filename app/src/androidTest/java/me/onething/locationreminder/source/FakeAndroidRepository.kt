package me.onething.locationreminder.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import me.onething.locationreminder.model.Remainder
import me.onething.locationreminder.repo.RemaindersRepository
import me.onething.locationreminder.utils.Result

class FakeAndroidRepository : RemaindersRepository {

  private var remainders: MutableList<Remainder> = mutableListOf()
  private var remainderLiveData = MutableLiveData<List<Remainder>>()


  override fun observeRemainders(): LiveData<List<Remainder>> {
    return remainderLiveData
  }

  override suspend fun saveReminder(remainder: Remainder) {
    remainders.add(remainder)
    remainderLiveData.postValue(remainders)
  }

  override suspend fun deleteRemainder(remainder: Remainder) {
    remainders.remove(remainder)
  }

  override suspend fun getRemainderById(id: String): Result<Remainder> {
    val remainder = remainders.findLast { it.id == id }
    return if (remainder != null) {
      Result.Success(remainder)
    } else {
      Result.Error("no remainder found")
    }
  }

  override suspend fun getRemainders(): Result<List<Remainder>> {
    return Result.Success(remainders)
  }
}