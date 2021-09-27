package com.udacity.project4.feature.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.udacity.project4.R
import com.udacity.project4.databinding.FragmentRemindersBinding
import com.udacity.project4.model.Reminder
import com.udacity.project4.utils.GeofenceUtils
import com.udacity.project4.utils.safeNavigate
import com.udacity.project4.utils.showSnackBar
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 * Use the [RemaindersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
private const val TAG = "RemindersFragment"

class RemindersFragment : Fragment(), RemainderAdapterCallback {

  private lateinit var binding: FragmentRemindersBinding

  val viewModel: RemindersViewModel by viewModel()

  private lateinit var adapter: RemainderAdapter

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reminders, container, false)
    adapter = RemainderAdapter(this)
    binding.apply {
      vm = viewModel
      lifecycleOwner = viewLifecycleOwner
      adapter = this@RemindersFragment.adapter
    }
    binding.fabAddRemainder.setOnClickListener {
      findNavController().safeNavigate(RemaindersFragmentDirections.actionRemaindersFragmentToAddNewRemainder())
    }
    initObserver()
    return binding.root
  }

  override fun onResume() {
    super.onResume()
    viewModel.loadReminders()
  }


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.remainder_menu, menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.menuLogout -> {
        viewModel.logout(requireActivity())
        true
      }
      else -> false
    }
  }


  private fun initObserver() {
    viewModel.logoutEvent.observe(viewLifecycleOwner, { logout ->
      findNavController().navigate(RemaindersFragmentDirections.actionRemaindersFragmentToLoginFragment())
    })
    viewModel.showSnackBarInt.observe(viewLifecycleOwner, Observer {
      it.getContentIfNotHandled()?.let {
        binding.root.showSnackBar(getString(it))
      }
    })
    viewModel.showSnackBar.observe(viewLifecycleOwner, {
      it.getContentIfNotHandled()?.let {
        binding.root.showSnackBar(it)
      }
    })
  }


  override fun itemDelete(remainder: Reminder) {
    viewModel.deleteRemainder(remainder)
  }

  override fun onItemClick(remainder: Reminder) {
    val bundle = Bundle()
    bundle.putString(GeofenceUtils.GEOFENCE_EXTRA, remainder.id)
    findNavController().safeNavigate(
      RemaindersFragmentDirections.actionRemaindersFragmentToRemainderDetailFragment(
        remainder.id
      )
    )
  }

}