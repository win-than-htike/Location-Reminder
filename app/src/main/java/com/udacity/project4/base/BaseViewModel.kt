package com.udacity.project4.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udacity.project4.utils.Event

abstract class BaseViewModel : ViewModel() {
  val showSnackBar = MutableLiveData<Event<String>>()
  val showSnackBarInt = MutableLiveData<Event<Int>>()
  val toastInt = MutableLiveData<Event<Int>>()
  val showNoData: MutableLiveData<Boolean> = MutableLiveData()

}