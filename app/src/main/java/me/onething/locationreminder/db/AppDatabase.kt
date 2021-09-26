package me.onething.locationreminder.db

import androidx.room.Database
import androidx.room.RoomDatabase
import me.onething.locationreminder.model.Remainder

@Database(entities = [Remainder::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun remaindersDao(): RemaindersDao
}