package com.example.taskly.presentation.home

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.toColorInt
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskly.R
import com.example.taskly.core.extention.collectLifecycleFlow
import com.example.taskly.core.extention.hide
import com.example.taskly.core.extention.show
import com.example.taskly.databinding.HomeFragmentBinding
import com.example.taskly.domain.model.Task
import com.example.taskly.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<HomeFragmentBinding>(
    HomeFragmentBinding::inflate
) {
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var taskAdapter: TaskAdapter

    override fun setupViews() {
        setupRecyclerView()
    }

    override fun setupListeners() {
        binding.apply {
            btnAddTask.setOnClickListener {
                findNavController().navigate(
                    R.id.action_homeFragment_to_addTaskFragment
                )
            }

            btnCalendar.setOnClickListener {
                findNavController().navigate(
                    R.id.action_homeFragment_to_calendarFragment
                )
            }
        }

        setupSearch()
        setupFilters()
    }

    override fun observeData() {
        collectLifecycleFlow(viewModel.tasks) { tasks ->
            taskAdapter.submitList(tasks)

            renderTasks(
                tasks = tasks,
                isLoading = viewModel.isLoading.value
            )
        }

        collectLifecycleFlow(viewModel.isLoading) { isLoading ->
            renderTasks(
                tasks = viewModel.tasks.value,
                isLoading = isLoading
            )
        }

        collectLifecycleFlow(viewModel.selectedFilter) { filter ->
            updateFilterButtons(filter)
        }

        collectLifecycleFlow(viewModel.searchQuery) { query ->
            if (binding.etSearchTasks.text.toString() != query) {
                binding.etSearchTasks.setText(query)
            }
        }
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            onEditClick = { task ->
                val bundle = Bundle().apply {
                    putInt("taskId", task.id)
                }
                findNavController().navigate(
                    R.id.action_homeFragment_to_editTaskFragment,
                    bundle
                )
            },

            onDeleteClick = { task ->
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.delete_task_title))
                    .setMessage(getString(R.string.delete_task_message))
                    .setNegativeButton(getString(R.string.cancel), null)
                    .setPositiveButton(getString(R.string.delete)) { _, _ ->
                        viewModel.onDeleteTaskClicked(task)
                    }
                    .show()
            },

            onStatusChangeClick = { task, isCompleted ->
                viewModel.onTaskStatusChanged(task, isCompleted)
            }
        )

        binding.rvTasks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = taskAdapter
        }
    }

    private fun setupSearch() {
        binding.etSearchTasks.doAfterTextChanged { text ->
            viewModel.onSearchQueryChanged(text.toString())
        }
    }

    private fun setupFilters() {
        binding.apply {
            btnFilterAll.setOnClickListener {
                selectFilter(TaskFilter.ALL)
            }

            btnFilterInProgress.setOnClickListener {
                selectFilter(TaskFilter.IN_PROGRESS)
            }

            btnFilterCompleted.setOnClickListener {
                selectFilter(TaskFilter.COMPLETED)
            }

            btnFilterOverdue.setOnClickListener {
                selectFilter(TaskFilter.OVERDUE)
            }
        }
    }

    private fun selectFilter(filter: TaskFilter) {
        viewModel.onFilterSelected(filter)
    }

    private fun renderTasks(
        tasks: List<Task>,
        isLoading: Boolean
    ) {
        binding.apply {
            if (isLoading) {
                progressHomeLoading.show()
            } else {
                progressHomeLoading.hide()
            }

            if (!isLoading && tasks.isEmpty()) {
                layoutEmptyState.show()
            } else {
                layoutEmptyState.hide()
            }

            if (!isLoading && tasks.isNotEmpty()) {
                rvTasks.show()
            } else {
                rvTasks.hide()
            }
        }
    }

    private fun updateFilterButtons(selectedFilter: TaskFilter) {
        val buttons = mapOf(
            TaskFilter.ALL to binding.btnFilterAll,
            TaskFilter.IN_PROGRESS to binding.btnFilterInProgress,
            TaskFilter.COMPLETED to binding.btnFilterCompleted,
            TaskFilter.OVERDUE to binding.btnFilterOverdue
        )

        buttons.forEach { (filter, button) ->
            val isSelected = filter == selectedFilter

            val backgroundColor = if (isSelected) {
                "#245B45".toColorInt()
            } else {
                "#E9EDE7".toColorInt()
            }

            val textColor = if (isSelected) {
                "#FFFFFF".toColorInt()
            } else {
                "#687565".toColorInt()
            }

            button.backgroundTintList =
                ColorStateList.valueOf(backgroundColor)

            button.setTextColor(textColor)
        }
    }
}
