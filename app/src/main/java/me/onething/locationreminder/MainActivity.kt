package me.onething.locationreminder

import android.Manifest
import android.annotation.TargetApi
import android.app.Application
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import me.onething.locationreminder.databinding.ActivityMainBinding
import me.onething.locationreminder.utils.createChannel
import org.koin.core.context.GlobalContext
import timber.log.Timber

private const val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 33
private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
private const val REQUEST_TURN_DEVICE_LOCATION_ON = 29
private const val TAG = "MainActivity"
private const val LOCATION_PERMISSION_INDEX = 0
private const val BACKGROUND_LOCATION_PERMISSION_INDEX = 1

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController


    private var _onPermissionGranted: (() -> Unit)? = null
    private var _onPermissionDenied: (() -> Unit)? = null

    private var snackBarDuration = Snackbar.LENGTH_INDEFINITE

    private val runningQOrLater =
        android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initNavController()
    }

    private fun initNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        val appBarConfiguration =
            AppBarConfiguration.Builder(R.id.remaindersFragment, R.id.loginFragment)
                .build()
        setupActionBarWithNavController(navController, appBarConfiguration)
        createChannel(this)
    }

    override fun onNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }


    @TargetApi(29)
    private fun foregroundAndBackgroundLocationPermissionApproved(toRequestBoth: Boolean = true): Boolean {
        val foregroundLocationApproved = (
          PackageManager.PERMISSION_GRANTED ==
            ActivityCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ))
        val backgroundPermissionApproved =
            if (runningQOrLater) {
                PackageManager.PERMISSION_GRANTED ==
                  ActivityCompat.checkSelfPermission(
                      this.applicationContext,
                      Manifest.permission.ACCESS_BACKGROUND_LOCATION
                  )
            } else {
                true
            }
        return if (toRequestBoth) {
            foregroundLocationApproved && backgroundPermissionApproved
        } else {
            foregroundLocationApproved
        }
    }


    @TargetApi(29)
    private fun requestForegroundAndBackgroundLocationPermissions() {
        if (foregroundAndBackgroundLocationPermissionApproved())
            return
        var permissionsArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        val resultCodeBoth = when {
            runningQOrLater -> {
                permissionsArray += Manifest.permission.ACCESS_BACKGROUND_LOCATION
                REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE
            }
            else -> REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
        }
        Timber.d("Request foreground only location permission")

        ActivityCompat.requestPermissions(
            this,
            permissionsArray,
            resultCodeBoth
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TURN_DEVICE_LOCATION_ON) {
            checkDeviceLocationSettingsAndStartGeofence(false)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Timber.d("onRequestPermissionResult")
        if (requestCode == REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE) {
            if (grantResults[LOCATION_PERMISSION_INDEX] == PackageManager.PERMISSION_GRANTED) {
                _onPermissionGranted?.invoke()
            } else {
                showSettingSnackBar()
            }
        } else if (
            grantResults.isEmpty() ||
            grantResults[LOCATION_PERMISSION_INDEX] == PackageManager.PERMISSION_DENIED ||
            (requestCode == REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE &&
              grantResults[BACKGROUND_LOCATION_PERMISSION_INDEX] ==
              PackageManager.PERMISSION_DENIED)
        ) {
            showSettingSnackBar()
            _onPermissionDenied?.invoke()
        } else {
            checkDeviceLocationSettingsAndStartGeofence(resolve = false)
        }
    }

    private fun showSettingSnackBar() {
        Snackbar.make(
            binding.layoutMain,
            R.string.permission_denied_explanation,
            snackBarDuration
        )
            .setAction(R.string.settings) {
                startActivity(Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
            }.show()
    }

    private fun checkDeviceLocationSettingsAndStartGeofence(resolve: Boolean = true) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(this)
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())
        locationSettingsResponseTask.addOnFailureListener { exception ->
            Timber.d("exception ${exception.message}")
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(
                        this,
                        REQUEST_TURN_DEVICE_LOCATION_ON
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    sendEx.printStackTrace()
                    _onPermissionDenied?.invoke()
                    Snackbar.make(
                        binding.layoutMain,
                        R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
                    ).setAction(android.R.string.ok) {
                        checkDeviceLocationSettingsAndStartGeofence()
                    }.show()
                }
            }
        }
        locationSettingsResponseTask.addOnCompleteListener {
            if (it.isSuccessful) {
                _onPermissionGranted?.invoke()
            }
        }
    }


    fun checkPermissionsAndStartGeofencing(
        onPermissionGranted: () -> Unit,
        onPermissionDenied: () -> Unit
    ) {
        this._onPermissionGranted = onPermissionGranted
        this._onPermissionDenied = onPermissionDenied
        if (foregroundAndBackgroundLocationPermissionApproved()) {
            checkDeviceLocationSettingsAndStartGeofence()
        } else {
            requestForegroundAndBackgroundLocationPermissions()
        }
    }

    fun requestForegroundLocationPermission(
        onPermissionGranted: () -> Unit,
        onPermissionDenied: () -> Unit
    ) {
        this._onPermissionGranted = onPermissionGranted
        this._onPermissionDenied = onPermissionDenied
        if (foregroundAndBackgroundLocationPermissionApproved(false)) {
            onPermissionGranted()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    private val locationPermissions =
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        } else {
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }

    fun hasLocationPermissions(): Boolean =
        allPermissionsGranted(*locationPermissions)

    private fun allPermissionsGranted(vararg permissions: String): Boolean = permissions.all {
        val context = GlobalContext.getOrNull()?.koin?.get<Application>() ?: return false
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

}