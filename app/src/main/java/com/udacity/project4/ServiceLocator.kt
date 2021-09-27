package com.udacity.project4

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.udacity.project4.datasource.local.ReminderLocalDataSource
import com.udacity.project4.db.AppDatabase
import com.udacity.project4.repo.RemindersRepository
import com.udacity.project4.repo.RemindersRepositoryImpl

object ServiceLocator {
  private var database: AppDatabase? = null

  @Volatile
  var repository: RemindersRepository? = null
    @VisibleForTesting set
  private val lock = Any()

  fun provideTasksRepository(context: Context): RemindersRepository {
    synchronized(this) {
      return repository ?: createTasksRepository(context)
    }
  }

  private fun createTasksRepository(context: Context): RemindersRepository {
    val newRepo = RemindersRepositoryImpl(createTaskLocalDataSource(context))
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


