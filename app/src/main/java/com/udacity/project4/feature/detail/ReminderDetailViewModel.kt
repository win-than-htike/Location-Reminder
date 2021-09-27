package com.udacity.project4.feature.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.udacity.project4.base.BaseViewModel
import com.udacity.project4.model.Reminder
import com.udacity.project4.repo.RemindersRepository
import com.udacity.project4.utils.Result

class ReminderDetailViewModel constructor(repository: RemindersRepository) :
  BaseViewModel() {
  private val _reminder = MutableLiveData<Reminder>()

  val remindersRepository = repository

  val reminder: LiveData<Reminder>
    get() = _reminder

  fun getReminderByPlaceId(placeId: String) {
    viewModelScope.launch {
      remindersRepository.getReminderById(placeId).let {
        if (it is Result.Success) {
          _reminder.value = it.data
        }
      }
    }
  }
}