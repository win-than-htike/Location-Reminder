package com.udacity.project4.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "reminder")
data class Reminder(
    @PrimaryKey @ColumnInfo(name = "reminderId")
    var id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val place: String,
)