package me.onething.locationreminder.di

import me.onething.locationreminder.feature.add.AddNewRemainderViewModel
import me.onething.locationreminder.feature.detail.RemainderDetailViewModel
import me.onething.locationreminder.feature.list.RemaindersViewModel
import me.onething.locationreminder.feature.map.MapViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
  viewModel {
    RemainderDetailViewModel(get())
  }
  viewModel {
    RemaindersViewModel(get())
  }
  viewModel {
    AddNewRemainderViewModel(get(),get())
  }

  viewModel {
    MapViewModel()
  }
}