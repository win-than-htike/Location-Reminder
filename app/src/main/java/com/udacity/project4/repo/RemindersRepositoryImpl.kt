package com.udacity.project4.repo

import com.udacity.project4.datasource.local.ReminderDataSource
import com.udacity.project4.model.Remainder
import com.udacity.project4.utils.wrapEspressoIdlingResource
import com.udacity.project4.utils.Result

class RemindersRepositoryImpl constructor(
  private val remaindersLocalDataSource: ReminderDataSource,
) :
  RemindersRepository {

  override fun observeRemainders() = remaindersLocalDataSource.observeRemainders()

  override suspend fun saveReminder(remainder: Remainder) {
    remaindersLocalDataSource.saveRemainder(remainder)
  }

  override suspend fun deleteRemainder(remainder: Remainder) {
    wrapEspressoIdlingResource {
      remaindersLocalDataSource.deleteRemainder(remainder)
    }
  }

  override suspend fun getRemainderById(id: String): Result<Remainder> {
    wrapEspressoIdlingResource {
      return try {
        remaindersLocalDataSource.getRemainderById(id)
      } catch (e: Exception) {
        Result.Error(e.localizedMessage)
      }
    }
  }

  override suspend fun getRemainders(): Result<List<Remainder>> {
    wrapEspressoIdlingResource {
      return try {
        remaindersLocalDataSource.getRemainders()
      } catch (e: Exception) {
        Result.Error(e.localizedMessage)
      }
    }
  }
}