package com.udacity.project4.feature.list

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.firebase.ui.auth.AuthUI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.udacity.project4.R
import com.udacity.project4.base.BaseViewModel
import com.udacity.project4.model.Reminder
import com.udacity.project4.repo.RemindersRepository
import com.udacity.project4.utils.Event
import com.udacity.project4.utils.Result.Error
import com.udacity.project4.utils.Result.Success

class RemindersViewModel constructor(private val repository: RemindersRepository) :
  BaseViewModel() {
  val remindersList = MutableLiveData<List<Reminder>>()
  private val _logoutEvent = MutableLiveData<Event<Unit>>()
  val loading = MutableLiveData<Boolean>(false)


  private val _remainders: LiveData<List<Reminder>> =
    repository.observeReminders()

  val remainders: LiveData<List<Reminder>>
    get() = _remainders


  val logoutEvent: LiveData<Event<Unit>>
    get() = _logoutEvent


  fun logoutEvent() {
    _logoutEvent.value = Event(Unit)
  }


  private val _isEmptyRemainders = Transformations.map(remindersList) {
    it.isNullOrEmpty()
  }


  val isEmptyRemainders: LiveData<Boolean>
    get() = _isEmptyRemainders


  fun logout(context: Context) {
    AuthUI.getInstance().signOut(context)
      .addOnSuccessListener { logoutEvent() }
  }

  fun deleteRemainder(remainder: Reminder) {
    CoroutineScope(Dispatchers.IO).launch {
      repository.deleteReminder(remainder)
    }
    showSnackBarInt.value = Event(R.string.remainder_deleted)
    loadReminders()
  }


  fun loadReminders() {
    loading.value = true
    viewModelScope.launch {
      //interacting with the dataSource has to be through a coroutine
      val result = repository.getReminders()
      loading.postValue(false)
      when (result) {
        is Success<*> -> {
          val dataList = ArrayList<Reminder>()
          @Suppress("UNCHECKED_CAST")
          dataList.addAll((result.data as List<Reminder>).map { reminder ->
            //map the reminder data from the DB to the be ready to be displayed on the UI
            Reminder(
              title = reminder.title,
              description = reminder.description,
              latitude = reminder.latitude,
              longitude = reminder.longitude,
              place = reminder.place,
              id = reminder.id
            )
          })
          remindersList.value = dataList
        }
        is Error ->
          showSnackBar.value = Event(result.message.orEmpty())
      }
      invalidateShowNoData()
    }
  }

  /**
   * Inform the user that there's not any data if the remindersList is empty
   */
  private fun invalidateShowNoData() {
    showNoData.value = remindersList.value == null || remindersList.value!!.isEmpty()
  }

}