package com.udacity.project4.di

import androidx.room.Room
import com.udacity.project4.db.AppDatabase
import org.koin.dsl.module

val databaseModule = module {
  single {
    Room.databaseBuilder(get(),
      AppDatabase::class.java, "Remainders.db")
      .fallbackToDestructiveMigration()
      .build()
  }

  single {
    (get() as AppDatabase).remindersDao()
  }
}