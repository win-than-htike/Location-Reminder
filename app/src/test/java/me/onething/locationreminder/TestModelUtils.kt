package me.onething.locationreminder

import me.onething.locationreminder.model.Remainder

object TestModelUtils {
  fun getTestRemainder(): Remainder {
    return Remainder(
      "1",
      "title",
      "description",
      0.0,
      0.0,
      "place",
    )
  }
}