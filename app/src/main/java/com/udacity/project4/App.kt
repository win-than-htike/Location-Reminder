package com.udacity.project4

import android.app.Application
import com.udacity.project4.datasource.local.ReminderDataSource
import com.udacity.project4.datasource.local.ReminderLocalDataSource
import com.udacity.project4.db.DB
import com.udacity.project4.feature.add.AddNewReminderViewModel
import com.udacity.project4.feature.detail.ReminderDetailViewModel
import com.udacity.project4.feature.list.RemindersViewModel
import com.udacity.project4.feature.map.MapViewModel
import com.udacity.project4.repo.RemindersRepository
import com.udacity.project4.repo.RemindersRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module
import timber.log.Timber

class App : Application() {

  override fun onCreate() {
    super.onCreate()
    if (BuildConfig.DEBUG)
      Timber.plant(Timber.DebugTree())

    val myModule = module {
      single<ReminderDataSource> { ReminderLocalDataSource(get()) }
      single<RemindersRepository> { RemindersRepositoryImpl(get() as ReminderDataSource) }
      single { DB.createReminderDatabase(this@App) }
      viewModel {
        RemindersViewModel(
          get() as RemindersRepository,
        )
      }
      viewModel { MapViewModel() }
      viewModel { ReminderDetailViewModel(get() as RemindersRepository) }
      single {
        AddNewReminderViewModel(
          get(),
          get() as RemindersRepository,
        )
      }
    }

    startKoin {
      androidLogger(Level.ERROR)
      androidContext(this@App)
      modules(listOf(myModule))
    }
  }

}