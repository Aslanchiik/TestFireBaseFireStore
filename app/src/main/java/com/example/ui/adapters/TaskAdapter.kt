package com.example.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.model.TaskModel
import com.example.testfirebasefirestore.databinding.ItemAdapterBinding

class TaskAdapter  : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    lateinit var binding : ItemAdapterBinding
    private var list : ArrayList<TaskModel> = ArrayList()

      fun addAllList (model : ArrayList<TaskModel>) {
          this.list = model
          notifyDataSetChanged()
      }

  inner  class ViewHolder(itemView: View) : RecyclerView.ViewHolder (itemView){

        fun bind(taskModel: TaskModel) {
             binding.itemName.text = taskModel.name
         //   binding.dateTxt.text = taskModel.time
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }


}

