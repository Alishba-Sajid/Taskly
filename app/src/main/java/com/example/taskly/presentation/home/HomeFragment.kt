package com.example.taskly.presentation.home

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.toColorInt
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskly.R
import com.example.taskly.databinding.HomeFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.example.taskly.domain.model.Task
@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: HomeFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomeFragmentBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearch()
        setupFilters()

        binding.btnAddTask.setOnClickListener {
            findNavController().navigate(
                R.id.action_homeFragment_to_addTaskFragment
            )
        }

        binding.btnCalendar.setOnClickListener {
            findNavController().navigate(
                R.id.action_homeFragment_to_calendarFragment
            )
        }

        observeViewModel()
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
        binding.btnFilterAll.setOnClickListener {
            selectFilter(TaskFilter.ALL)
        }

        binding.btnFilterInProgress.setOnClickListener {
            selectFilter(TaskFilter.IN_PROGRESS)
        }

        binding.btnFilterCompleted.setOnClickListener {
            selectFilter(TaskFilter.COMPLETED)
        }

        binding.btnFilterOverdue.setOnClickListener {
            selectFilter(TaskFilter.OVERDUE)
        }
    }
    private fun selectFilter(filter: TaskFilter) {
        viewModel.onFilterSelected(filter)
    }
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.tasks.collect { tasks ->
                        taskAdapter.submitList(tasks)

                        renderTasks(
                            tasks = tasks,
                            isLoading = viewModel.isLoading.value
                        )
                    }
                }

                launch {
                    viewModel.isLoading.collect { isLoading ->
                        renderTasks(
                            tasks = viewModel.tasks.value,
                            isLoading = isLoading
                        )
                    }
                }

                launch {
                    viewModel.selectedFilter.collect { filter ->
                        updateFilterButtons(filter)
                    }
                }

                launch {
                    viewModel.searchQuery.collect { query ->
                        if (binding.etSearchTasks.text.toString() != query) {
                            binding.etSearchTasks.setText(query)
                        }
                    }
                }
            }
        }
    }
    private fun renderTasks(
        tasks: List<Task>,
        isLoading: Boolean
    ) {
        binding.progressHomeLoading.isVisible = isLoading

        binding.layoutEmptyState.isVisible =
            !isLoading && tasks.isEmpty()

        binding.rvTasks.isVisible =
            !isLoading && tasks.isNotEmpty()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
