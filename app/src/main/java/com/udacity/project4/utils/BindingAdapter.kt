package com.udacity.project4.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.project4.model.Reminder

@BindingAdapter("adapter")
fun bindAdapter(recyclerView: RecyclerView, adapter: ListAdapter<*, *>) {
  recyclerView.adapter = adapter
}

@BindingAdapter("items")
fun setItems(recyclerView: RecyclerView, items: List<Reminder>?) {
  (recyclerView.adapter as ListAdapter<*, *>).submitList(items as List<Nothing>?)
}

@BindingAdapter("latlng")
fun bindLatLng(textView: TextView, remainder: Reminder?) {
  remainder?.let {
    textView.text = "${remainder.latitude}, ${remainder.longitude}"
  }
}