package me.onething.locationreminder.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import me.onething.locationreminder.utils.Event

abstract class BaseViewModel : ViewModel() {
  val showSnackBar = MutableLiveData<Event<String>>()
  val showSnackBarInt = MutableLiveData<Event<Int>>()
  val toastInt = MutableLiveData<Event<Int>>()
  val showNoData: MutableLiveData<Boolean> = MutableLiveData()

}