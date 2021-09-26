package me.onething.locationreminder.di

import me.onething.locationreminder.datasource.local.RemainderDataSource
import me.onething.locationreminder.datasource.local.ReminderLocalDataSource
import org.koin.dsl.module

val dataSourceModule = module {
  single<RemainderDataSource> {
    ReminderLocalDataSource(get())
  }
}