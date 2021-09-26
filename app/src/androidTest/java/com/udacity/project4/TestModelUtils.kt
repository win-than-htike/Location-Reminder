package com.udacity.project4

import com.udacity.project4.model.Remainder

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