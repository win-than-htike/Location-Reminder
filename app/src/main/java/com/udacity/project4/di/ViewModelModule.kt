package com.udacity.project4.di

import com.udacity.project4.feature.add.AddNewReminderViewModel
import com.udacity.project4.feature.detail.ReminderDetailViewModel
import com.udacity.project4.feature.list.RemindersViewModel
import com.udacity.project4.feature.map.MapViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
  viewModel {
    ReminderDetailViewModel(get())
  }
  viewModel {
    RemindersViewModel(get())
  }
  viewModel {
    AddNewReminderViewModel(get(),get())
  }

  viewModel {
    MapViewModel()
  }
}