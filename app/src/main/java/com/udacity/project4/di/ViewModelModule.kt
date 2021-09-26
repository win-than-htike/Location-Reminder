package com.udacity.project4.di

import com.udacity.project4.feature.add.AddNewRemainderViewModel
import com.udacity.project4.feature.detail.RemainderDetailViewModel
import com.udacity.project4.feature.list.RemaindersViewModel
import com.udacity.project4.feature.map.MapViewModel
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