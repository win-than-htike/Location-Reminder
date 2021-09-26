package me.onething.locationreminder.repo

import me.onething.locationreminder.datasource.local.RemainderDataSource
import me.onething.locationreminder.model.Remainder
import me.onething.locationreminder.utils.wrapEspressoIdlingResource
import me.onething.locationreminder.utils.Result

class RemaindersRepositoryImpl constructor(
  private val remaindersLocalDataSource: RemainderDataSource,
) :
  RemaindersRepository {

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