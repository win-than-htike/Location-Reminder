package me.onething.locationreminder.utils

import android.annotation.SuppressLint
import android.content.IntentSender
import android.location.Location
import android.os.Looper
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import me.onething.locationreminder.MainActivity
import timber.log.Timber

class LocationHandler(
  private val context: MainActivity,
  private val onLocationUpdated: (location: Location) -> Unit,
  private val onLocationUpdateOnError: (message: String) -> Unit
) {

  companion object {
    const val REQUEST_CHECK_SETTINGS = 300
  }

  private var fusedLocationClient: FusedLocationProviderClient =
    LocationServices.getFusedLocationProviderClient(context)
  private var locationRequest: LocationRequest = LocationRequest.create().apply {
    interval = 10000
    fastestInterval = 5000
    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
  }
  private val REQUEST_CHECK_SETTINGS = 300

  private var locationCallback: LocationCallback = object : LocationCallback() {
    override fun onLocationResult(locationResult: LocationResult?) {
      locationResult ?: return
      Timber.d("Location update ${locationResult.locations.last()}")
      if (locationResult.locations.isNotEmpty()) {
        onLocationUpdated(locationResult.locations.last())
        stopLocationUpdates()
      }
    }
  }

  @SuppressLint("MissingPermission")
  fun getLastKnownLocation() {
    fusedLocationClient.lastLocation
      .addOnSuccessListener {
        if (it != null) onLocationUpdated(it) else startLocationUpdates(locationRequest)
      }
      .addOnFailureListener {
        onLocationUpdateOnError(it.localizedMessage.orEmpty())
      }
  }

  fun requestLocation() {
    val builder = LocationSettingsRequest.Builder()
      .addLocationRequest(locationRequest)
    val client: SettingsClient = LocationServices.getSettingsClient(context)
    val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
    task.addOnSuccessListener { locationSettingsResponse ->
      getLastKnownLocation()
    }
    task.addOnFailureListener { exception ->
      if (exception is ResolvableApiException) {
        try {
          exception.startResolutionForResult(
            context,
            REQUEST_CHECK_SETTINGS
          )
        } catch (sendEx: IntentSender.SendIntentException) {
          sendEx.printStackTrace()
          onLocationUpdateOnError(sendEx.localizedMessage.orEmpty())
        }
      }
    }
  }

  fun stopLocationUpdates() {
    fusedLocationClient.removeLocationUpdates(locationCallback)
  }

  @SuppressLint("MissingPermission")
  fun startLocationUpdates(locationRequest: LocationRequest) {
    fusedLocationClient.requestLocationUpdates(
      locationRequest,
      locationCallback,
      Looper.getMainLooper()
    )
  }
}