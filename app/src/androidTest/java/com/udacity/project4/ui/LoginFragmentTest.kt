package com.udacity.project4.ui

import android.os.Bundle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.udacity.project4.R
import com.udacity.project4.utils.launchFragmentInHiltContainer
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.feature.login.LoginFragment
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@MediumTest
@RunWith(AndroidJUnit4::class)
class LoginFragmentTest {


  @Before
  fun logoutFirst() {
    FirebaseAuth.getInstance().signOut()
  }

  @Test
  fun loginFragment_performActions() {
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    launchFragmentInHiltContainer<LoginFragment>(Bundle(), R.style.AppTheme)
    onView(withId(R.id.btnLogin)).check(matches(isClickable()))
    onView(withId(R.id.tvIntro)).check(matches(withText(appContext.getString(R.string.label_login_description_screen))))
  }
}