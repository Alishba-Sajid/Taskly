package com.example.taskly.presentation.calendar

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskly.R
import com.example.taskly.core.extention.collectLifecycleFlow
import com.example.taskly.core.extention.hide
import com.example.taskly.core.extention.show
import com.example.taskly.databinding.FragmentCalendarBinding
import com.example.taskly.domain.model.Task
import com.example.taskly.presentation.base.BaseFragment
import com.example.taskly.presentation.home.TaskAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class CalendarFragment : BaseFragment<FragmentCalendarBinding>(
    FragmentCalendarBinding::inflate
) {
    private val viewModel: CalendarViewModel by viewModels()

    private lateinit var taskAdapter: TaskAdapter

    override fun setupViews() {
        setupRecyclerView()
    }

    override fun setupListeners() {
        setupClickListeners()
        setupCalendar()
    }

    override fun observeData() {

        collectLifecycleFlow(viewModel.selectedDate) { selectedDate ->
            binding.tvSelectedDate.text = getString(
                R.string.tasks_for_date,
                formatDisplayDate(selectedDate)
            )
        }

        collectLifecycleFlow(viewModel.tasks) { tasks ->
            taskAdapter.submitList(tasks)
            renderTasks(tasks)
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            onEditClick = { task ->
                val bundle = Bundle().apply {
                    putInt("taskId", task.id)
                }

                findNavController().navigate(
                    R.id.action_calendarFragment_to_editTaskFragment,
                    bundle
                )
            },

            onDeleteClick = { task ->
                showDeleteConfirmation(task)
            },

            onStatusChangeClick = { task, isCompleted ->
                viewModel.updateTaskStatus(
                    task = task,
                    isCompleted = isCompleted
                )
            }
        )

        binding.rvCalendarTasks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = taskAdapter
        }
    }

    private fun setupCalendar() {
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->

            val selectedDate = String.format(
                Locale.US,
                "%04d-%02d-%02d",
                year,
                month + 1,
                dayOfMonth
            )

            viewModel.onDateSelected(selectedDate)
        }
    }

    private fun renderTasks(tasks: List<Task>) {
        binding.apply {
            if (tasks.isEmpty()) {
                tvEmptyTasks.show()
                rvCalendarTasks.hide()
            } else {
                tvEmptyTasks.hide()
                rvCalendarTasks.show()
            }
        }
    }

    private fun formatDisplayDate(date: String): String {
        val parsedDate = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        ).parse(date)

        return parsedDate?.let {
            SimpleDateFormat(
                "EEEE, dd MMM yyyy",
                Locale.getDefault()
            ).format(it)
        } ?: date
    }

    private fun showDeleteConfirmation(task: Task) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_task_title))
            .setMessage(getString(R.string.delete_task_message))
            .setNegativeButton(getString(R.string.cancel), null)
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                viewModel.deleteTask(task)
            }
            .show()
    }
}
