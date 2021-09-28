package com.udacity.project4

import com.udacity.project4.model.Reminder

object TestAndroidModelUtils {
  fun getTestReminder(): Reminder {
    return Reminder(
      "1",
      "title",
      "description",
      0.0,
      0.0,
      "place",
    )
  }
}