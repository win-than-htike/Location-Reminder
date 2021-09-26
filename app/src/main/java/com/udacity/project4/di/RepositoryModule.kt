package com.udacity.project4.di

import com.udacity.project4.repo.RemaindersRepository
import com.udacity.project4.repo.RemaindersRepositoryImpl
import org.koin.dsl.module

val repositoryyModule = module {
  single<RemaindersRepository> {
    RemaindersRepositoryImpl(get())
  }
}