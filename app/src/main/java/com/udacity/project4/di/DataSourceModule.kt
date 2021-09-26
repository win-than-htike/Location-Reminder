package com.udacity.project4.di

import com.udacity.project4.datasource.local.RemainderDataSource
import com.udacity.project4.datasource.local.ReminderLocalDataSource
import org.koin.dsl.module

val dataSourceModule = module {
  single<RemainderDataSource> {
    ReminderLocalDataSource(get())
  }
}