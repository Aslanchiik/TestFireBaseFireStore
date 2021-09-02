package com.example.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.model.TaskModel
import com.example.testfirebasefirestore.databinding.ItemAdapterBinding

class TaskAdapter : ListAdapter<TaskModel, TaskAdapter.ViewHolder>(diffCallback) {

    class ViewHolder(private val binding: ItemAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(taskModel: TaskModel) {
            binding.textViewTitle.text = taskModel.name
            binding.textViewPriority.text = taskModel.time

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<TaskModel>() {
            override fun areItemsTheSame(
                oldItem: TaskModel,
                newItem: TaskModel
            ): Boolean {
                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(
                oldItem: TaskModel,
                newItem: TaskModel)
            : Boolean {
                return oldItem == newItem
            }

        }
    }

}

