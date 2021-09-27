package com.udacity.project4.feature.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.udacity.project4.R
import com.udacity.project4.databinding.FragmentRemainderDetailBinding
import com.udacity.project4.utils.GeofenceUtils
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 * Use the [ReminderDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ReminderDetailFragment : Fragment() {

  private lateinit var binding: FragmentRemainderDetailBinding
  val viewModel: ReminderDetailViewModel by viewModel()

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    // Inflate the layout for this fragment
    binding =
      DataBindingUtil.inflate(inflater, R.layout.fragment_remainder_detail, container, false)
    binding.apply {
      vm = viewModel
      lifecycleOwner = viewLifecycleOwner
    }
    val placeId = arguments?.getString(GeofenceUtils.GEOFENCE_EXTRA)
    viewModel.getRemainderByPlaceId(placeId.orEmpty())
    viewModel.remainder.observe(viewLifecycleOwner, { remainder ->
      if(requireActivity() is AppCompatActivity)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = remainder.title
    })
    return binding.root
  }

}