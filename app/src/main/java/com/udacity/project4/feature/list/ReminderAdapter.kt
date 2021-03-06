package com.udacity.project4.feature.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.project4.databinding.ItemReminderBinding
import com.udacity.project4.model.Reminder

interface ReminderAdapterCallback {
  fun itemDelete(reminder: Reminder)
  fun onItemClick(reminder: Reminder)
}

class ReminderAdapter(private val callback: ReminderAdapterCallback) :
  ListAdapter<Reminder, ReminderVH>(diffUtils) {


  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderVH {

    return ReminderVH(
      ItemReminderBinding.inflate(LayoutInflater.from(parent.context), parent, false),
      callback
    )
  }

  override fun onBindViewHolder(holder: ReminderVH, position: Int) {
    holder.onBind(getItem(position))
  }


  companion object {
    val diffUtils = object : DiffUtil.ItemCallback<Reminder>() {
      override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
        return oldItem.id == newItem.id
      }

      override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
        return oldItem == newItem
      }
    }
  }
}


class ReminderVH(
  private val binding: ItemReminderBinding,
  private val callback: ReminderAdapterCallback
) :
  RecyclerView.ViewHolder(binding.root) {
  fun onBind(item: Reminder) {
    binding.apply {
      reminder = item
      ivDelete.setOnClickListener { callback.itemDelete(item) }
      this.root.setOnClickListener {
        callback.onItemClick(item)
      }
    }
  }
}