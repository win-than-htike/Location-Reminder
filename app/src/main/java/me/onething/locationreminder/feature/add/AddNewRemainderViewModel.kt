package me.onething.locationreminder.feature.add

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.onething.locationreminder.R
import me.onething.locationreminder.model.Point
import me.onething.locationreminder.model.Remainder
import me.onething.locationreminder.repo.RemaindersRepository
import me.onething.locationreminder.utils.Event
import timber.log.Timber

class AddNewRemainderViewModel constructor(
  val app: Application,
  remainderRepository: RemaindersRepository
) :
  AndroidViewModel(app) {

  private val repository = remainderRepository

  private val _selectedPOI = MutableLiveData<Point?>()

  val showSnackBarInt = MutableLiveData<Event<Int>?>()
  val toastInt = MutableLiveData<Event<Int>?>()

  var title = MutableLiveData<String>()
  var description = MutableLiveData<String>()
  var savedRemainder: Remainder? = null


  fun updatePOI(data: Point) {
    _selectedPOI.postValue(data)
  }


  private val _savedRemainderEvent = MutableLiveData<Event<Int>?>()

  val savedRemainderEvent: LiveData<Event<Int>?>
    get() = _savedRemainderEvent


  val poi: LiveData<Point?>
    get() = _selectedPOI


  fun isValidToSave(): Boolean {
    if (title.value.orEmpty().isEmpty()) {
      showSnackBarInt.value = Event(R.string.error_message_title_empty)
      return false
    }

    if (description.value.orEmpty().isEmpty()) {
      showSnackBarInt.value = Event(R.string.error_message_description_empty)
      return false
    }

    if (_selectedPOI.value == null) {
      showSnackBarInt.value = Event(R.string.error_message_poi_empty)
      return false
    }

    return title.value.orEmpty().isNotEmpty() && description.value.orEmpty()
      .isNotEmpty() && _selectedPOI.value != null
  }

  fun addNewRemainder() {
    if (isValidToSave()) {
      val remainder = Remainder(
        title = title.value.orEmpty(),
        description = description.value.orEmpty(),
        longitude = _selectedPOI.value?.latLng?.longitude ?: 0.0,
        latitude = _selectedPOI.value?.latLng?.latitude ?: 0.0,
        place = _selectedPOI.value?.address?.featureName.orEmpty(),
      )
      savedRemainder = remainder
      viewModelScope.launch {
        repository.saveReminder(remainder)
        title.value = ""
        description.value = ""
        savedRemainder()
      }
    }
  }


  fun savedRemainder() {
    _savedRemainderEvent.value = Event(R.string.text_add_new_remainder_sucess)
    toastInt.value = Event(R.string.text_add_new_remainder_sucess)
  }


  fun clearAll() {
    title.value = ""
    description.value = ""
    _selectedPOI.value = null
    showSnackBarInt.value = null
    toastInt.value = null
    _savedRemainderEvent.value = null
  }

}