package me.onething.locationreminder.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.google.android.gms.location.GeofenceStatusCodes
import me.onething.locationreminder.R
import java.util.Locale
import java.util.concurrent.TimeUnit

object GeofenceUtils {

  const val GEOFENCE_RADIUS_IN_METERS = 100f
  val GEOFENCE_EXPIRATION_IN_MILLISECONDS: Long = TimeUnit.HOURS.toMillis(1)
  const val GEOFENCE_EXTRA = "GEOFENCE_EXTRA"

  fun errorMessage(context: Context, errorCode: Int): String {
    val resources = context.resources
    return when (errorCode) {
      GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> resources.getString(
        R.string.geofence_not_available
      )
      GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> resources.getString(
        R.string.geofence_too_many_geofences
      )
      GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> resources.getString(
        R.string.geofence_too_many_pending_intents
      )
      else -> resources.getString(R.string.unknown_geofence_error)
    }
  }  fun getAddress(context: Context, latitude: Double, longitude: Double): Address? {

    try {
      val geocoder: Geocoder = Geocoder(context, Locale.getDefault())
      val addresses: List<Address> = geocoder.getFromLocation(
        latitude,
        longitude,
        1
      )
      return if (addresses.isNotEmpty()) {
        addresses[0]
      } else {
        null
      }
    }catch (e :Exception){
      val address =Address(Locale.ENGLISH)
      address.featureName = "No name found"
      return address
    }


  }

}