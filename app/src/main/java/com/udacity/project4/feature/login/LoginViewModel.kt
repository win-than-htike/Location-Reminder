package com.udacity.project4.feature.login

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.udacity.project4.R

class LoginViewModel : ViewModel() {

  private val _user = MutableLiveData<FirebaseUser?>()


  val user: LiveData<FirebaseUser?>
    get() = _user


  val isLoggedIn = Transformations.map(_user) {
    it != null
  }


  init {
    _user.value = FirebaseAuth.getInstance().currentUser
  }


  private val providers = arrayListOf(
    AuthUI.IdpConfig.EmailBuilder().build(),
    AuthUI.IdpConfig.GoogleBuilder().build(),
  )

  // Create and launch sign-in intent
  val signInIntent = AuthUI.getInstance()
    .createSignInIntentBuilder()
    .setAvailableProviders(providers)
    .setTheme(R.style.FirebaseUI_ReminderUITheme)
    .setLogo(R.drawable.login_background)
    .build()


  fun login(user: FirebaseUser) {
    _user.value = user
  }

  fun logout(context: Context) {
    AuthUI.getInstance()
      .signOut(context)
      .addOnCompleteListener {
        _user.value = null
      }
  }

}