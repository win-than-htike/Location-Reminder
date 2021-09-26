package com.udacity.project4.db

import android.content.Context
import androidx.room.Room

object DB {
  fun createRemainderDatabase(context: Context): RemaindersDao {
    return Room.databaseBuilder(
      context.applicationContext,
      AppDatabase::class.java, "Remainders.db"
    ).build().remaindersDao()
  }
}