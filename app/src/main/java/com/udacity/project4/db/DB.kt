package com.udacity.project4.db

import android.content.Context
import androidx.room.Room

object DB {
  fun createRemainderDatabase(context: Context): RemindersDao {
    return Room.databaseBuilder(
      context.applicationContext,
      AppDatabase::class.java, "Reminders.db"
    ).build().remindersDao()
  }
}