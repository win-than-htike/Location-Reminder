package com.udacity.project4.feature.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.udacity.project4.base.BaseViewModel
import com.udacity.project4.model.Remainder
import com.udacity.project4.repo.RemaindersRepository
import com.udacity.project4.utils.Result

class RemainderDetailViewModel constructor(repository: RemaindersRepository) :
  BaseViewModel() {
  private val _remainder = MutableLiveData<Remainder>()

  val remaindersRepository = repository

  val remainder: LiveData<Remainder>
    get() = _remainder

  fun getRemainderByPlaceId(placeId: String) {
    viewModelScope.launch {
      remaindersRepository.getRemainderById(placeId).let {
        if (it is Result.Success) {
          _remainder.value = it.data
        }
      }
    }
  }
}