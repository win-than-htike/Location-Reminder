package com.udacity.project4.feature.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.udacity.project4.R.layout

/**
 * A simple [Fragment] subclass.
 * Use the [RemainderDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RemainderDetailFragment : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    return inflater.inflate(layout.fragment_remainder_detail, container, false)
  }

}