package me.onething.locationreminder.feature.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.onething.locationreminder.base.BaseViewModel
import me.onething.locationreminder.model.Remainder
import me.onething.locationreminder.repo.RemaindersRepository
import me.onething.locationreminder.utils.Result

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