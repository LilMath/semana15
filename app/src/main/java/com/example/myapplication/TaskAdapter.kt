package com.example.myapplication

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemTaskBinding

class TaskAdapter(
    private val tasks: MutableList<Task>,
    private val onTaskChecked: (Task, Boolean) -> Unit,
    private val onTaskDelete: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.binding.tvTaskName.text = task.nome
        holder.binding.cbDone.isChecked = task.concluida

        updateStrikeThrough(holder, task.concluida)

        holder.binding.cbDone.setOnCheckedChangeListener { _, isChecked ->
            onTaskChecked(task, isChecked)
        }

        holder.binding.ivDelete.setOnClickListener {
            onTaskDelete(task)
        }
    }

    private fun updateStrikeThrough(holder: TaskViewHolder, isDone: Boolean) {
        if (isDone) {
            holder.binding.tvTaskName.paintFlags = holder.binding.tvTaskName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.binding.tvTaskName.paintFlags = holder.binding.tvTaskName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    override fun getItemCount() = tasks.size
}
