package com.udacity.project4

import android.app.Application
import com.udacity.project4.datasource.local.RemainderDataSource
import com.udacity.project4.datasource.local.ReminderLocalDataSource
import com.udacity.project4.db.DB
import com.udacity.project4.feature.add.AddNewRemainderViewModel
import com.udacity.project4.feature.detail.RemainderDetailViewModel
import com.udacity.project4.feature.list.RemaindersViewModel
import com.udacity.project4.feature.map.MapViewModel
import com.udacity.project4.repo.RemaindersRepository
import com.udacity.project4.repo.RemaindersRepositoryImpl
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
      single<RemainderDataSource> { ReminderLocalDataSource(get()) }
      single<RemaindersRepository> { RemaindersRepositoryImpl(get() as RemainderDataSource) }
      single { DB.createRemainderDatabase(this@App) }
      viewModel {
        RemaindersViewModel(
          get() as RemaindersRepository,
        )
      }
      viewModel { MapViewModel() }
      viewModel { RemainderDetailViewModel(get() as RemaindersRepository) }
      single {
        AddNewRemainderViewModel(
          get(),
          get() as RemaindersRepository,
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