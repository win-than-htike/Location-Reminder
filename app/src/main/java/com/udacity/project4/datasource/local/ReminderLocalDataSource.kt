package com.udacity.project4.datasource.local

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.udacity.project4.db.RemindersDao
import com.udacity.project4.model.Remainder
import com.udacity.project4.utils.Result

class ReminderLocalDataSource constructor(private val dao: RemindersDao) :
  ReminderDataSource {
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