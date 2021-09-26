package me.onething.locationreminder.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import me.onething.locationreminder.MainActivity
import me.onething.locationreminder.R
import me.onething.locationreminder.model.Remainder
import java.util.Date

fun createChannel(context: Context) {
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    val notificationChannel = NotificationChannel(
      CHANNEL_ID,
      context.getString(R.string.channel_name),

      NotificationManager.IMPORTANCE_HIGH
    )
      .apply {
        setShowBadge(false)
      }

    notificationChannel.enableLights(true)
    notificationChannel.lightColor = Color.RED
    notificationChannel.enableVibration(true)
    notificationChannel.description =
      context.getString(R.string.notification_channel_description)

    val notificationManager = context.getSystemService(NotificationManager::class.java)
    notificationManager.createNotificationChannel(notificationChannel)
  }
}

fun pendingIntent(context: Context, placeId: String): PendingIntent {
  val bundle = Bundle()
  bundle.putString(GeofenceUtils.GEOFENCE_EXTRA, placeId)
  return NavDeepLinkBuilder(context)
    .setGraph(R.navigation.nav_graph)
    .setDestination(R.id.remainderDetailFragment)
    .setArguments(bundle)
    .setComponentName(MainActivity::class.java)
    .createPendingIntent()
}

fun NotificationManager.sendGeofenceEnteredNotification(context: Context, remainder: Remainder) {
  val notiId = Date().time.toInt()
  val contentIntent = Intent(context, MainActivity::class.java)
  contentIntent.putExtra(GeofenceUtils.GEOFENCE_EXTRA, remainder.id)
  val mapImage = BitmapFactory.decodeResource(
    context.resources,
    R.drawable.map_small
  )
  val bigPicStyle = NotificationCompat.BigPictureStyle()
    .bigPicture(mapImage)
    .bigLargeIcon(null)

  val builder = NotificationCompat.Builder(context, CHANNEL_ID)
    .setContentTitle(context.getString(R.string.app_name))
    .setContentText(
      context.getString(
        R.string.content_text,
        (remainder.place)
      )
    )
    .setPriority(NotificationCompat.PRIORITY_HIGH)
    .setContentIntent(pendingIntent(context, remainder.id))
    .setSmallIcon(R.drawable.map_small)
    .setStyle(bigPicStyle)
    .setAutoCancel(true)
    .setLargeIcon(mapImage)

  notify(notiId, builder.build())
}

private const val CHANNEL_ID = "LocationRemainderChannel"