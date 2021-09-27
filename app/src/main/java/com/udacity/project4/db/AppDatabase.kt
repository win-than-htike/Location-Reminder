package com.udacity.project4.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.udacity.project4.model.Remainder

@Database(entities = [Remainder::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun remaindersDao(): RemindersDao
}