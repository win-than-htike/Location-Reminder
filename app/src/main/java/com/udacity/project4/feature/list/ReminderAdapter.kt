package com.udacity.project4.feature.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.project4.databinding.ItemReminderBinding
import com.udacity.project4.model.Reminder

interface RemainderAdapterCallback {
  fun itemDelete(remainder: Reminder)
  fun onItemClick(remainder: Reminder)
}

class RemainderAdapter(private val callback: RemainderAdapterCallback) :
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
  private val callback: RemainderAdapterCallback
) :
  RecyclerView.ViewHolder(binding.root) {
  fun onBind(item: Reminder) {
    binding.apply {
      remainder = item
      ivDelete.setOnClickListener { callback.itemDelete(item) }
      this.root.setOnClickListener {
        callback.onItemClick(item)
      }
    }
  }
}