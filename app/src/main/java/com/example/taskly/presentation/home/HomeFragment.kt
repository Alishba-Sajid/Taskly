package com.example.taskly.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskly.R
import com.example.taskly.databinding.HomeFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import android.content.res.ColorStateList
import androidx.core.graphics.toColorInt
import androidx.navigation.fragment.findNavController


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

        observeUiState()
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            onEditClick = {

            },
            onDeleteClick = { task ->

                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.delete_task_title))
                    .setMessage(getString(R.string.delete_task_message))
                    .setNegativeButton(
                        getString(R.string.cancel),
                        null
                    )
                    .setPositiveButton(
                        getString(R.string.delete)
                    ) { _, _ ->

                        viewModel.onEvent(
                            HomeEvent.DeleteTaskClicked(task)
                        )
                    }
                    .show()
            }
        )

        binding.rvTasks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = taskAdapter
        }
    }

    private fun setupSearch() {
        binding.etSearchTasks.doAfterTextChanged { text ->
            viewModel.onEvent(
                HomeEvent.SearchQueryChanged(text.toString())
            )
        }
    }

    private fun setupFilters() {
        binding.btnFilterAll.setOnClickListener {
            selectFilter(TaskFilter.ALL)
        }

        binding.btnFilterPending.setOnClickListener {
            selectFilter(TaskFilter.PENDING)
        }

        binding.btnFilterInProgress.setOnClickListener {
            selectFilter(TaskFilter.IN_PROGRESS)
        }

        binding.btnFilterCompleted.setOnClickListener {
            selectFilter(TaskFilter.COMPLETED)
        }
    }

    private fun selectFilter(filter: TaskFilter) {
        viewModel.onEvent(
            HomeEvent.FilterSelected(filter)
        )
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->

                    taskAdapter.submitList(state.tasks)

                    binding.layoutEmptyState.isVisible =
                        !state.isLoading && state.tasks.isEmpty()

                    binding.rvTasks.isVisible =
                        !state.isLoading && state.tasks.isNotEmpty()

                    updateFilterButtons(state.selectedFilter)
                }
            }
        }
    }

    private fun updateFilterButtons(selectedFilter: TaskFilter) {

        val buttons = mapOf(
            TaskFilter.ALL to binding.btnFilterAll,
            TaskFilter.PENDING to binding.btnFilterPending,
            TaskFilter.IN_PROGRESS to binding.btnFilterInProgress,
            TaskFilter.COMPLETED to binding.btnFilterCompleted
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
