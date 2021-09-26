package me.onething.locationreminder.di

import me.onething.locationreminder.repo.RemaindersRepository
import me.onething.locationreminder.repo.RemaindersRepositoryImpl
import org.koin.dsl.module

val repositoryyModule = module {
  single<RemaindersRepository> {
    RemaindersRepositoryImpl(get())
  }
}