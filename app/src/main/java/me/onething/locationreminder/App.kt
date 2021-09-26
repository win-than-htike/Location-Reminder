package me.onething.locationreminder

import android.app.Application
import me.onething.locationreminder.datasource.local.RemainderDataSource
import me.onething.locationreminder.datasource.local.ReminderLocalDataSource
import me.onething.locationreminder.db.AppDatabase
import me.onething.locationreminder.db.DB
import me.onething.locationreminder.feature.add.AddNewRemainderViewModel
import me.onething.locationreminder.feature.detail.RemainderDetailViewModel
import me.onething.locationreminder.feature.list.RemaindersViewModel
import me.onething.locationreminder.feature.map.MapViewModel
import me.onething.locationreminder.repo.RemaindersRepository
import me.onething.locationreminder.repo.RemaindersRepositoryImpl
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