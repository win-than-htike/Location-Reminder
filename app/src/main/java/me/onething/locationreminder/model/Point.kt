package me.onething.locationreminder.model

import android.location.Address
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class Point(
    val latLng: LatLng,
    val address: Address?
) : Parcelable