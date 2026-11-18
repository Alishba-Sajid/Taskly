package com.example.taskly.presentation.home

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.taskly.R
import com.example.taskly.core.extention.isOverdue
import com.example.taskly.core.extention.show
import com.example.taskly.core.extention.toDueDateText
import com.example.taskly.databinding.ItemTaskBinding
import com.example.taskly.domain.model.Task
import com.example.taskly.domain.model.TaskStatus

class TaskAdapter(
    private val onEditClick: (Task) -> Unit,
    private val onDeleteClick: (Task) -> Unit,
    private val onStatusChangeClick: (Task, Boolean) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: TaskViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(
        private val binding: ItemTaskBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            val context = binding.root.context
            val isOverdue = task.isOverdue()

            val statusText: String
            val backgroundColor: Int
            val textColor: Int

            when {
                task.status == TaskStatus.COMPLETED -> {
                    statusText = context.getString(
                        R.string.filter_completed
                    )
                    backgroundColor = "#DCE9DF".toColorInt()
                    textColor = "#245B45".toColorInt()
                }

                isOverdue -> {
                    statusText = context.getString(
                        R.string.filter_overdue
                    )
                    backgroundColor = "#FCE8E6".toColorInt()
                    textColor = "#B3261E".toColorInt()
                }

                else -> {
                    statusText = context.getString(
                        R.string.filter_in_progress
                    )
                    backgroundColor = "#E3EDF9".toColorInt()
                    textColor = "#285B8F".toColorInt()
                }
            }

            val cornerRadius = 8 * binding.root.resources
                .displayMetrics.density

            binding.apply {
                tvTaskTitle.text = task.title
                tvTaskDescription.text = task.description

                tvTaskStatus.text = statusText
                tvTaskStatus.setTextColor(textColor)

                tvTaskStatus.background = GradientDrawable().apply {
                    setColor(backgroundColor)
                    this.cornerRadius = cornerRadius
                }

                tvTaskDueDate.text = task.toDueDateText(context)
                tvTaskDueDate.show()

                cbTaskCompleted.setOnCheckedChangeListener(null)

                cbTaskCompleted.isChecked =
                    task.status == TaskStatus.COMPLETED

                cbTaskCompleted.setOnCheckedChangeListener { _, isChecked ->
                    onStatusChangeClick(task, isChecked)
                }

                btnEditTask.setOnClickListener {
                    onEditClick(task)
                }

                btnDeleteTask.setOnClickListener {
                    onDeleteClick(task)
                }
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {

        override fun areItemsTheSame(
            oldItem: Task,
            newItem: Task
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Task,
            newItem: Task
        ): Boolean {
            return oldItem == newItem
        }
    }
}
