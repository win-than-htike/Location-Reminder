package com.udacity.project4.geofence

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.udacity.project4.repo.RemindersRepository
import com.udacity.project4.utils.GeofenceUtils
import com.udacity.project4.utils.sendGeofenceEnteredNotification
import org.koin.android.ext.android.inject
import timber.log.Timber
import com.udacity.project4.utils.Result

class GeoJobService : JobIntentService() {
  private val repository: RemindersRepository by inject()


  override fun onHandleWork(intent: Intent) {
    Timber.d("on Handle Job")
    val geofencingEvent = GeofencingEvent.fromIntent(intent)
    Timber.d("Geofence Receiver ${geofencingEvent.triggeringGeofences.size}")
    if (geofencingEvent.hasError()) {
      val errorMessage = GeofenceUtils.errorMessage(this, geofencingEvent.errorCode)
      Timber.d("${GeofenceBroadcastReceiver.TAG} error ${errorMessage}")
      return
    }
    if (geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
      geofencingEvent.triggeringGeofences.forEach(::showNotification)
    }
  }


  private fun showNotification(geo: Geofence) {
    CoroutineScope(Dispatchers.IO).launch {
      val reminder = repository.getReminderById(geo.requestId) as Result.Success
      Timber.d("Geofence Reminder $reminder")
      if (reminder != null) {
        val notificationManager = ContextCompat.getSystemService(
          this@GeoJobService.applicationContext,
          NotificationManager::class.java
        ) as NotificationManager

        notificationManager.sendGeofenceEnteredNotification(
          this@GeoJobService.applicationContext,
          reminder.data
        )
      }
    }
  }

  companion object {
    private const val JOB_ID = 343
    fun enqueueWork(context: Context, work: Intent) {
      enqueueWork(context, GeoJobService::class.java, JOB_ID, work)
    }
  }
}