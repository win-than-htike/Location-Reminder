package me.onething.locationreminder.di

import androidx.room.Room
import me.onething.locationreminder.db.AppDatabase
import org.koin.dsl.module

val databaseModule = module {
  single {
    Room.databaseBuilder(get(),
      AppDatabase::class.java, "Remainders.db")
      .fallbackToDestructiveMigration()
      .build()
  }

  single {
    (get() as AppDatabase).remaindersDao()
  }
}