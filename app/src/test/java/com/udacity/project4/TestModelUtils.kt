package com.udacity.project4

import com.udacity.project4.model.Reminder

object TestModelUtils {
  fun getTestRemainder(): Reminder {
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