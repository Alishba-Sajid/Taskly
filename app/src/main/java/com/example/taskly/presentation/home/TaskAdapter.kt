package com.example.taskly.presentation.home

import androidx.core.graphics.toColorInt
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.taskly.R
import com.example.taskly.databinding.ItemTaskBinding
import com.example.taskly.domain.model.Task
import com.example.taskly.domain.model.TaskStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
            binding.tvTaskTitle.text = task.title
            binding.tvTaskDescription.text = task.description

            val today = SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.US
            ).format(Date())

            val isOverdue =
                task.status == TaskStatus.IN_PROGRESS &&
                        task.dueDate < today

            val statusText: String
            val backgroundColor: Int
            val textColor: Int

            when {
                task.status == TaskStatus.COMPLETED -> {
                    statusText = binding.root.context.getString(
                        R.string.filter_completed
                    )
                    backgroundColor = "#DCE9DF".toColorInt()
                    textColor = "#245B45".toColorInt()
                }

                isOverdue -> {
                    statusText = binding.root.context.getString(
                        R.string.filter_overdue
                    )
                    backgroundColor = "#FCE8E6".toColorInt()
                    textColor = "#B3261E".toColorInt()
                }

                else -> {
                    statusText = binding.root.context.getString(
                        R.string.filter_in_progress
                    )
                    backgroundColor = "#E3EDF9".toColorInt()
                    textColor = "#285B8F".toColorInt()
                }
            }


            binding.tvTaskStatus.text = statusText
            binding.tvTaskStatus.setTextColor(textColor)

            val cornerRadius = 8 * binding.root.resources
                .displayMetrics.density

            binding.tvTaskStatus.background = GradientDrawable().apply {
                setColor(backgroundColor)
                this.cornerRadius = cornerRadius
            }

            val parsedDate = SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.US
            ).parse(task.dueDate)

            val displayDate = parsedDate?.let {
                SimpleDateFormat(
                    "dd MMM yyyy",
                    Locale.getDefault()
                ).format(it)
            } ?: task.dueDate

            binding.tvTaskDueDate.text = binding.root.context.getString(
                R.string.task_due_date_display,
                displayDate
            )

            binding.tvTaskDueDate.visibility = View.VISIBLE

            binding.cbTaskCompleted.setOnCheckedChangeListener(null)

            binding.cbTaskCompleted.isChecked =
                task.status == TaskStatus.COMPLETED

            binding.cbTaskCompleted.setOnCheckedChangeListener { _, isChecked ->
                onStatusChangeClick(task, isChecked)
            }

            binding.btnEditTask.setOnClickListener {
                onEditClick(task)
            }

            binding.btnDeleteTask.setOnClickListener {
                onDeleteClick(task)
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
