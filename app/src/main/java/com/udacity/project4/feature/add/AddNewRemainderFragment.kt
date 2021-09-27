package com.udacity.project4.feature.add

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER
import com.google.android.gms.location.Geofence.NEVER_EXPIRE
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.MainActivity
import com.udacity.project4.R
import com.udacity.project4.databinding.FragmentAddNewRemainderBinding
import com.udacity.project4.geofence.GeofenceBroadcastReceiver
import com.udacity.project4.utils.GeofenceUtils
import com.udacity.project4.utils.safeNavigate
import com.udacity.project4.utils.showSnackBar
import org.koin.android.ext.android.inject
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 * Use the [AddNewRemainderFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddNewRemainderFragment : Fragment() {

  companion object {
    const val SELECTED_POI = "SELECTED_POI"
    internal const val ACTION_GEOFENCE_EVENT =
      "AddNewRemainderFragment.action.ACTION_GEOFENCE_EVENT"
  }

  val viewModel: AddNewRemainderViewModel by inject()
  private lateinit var binding: FragmentAddNewRemainderBinding

  private lateinit var geofencingClient: GeofencingClient

  private val geofencePendingIntent: PendingIntent by lazy {
    val intent =
      Intent(requireActivity().applicationContext, GeofenceBroadcastReceiver::class.java)
    intent.action = ACTION_GEOFENCE_EVENT
    PendingIntent.getBroadcast(
      requireActivity().applicationContext,
      0,
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT
    )
  }


  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding =
      DataBindingUtil.inflate(inflater, R.layout.fragment_add_new_remainder, container, false)
    binding.apply {
      lifecycleOwner = viewLifecycleOwner
      vm = viewModel
    }
    setupActions()

//        // avoid on testing process
//        if (activity is MainActivity) {
//            getBackStackData<Point>(SELECTED_POI) { data ->
//                viewModel.updatePOI(data)
//            }
//        }

    viewModel.savedRemainderEvent.observe(viewLifecycleOwner, {
      if (it != null) {
        if (requireActivity() is MainActivity) {
          (requireActivity() as MainActivity).checkPermissionsAndStartGeofencing(
            onPermissionDenied = {},
            onPermissionGranted = {
              binding.root.showSnackBar(getString(R.string.text_saved_remainder))
              addGeofence()
            })
        }
      }
    })

    viewModel.showSnackBarInt.observe(viewLifecycleOwner, {
      if (it != null) {
        it.getContentIfNotHandled()?.let {
          Snackbar.make(binding.root, getString(it), 2000).show()
        }
      }
    })


    viewModel.toastInt.observe(viewLifecycleOwner, {
      if (it != null) {
        it.getContentIfNotHandled()?.let {
          Toast.makeText(
            requireContext(),
            getString(it),
            Toast.LENGTH_SHORT
          ).show()
        }
      }
    })
    init()
    return binding.root
  }


  private fun setupActions() {
    with(binding) {
      btnLocation.setOnClickListener {
        findNavController().safeNavigate(AddNewRemainderFragmentDirections.actionAddNewRemainderToMapFragment())
      }
      fabSaveRemainder.setOnClickListener {
        viewModel.addNewRemainder()
      }
    }
  }

  @SuppressLint("MissingPermission")
  private fun addGeofence() {
    val geofenceData = viewModel.poi.value
    geofenceData?.let {
      val geofence = Geofence.Builder()
        .setRequestId(viewModel.savedRemainder?.id)
        .setCircularRegion(
          geofenceData.latLng.latitude,
          geofenceData.latLng.longitude,
          GeofenceUtils.GEOFENCE_RADIUS_IN_METERS
        )
        .setExpirationDuration(NEVER_EXPIRE)
        .setTransitionTypes(GEOFENCE_TRANSITION_ENTER)
        .build()

      val geofencingRequest = GeofencingRequest.Builder()
        .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
        .addGeofence(geofence)
        .build()
      viewModel.clearAll()
      geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)?.run {
        addOnSuccessListener {
          binding.root.showSnackBar(getString(R.string.geofences_added))
          goToRemainders()
          Timber.d("Add Geofence ${geofence.requestId}")
        }
        addOnFailureListener {
          binding.root.showSnackBar(getString(R.string.geofences_not_added))
          if ((it.message != null)) {
            Timber.d("geofence not added ${it.message}")
          }
        }
      }
    }
  }

  private fun goToRemainders() {
    findNavController().popBackStack()
  }

  private fun init() {
    geofencingClient =
      LocationServices.getGeofencingClient(requireActivity().applicationContext)
  }

}