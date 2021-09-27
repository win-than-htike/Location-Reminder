package com.udacity.project4.di

import com.udacity.project4.repo.RemindersRepository
import com.udacity.project4.repo.RemindersRepositoryImpl
import org.koin.dsl.module

val repositoryyModule = module {
  single<RemindersRepository> {
    RemindersRepositoryImpl(get())
  }
}