package me.onething.locationreminder

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import me.onething.locationreminder.datasource.local.ReminderLocalDataSource
import me.onething.locationreminder.db.AppDatabase
import me.onething.locationreminder.repo.RemaindersRepository
import me.onething.locationreminder.repo.RemaindersRepositoryImpl

object ServiceLocator {
  private var database: AppDatabase? = null

  @Volatile
  var repository: RemaindersRepository? = null
    @VisibleForTesting set
  private val lock = Any()

  fun provideTasksRepository(context: Context): RemaindersRepository {
    synchronized(this) {
      return repository ?: createTasksRepository(context)
    }
  }

  private fun createTasksRepository(context: Context): RemaindersRepository {
    val newRepo = RemaindersRepositoryImpl(createTaskLocalDataSource(context))
    repository = newRepo
    return newRepo
  }

  private fun createTaskLocalDataSource(context: Context): ReminderLocalDataSource {
    val database = database ?: createDataBase(context)
    return ReminderLocalDataSource(database.remaindersDao())
  }

  private fun createDataBase(context: Context): AppDatabase {
    val result = Room.databaseBuilder(
      context.applicationContext,
      AppDatabase::class.java, "Remainders.db"
    )
      .fallbackToDestructiveMigration()
      .build()
    database = result
    return result
  }

  @VisibleForTesting
  fun resetRepository() {
    synchronized(lock) {
      database?.apply {
        clearAllTables()
        close()
      }
      database = null
      repository = null
    }
  }
}


