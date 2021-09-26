package me.onething.locationreminder.datasource.local

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.onething.locationreminder.db.RemaindersDao
import me.onething.locationreminder.model.Remainder
import me.onething.locationreminder.utils.Result

class ReminderLocalDataSource constructor(private val dao: RemaindersDao) :
  RemainderDataSource {
  override suspend fun saveRemainder(remainder: Remainder) {
    withContext(Dispatchers.IO) {
      dao.insertRemainder(remainder)
    }
  }

  override fun observeRemainders(): LiveData<List<Remainder>> {
    return dao.observeRemainders()
  }

  override suspend fun deleteRemainder(remainder: Remainder) {
    dao.deleteRemainder(remainder)
  }

  override suspend fun getRemainderById(id: String) = withContext(Dispatchers.IO) {
    return@withContext try {
      Result.Success(dao.getRemainderById(id)!!)
    } catch (e: Exception) {
      Result.Error(e.localizedMessage)
    }
  }

  override suspend fun getRemainders() = withContext(Dispatchers.IO) {
    return@withContext try {
      Result.Success(dao.getRemainders())
    } catch (e: Exception) {
      Result.Error(e.localizedMessage)
    }
  }

  override suspend fun deleteAllRemainder() {
    dao.deleteAllRemainders()
  }
}