package me.onething.locationreminder.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import me.onething.locationreminder.feature.add.AddNewRemainderFragment.Companion.ACTION_GEOFENCE_EVENT

class GeofenceBroadcastReceiver : BroadcastReceiver() {

  override fun onReceive(context: Context, intent: Intent) {
    if (intent.action == ACTION_GEOFENCE_EVENT) {
      GeoJobService.enqueueWork(context, intent)
    }
  }

  companion object {
    const val TAG = "GeofenceBroadcastReceiver"
  }
}